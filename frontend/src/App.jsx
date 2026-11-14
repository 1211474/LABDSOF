import { useEffect, useState } from 'react'
import { askNavigator, fetchCurrentUser, fetchServices, login } from './api'

const SERVICE_TYPES = [
  ['', 'Todos os serviços'],
  ['PHARMACY', 'Farmácia'],
  ['DAY_CENTRE', 'Centro de dia'],
  ['ADAPTED_TRANSPORT', 'Transporte adaptado'],
  ['HEALTH_CENTRE', 'Centro de saúde'],
  ['HOME_SUPPORT', 'Apoio ao domicílio'],
  ['OTHER', 'Outro']
]

const TYPE_LABELS = Object.fromEntries(
  SERVICE_TYPES.filter(([value]) => value).map(([value, label]) => [value, label])
)

const STATUS_LABELS = {
  OPEN: 'Aberto',
  CLOSED: 'Fechado',
  TEMPORARILY_CLOSED: 'Temporariamente fechado',
  UNKNOWN: 'Estado desconhecido'
}

const STATUS_STYLES = {
  OPEN: 'border-emerald-700 bg-emerald-100 text-emerald-950',
  CLOSED: 'border-red-700 bg-red-100 text-red-950',
  TEMPORARILY_CLOSED: 'border-amber-700 bg-amber-100 text-amber-950',
  UNKNOWN: 'border-slate-600 bg-slate-200 text-slate-950'
}

function formatDate(value) {
  if (!value) {
    return 'Data desconhecida'
  }

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return 'Data desconhecida'
  }

  return new Intl.DateTimeFormat('pt-PT', {
    dateStyle: 'short',
    timeStyle: 'short'
  }).format(date)
}

export default function App() {
  const [authUser, setAuthUser] = useState(null)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [authError, setAuthError] = useState('')
  const [authLoading, setAuthLoading] = useState(false)
  const [services, setServices] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [type, setType] = useState('')
  const [location, setLocation] = useState('')

  const [question, setQuestion] = useState('')
  const [assistantResult, setAssistantResult] = useState(null)
  const [assistantError, setAssistantError] = useState('')
  const [assistantLoading, setAssistantLoading] = useState(false)

  async function loadServices(filters = {}) {
    setLoading(true)
    setError('')

    try {
      const data = await fetchServices(filters)
      setServices(data)
    } catch {
      setError('Não foi possível carregar os serviços. Tente novamente.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void loadServices()
  }, [])

  useEffect(() => {
    const token = window.localStorage.getItem('carebnb.token')
    if (!token) {
      return
    }

    fetchCurrentUser(token)
      .then(setAuthUser)
      .catch(() => {
        window.localStorage.removeItem('carebnb.token')
      })
  }, [])

  async function handleLogin(event) {
    event.preventDefault()
    setAuthLoading(true)
    setAuthError('')

    try {
      const result = await login(email.trim(), password)
      window.localStorage.setItem('carebnb.token', result.token)
      setAuthUser({ email: email.trim(), role: result.role, permissions: result.permissions ?? [] })
      setPassword('')
    } catch (requestError) {
      setAuthError(requestError.message)
    } finally {
      setAuthLoading(false)
    }
  }

  function handleLogout() {
    window.localStorage.removeItem('carebnb.token')
    setAuthUser(null)
  }

  function handleSearch(event) {
    event.preventDefault()
    void loadServices({ type, coarseLocation: location })
  }

  async function handleAsk(event) {
    event.preventDefault()
    const trimmedQuestion = question.trim()

    if (!trimmedQuestion) {
      return
    }

    setAssistantLoading(true)
    setAssistantError('')
    setAssistantResult(null)

    try {
      const result = await askNavigator(trimmedQuestion)
      setAssistantResult(result)
    } catch (requestError) {
      setAssistantError(requestError.message)
    } finally {
      setAssistantLoading(false)
    }
  }

  return (
    <main className="mx-auto min-h-screen max-w-4xl px-4 py-8 sm:px-6">
      <header className="mb-8">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div>
            <h1 className="mt-2 text-3xl font-black leading-tight sm:text-4xl">CareBnB</h1>
            <p className="mt-3 max-w-2xl text-slate-700">
              Encontre serviços locais de apoio e confirme sempre a disponibilidade por telefone.
            </p>
          </div>
          {authUser ? (
            <div className="rounded-xl border-2 border-emerald-700 bg-emerald-50 p-3 text-right">
              <p className="font-semibold">{authUser.email}</p>
              <p className="text-sm">{authUser.role === 'CAREGIVER' ? 'Cuidador informal' : 'Prestador'}</p>
              <button className="mt-2 font-bold underline" type="button" onClick={handleLogout}>
                Terminar sessão
              </button>
            </div>
          ) : null}
        </div>
      </header>

      {!authUser ? (
        <section className="mb-8 rounded-2xl border-2 border-sky-800 bg-sky-50 p-5" aria-labelledby="login-title">
          <h2 id="login-title" className="text-2xl font-bold">Iniciar sessão</h2>
          <p className="mt-2 text-slate-700">
            Entre como cuidador ou prestador para aceder às funcionalidades protegidas.
          </p>
          <form className="mt-4 grid gap-4 sm:grid-cols-[1fr_1fr_auto]" onSubmit={handleLogin}>
            <label>
              <span className="block font-semibold">Email</span>
              <input
                className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                type="email"
                autoComplete="email"
                required
              />
            </label>
            <label>
              <span className="block font-semibold">Palavra-passe</span>
              <input
                className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                type="password"
                autoComplete="current-password"
                required
              />
            </label>
            <button
              className="min-h-12 self-end rounded-xl bg-sky-800 px-6 py-3 text-lg font-bold text-white hover:bg-sky-900 disabled:opacity-60"
              type="submit"
              disabled={authLoading}
            >
              {authLoading ? 'A entrar...' : 'Entrar'}
            </button>
          </form>
          {authError ? <p className="mt-4 font-semibold text-red-950" role="alert">{authError}</p> : null}
        </section>
      ) : null}

      <section className="mb-8 rounded-2xl border-2 border-sky-800 bg-sky-50 p-5" aria-labelledby="emergency-title">
        <h2 id="emergency-title" className="text-xl font-bold">
          Em emergência
        </h2>
        <p className="mt-2 text-slate-800">
          Ligue <a className="font-bold underline" href="tel:112">112</a>. Para aconselhamento de saúde,
          ligue <a className="font-bold underline" href="tel:808242424">SNS 24 (808 24 24 24)</a>.
        </p>
      </section>

      {authUser?.role !== 'PROVIDER' ? (
      <section className="mb-8 rounded-2xl border-2 border-slate-300 bg-white p-5" aria-labelledby="assistant-title">
        <h2 id="assistant-title" className="text-2xl font-bold">
          Peça ajuda em linguagem simples
        </h2>
        <p className="mt-2 text-slate-700">
          Exemplo: “farmácias perto da Boavista”. Nesta fase, a pesquisa usa apenas o diretório verificado.
        </p>

        <form className="mt-4 flex flex-col gap-3 sm:flex-row" onSubmit={handleAsk}>
          <label className="flex-1">
            <span className="block font-semibold">Qual é a sua necessidade?</span>
            <input
              className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
              value={question}
              onChange={(event) => setQuestion(event.target.value)}
              placeholder="Ex.: farmácias perto da Boavista"
              type="text"
            />
          </label>
          <button
            className="min-h-12 self-end rounded-xl bg-sky-800 px-6 py-3 text-lg font-bold text-white hover:bg-sky-900 disabled:opacity-60"
            type="submit"
            disabled={assistantLoading}
          >
            {assistantLoading ? 'A procurar...' : 'Procurar ajuda'}
          </button>
        </form>

        {assistantError ? (
          <p className="mt-4 rounded-xl border-2 border-red-700 bg-red-50 p-4 font-semibold text-red-950" role="alert">
            {assistantError} Use a pesquisa de serviços abaixo.
          </p>
        ) : null}

        {assistantResult ? (
          <div className="mt-4 rounded-xl border-2 border-slate-300 bg-slate-50 p-4" aria-live="polite">
            <p className="font-semibold">{assistantResult.answer}</p>
            {assistantResult.isFallback ? (
              <p className="mt-2 text-sm font-semibold text-amber-900">
                Modo de contingência: a pergunta foi interpretada por palavras-chave sobre os dados do diretório.
              </p>
            ) : null}
            {assistantResult.services?.length ? (
              <ul className="mt-3 space-y-2">
                {assistantResult.services.map((service) => (
                  <li key={service.id} className="rounded-lg border border-slate-300 bg-white p-3">
                    <span className="font-bold">{service.name}</span>
                    <span className="block text-slate-700">
                      {TYPE_LABELS[service.type] ?? service.type} · {service.coarseLocation} ·{' '}
                      {STATUS_LABELS[service.currentStatus] ?? service.currentStatus}
                    </span>
                  </li>
                ))}
              </ul>
            ) : null}
            {assistantResult.limitations?.length ? (
              <ul className="mt-3 list-disc space-y-1 pl-5 text-sm text-slate-700">
                {assistantResult.limitations.map((limitation) => (
                  <li key={limitation}>{limitation}</li>
                ))}
              </ul>
            ) : null}
          </div>
        ) : null}
      </section>
      ) : (
        <section className="mb-8 rounded-2xl border-2 border-amber-700 bg-amber-50 p-5" aria-labelledby="provider-access-title">
          <h2 id="provider-access-title" className="text-xl font-bold">Área de prestador</h2>
          <p className="mt-2 text-slate-800">
            A pesquisa pública de serviços está disponível. As ferramentas de atualização de estado do prestador
            serão disponibilizadas nesta área quando estiverem configuradas.
          </p>
        </section>
      )}

      <section className="mb-8" aria-labelledby="search-title">
        <h2 id="search-title" className="text-2xl font-bold">
          Pesquisar serviços de apoio
        </h2>

        <form className="mt-4 grid gap-4 rounded-2xl border-2 border-slate-300 bg-white p-5 sm:grid-cols-[1fr_1fr_auto]" onSubmit={handleSearch}>
          <label>
            <span className="block font-semibold">Tipo de serviço</span>
            <select
              className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
              value={type}
              onChange={(event) => setType(event.target.value)}
            >
              {SERVICE_TYPES.map(([value, label]) => (
                <option key={value || 'ALL'} value={value}>
                  {label}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span className="block font-semibold">Zona</span>
            <input
              className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
              value={location}
              onChange={(event) => setLocation(event.target.value)}
              placeholder="Ex.: Boavista"
              type="text"
            />
          </label>

          <button
            className="min-h-12 self-end rounded-xl bg-sky-800 px-6 py-3 text-lg font-bold text-white hover:bg-sky-900"
            type="submit"
          >
            Pesquisar
          </button>
        </form>
      </section>

      <section aria-labelledby="results-title" aria-live="polite">
        <div className="mb-4 flex flex-wrap items-end justify-between gap-2">
          <h2 id="results-title" className="text-2xl font-bold">
            Serviços encontrados
          </h2>
          {!loading && !error ? (
            <p className="font-semibold text-slate-700">{services.length} resultado(s)</p>
          ) : null}
        </div>

        {loading ? (
          <p className="rounded-xl border-2 border-slate-300 bg-white p-5" role="status">
            A carregar serviços...
          </p>
        ) : null}

        {error ? (
          <p className="rounded-xl border-2 border-red-700 bg-red-50 p-5 font-semibold text-red-950" role="alert">
            {error}
          </p>
        ) : null}

        {!loading && !error && services.length === 0 ? (
          <p className="rounded-xl border-2 border-slate-300 bg-white p-5">
            Não encontrámos serviços com estes filtros. Tente outra zona ou tipo de serviço.
          </p>
        ) : null}

        <ul className="space-y-4">
          {services.map((service) => (
            <li key={service.id}>
              <article className="rounded-2xl border-2 border-slate-300 bg-white p-5 shadow-sm">
                <div className="flex flex-wrap items-start justify-between gap-3">
                  <div>
                    <h3 className="text-xl font-bold">{service.name}</h3>
                    <p className="text-slate-700">
                      {TYPE_LABELS[service.type] ?? service.type} · {service.coarseLocation}
                    </p>
                  </div>
                  <span
                    className={`inline-block rounded-full border-2 px-4 py-1 text-sm font-bold ${
                      STATUS_STYLES[service.currentStatus] ?? STATUS_STYLES.UNKNOWN
                    }`}
                  >
                    {STATUS_LABELS[service.currentStatus] ?? service.currentStatus}
                  </span>
                </div>

                <dl className="mt-4 grid gap-2 sm:grid-cols-2">
                  <div>
                    <dt className="font-semibold">Horário</dt>
                    <dd>{service.operatingHours}</dd>
                  </div>
                  <div>
                    <dt className="font-semibold">Última atualização</dt>
                    <dd>{formatDate(service.lastUpdated)}</dd>
                  </div>
                </dl>

                <div className="mt-4 flex flex-wrap items-center justify-between gap-3">
                  <span
                    className={`rounded-lg border-2 px-3 py-1 text-sm font-bold ${
                      service.verified
                        ? 'border-emerald-700 bg-emerald-50 text-emerald-950'
                        : 'border-amber-700 bg-amber-50 text-amber-950'
                    }`}
                  >
                    {service.verified ? 'Fonte oficial verificada' : 'Fonte comunitária (não verificada)'}
                  </span>

                  <a
                    className="inline-flex min-h-12 items-center justify-center rounded-xl bg-emerald-800 px-6 py-3 text-lg font-bold text-white hover:bg-emerald-900"
                    href={`tel:${service.contactPhone}`}
                    aria-label={`Ligar para ${service.name}`}
                  >
                    Ligar agora
                  </a>
                </div>
              </article>
            </li>
          ))}
        </ul>

        <p className="mt-6 rounded-xl border-2 border-amber-700 bg-amber-50 p-4 font-semibold text-amber-950">
          A disponibilidade em tempo real não é garantida. Confirme sempre diretamente por telefone.
        </p>
      </section>
    </main>
  )
}
