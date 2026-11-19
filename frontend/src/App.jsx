import { useEffect, useState } from 'react'
import {
  addCareRecipient,
  askNavigator,
  createCaregiverProfile,
  fetchCaregiverProfile,
  fetchCurrentUser,
  fetchServices,
  login,
  removeCareRecipient,
  updateCareRecipient,
  updateCaregiverProfile
} from './api'

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

const ACCESSIBILITY_TAGS = [
  ['wheelchair_accessible', 'Acesso para cadeira de rodas'],
  ['hearing_assistance', 'Apoio auditivo'],
  ['visual_assistance', 'Apoio visual'],
  ['mobility_assistance', 'Apoio à mobilidade'],
  ['cognitive_assistance', 'Apoio cognitivo']
]

const ACCESSIBILITY_TAG_LABELS = Object.fromEntries(ACCESSIBILITY_TAGS)

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
  const [careProfile, setCareProfile] = useState(null)
  const [profileName, setProfileName] = useState('')
  const [profilePhone, setProfilePhone] = useState('')
  const [profileLocation, setProfileLocation] = useState('')
  const [profileError, setProfileError] = useState('')
  const [profileSuccess, setProfileSuccess] = useState('')
  const [profileLoading, setProfileLoading] = useState(false)
  const [recipientName, setRecipientName] = useState('')
  const [recipientTags, setRecipientTags] = useState([])
  const [editingRecipientId, setEditingRecipientId] = useState(null)
  const [recipientError, setRecipientError] = useState('')
  const [recipientSuccess, setRecipientSuccess] = useState('')
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
      .then((user) => {
        setAuthUser(user)
        if (user.role === 'CAREGIVER') void loadCareProfile()
      })
      .catch(() => {
        window.localStorage.removeItem('carebnb.token')
      })
  }, [])

  async function loadCareProfile() {
    try {
      const profile = await fetchCaregiverProfile()
      setCareProfile(profile)
      setProfileName(profile.displayName)
      setProfilePhone(profile.phoneNumber)
      setProfileLocation(profile.coarseLocation)
    } catch (requestError) {
      if (!requestError.message.includes('não encontrado')) {
        setProfileError(requestError.message)
      }
    }
  }

  async function handleLogin(event) {
    event.preventDefault()
    setAuthLoading(true)
    setAuthError('')

    try {
      const result = await login(email.trim(), password)
      window.localStorage.setItem('carebnb.token', result.token)
      setAuthUser({ email: email.trim(), role: result.role, permissions: result.permissions ?? [] })
      if (result.role === 'CAREGIVER') void loadCareProfile()
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
    setCareProfile(null)
    setProfileName('')
    setProfilePhone('')
    setProfileLocation('')
    setProfileError('')
    setProfileSuccess('')
  }

  function toggleRecipientTag(tag) {
    setRecipientTags((current) => current.includes(tag)
      ? current.filter((value) => value !== tag)
      : [...current, tag])
  }

  function startEditingRecipient(recipient) {
    setEditingRecipientId(recipient.id)
    setRecipientName(recipient.displayName)
    setRecipientTags(recipient.coarseAccessibilityTags)
    setRecipientError('')
    setRecipientSuccess('')
  }

  function resetRecipientForm() {
    setEditingRecipientId(null)
    setRecipientName('')
    setRecipientTags([])
  }

  async function handleProfileSubmit(event) {
    event.preventDefault()
    setProfileLoading(true)
    setProfileError('')
    setProfileSuccess('')

    try {
      const profile = careProfile
        ? await updateCaregiverProfile(careProfile.id, {
          displayName: profileName,
          phoneNumber: profilePhone,
          coarseLocation: profileLocation
        })
        : await createCaregiverProfile({
          displayName: profileName,
          phoneNumber: profilePhone,
          coarseLocation: profileLocation
        })
      setCareProfile(profile)
      setProfileName(profile.displayName)
      setProfilePhone(profile.phoneNumber)
      setProfileLocation(profile.coarseLocation)
      setProfileSuccess('Perfil guardado com sucesso.')
    } catch (requestError) {
      setProfileError(requestError.message)
    } finally {
      setProfileLoading(false)
    }
  }

  async function handleRecipientSubmit(event) {
    event.preventDefault()
    setRecipientError('')
    setRecipientSuccess('')

    try {
      const request = {
        displayName: recipientName.trim(),
        coarseAccessibilityTags: recipientTags
      }
      const recipient = editingRecipientId
        ? await updateCareRecipient(careProfile.id, editingRecipientId, request)
        : await addCareRecipient(careProfile.id, request)
      setCareProfile((current) => ({
        ...current,
        recipients: editingRecipientId
          ? current.recipients.map((item) => item.id === recipient.id ? recipient : item)
          : [...(current.recipients ?? []), recipient]
      }))
      resetRecipientForm()
      setRecipientSuccess(editingRecipientId ? 'Pessoa acompanhada atualizada.' : 'Pessoa acompanhada adicionada.')
    } catch (requestError) {
      setRecipientError(requestError.message)
    }
  }

  async function handleRemoveRecipient(recipientId) {
    if (!window.confirm('Remover esta pessoa acompanhada do perfil?')) return
    setRecipientError('')
    setRecipientSuccess('')
    try {
      await removeCareRecipient(careProfile.id, recipientId)
      setCareProfile((current) => ({
        ...current,
        recipients: current.recipients.filter((item) => item.id !== recipientId)
      }))
      if (editingRecipientId === recipientId) resetRecipientForm()
      setRecipientSuccess('Pessoa acompanhada removida.')
    } catch (requestError) {
      setRecipientError(requestError.message)
    }
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

      {authUser?.role === 'CAREGIVER' ? (
        <section className="mb-8 rounded-2xl border-2 border-emerald-800 bg-emerald-50 p-5" aria-labelledby="profile-title">
          <h2 id="profile-title" className="text-2xl font-bold">Perfil do cuidador</h2>
          <p className="mt-2 text-slate-800">
            Guarde apenas informação operacional. Não introduza diagnósticos, notas médicas ou documentos.
          </p>

          <form className="mt-4 grid gap-4 sm:grid-cols-2" onSubmit={handleProfileSubmit}>
            <label>
              <span className="block font-semibold">Nome de apresentação</span>
              <input
                className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
                value={profileName}
                onChange={(event) => setProfileName(event.target.value)}
                maxLength={120}
                required
              />
            </label>
            <label>
              <span className="block font-semibold">Telefone de contacto</span>
              <input
                className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
                value={profilePhone}
                onChange={(event) => setProfilePhone(event.target.value)}
                placeholder="+351 912 345 678"
                type="tel"
                required
              />
            </label>
            <label>
              <span className="block font-semibold">Zona aproximada</span>
              <input
                className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
                value={profileLocation}
                onChange={(event) => setProfileLocation(event.target.value)}
                placeholder="Ex.: Boavista"
                maxLength={120}
                required
              />
              <span className="mt-1 block text-sm text-slate-700">Indique apenas bairro ou paróquia, não a morada.</span>
            </label>
            <button
              className="min-h-12 self-end rounded-xl bg-emerald-800 px-6 py-3 text-lg font-bold text-white hover:bg-emerald-900 disabled:opacity-60 sm:col-span-2"
              type="submit"
              disabled={profileLoading}
            >
              {profileLoading ? 'A guardar...' : careProfile ? 'Atualizar perfil' : 'Criar perfil'}
            </button>
          </form>

          {profileError ? <p className="mt-4 font-semibold text-red-950" role="alert">{profileError}</p> : null}
          {profileSuccess ? <p className="mt-4 font-semibold text-emerald-950" role="status">{profileSuccess}</p> : null}

          {careProfile ? (
            <form className="mt-6 border-t-2 border-emerald-200 pt-5" onSubmit={handleRecipientSubmit}>
              <h3 className="text-xl font-bold">
                {editingRecipientId ? 'Editar pessoa acompanhada' : 'Adicionar pessoa acompanhada'}
              </h3>
              <p className="mt-2 text-slate-800">
                Use um nome curto para a identificar. Guarde apenas necessidades operacionais, sem dados clínicos.
              </p>
              <label className="mt-4 block">
                <span className="block font-semibold">Nome</span>
                <input
                  className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
                  value={recipientName}
                  onChange={(event) => setRecipientName(event.target.value)}
                  maxLength={120}
                  required
                />
              </label>
              <fieldset className="mt-4">
                <legend className="font-semibold">Tags de acessibilidade</legend>
                <div className="mt-2 grid gap-2 sm:grid-cols-2">
                  {ACCESSIBILITY_TAGS.map(([tag, label]) => (
                    <label key={tag} className="flex min-h-12 items-center gap-3 rounded-xl border-2 border-slate-400 bg-white px-4 py-2">
                      <input
                        className="h-5 w-5"
                        type="checkbox"
                        checked={recipientTags.includes(tag)}
                        onChange={() => toggleRecipientTag(tag)}
                      />
                      <span>{label}</span>
                    </label>
                  ))}
                </div>
              </fieldset>
              <button
                className="mt-4 min-h-12 rounded-xl bg-sky-800 px-6 py-3 text-lg font-bold text-white hover:bg-sky-900"
                type="submit"
              >
                {editingRecipientId ? 'Guardar alterações' : 'Adicionar pessoa'}
              </button>
              {editingRecipientId ? (
                <button
                  className="ml-3 min-h-12 rounded-xl border-2 border-slate-600 px-6 py-3 text-lg font-bold hover:bg-white"
                  type="button"
                  onClick={resetRecipientForm}
                >
                  Cancelar
                </button>
              ) : null}
              {recipientError ? <p className="mt-3 font-semibold text-red-950" role="alert">{recipientError}</p> : null}
              {recipientSuccess ? <p className="mt-3 font-semibold text-emerald-950" role="status">{recipientSuccess}</p> : null}
            </form>
          ) : null}

          {careProfile?.recipients?.length ? (
            <div className="mt-5">
              <h3 className="font-bold">Destinatários configurados</h3>
              <ul className="mt-2 space-y-2">
                {careProfile.recipients.map((recipient) => (
                  <li key={recipient.id} className="rounded-xl border-2 border-emerald-200 bg-white p-3">
                    <div className="flex flex-wrap items-start justify-between gap-3">
                      <div>
                        <p className="font-bold">{recipient.displayName}</p>
                        <p className="text-sm text-slate-700">
                          {recipient.coarseAccessibilityTags.length
                            ? recipient.coarseAccessibilityTags.map((tag) => ACCESSIBILITY_TAG_LABELS[tag] ?? tag).join(', ')
                            : 'Sem tags de acessibilidade'}
                        </p>
                      </div>
                      <div className="flex gap-2">
                        <button
                          className="rounded-lg border-2 border-sky-700 px-3 py-2 font-bold text-sky-900"
                          type="button"
                          onClick={() => startEditingRecipient(recipient)}
                        >
                          Editar
                        </button>
                        <button
                          className="rounded-lg border-2 border-red-700 px-3 py-2 font-bold text-red-900"
                          type="button"
                          onClick={() => void handleRemoveRecipient(recipient.id)}
                        >
                          Remover
                        </button>
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          ) : null}
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
            {assistantResult.sourceContext ? (
              <p className="mt-2 text-sm text-slate-700" data-testid="assistant-source-context">
                Fonte: {assistantResult.sourceContext.source} ·{' '}
                {assistantResult.sourceContext.recordCount} registo(s) verificado(s) · Consultado em{' '}
                {new Date(assistantResult.sourceContext.retrievedAt).toLocaleString('pt-PT')}
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
                    {service.lastUpdated ? (
                      <span className="block text-sm text-slate-600">
                        Atualizado em {new Date(service.lastUpdated).toLocaleDateString('pt-PT')}
                      </span>
                    ) : null}
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
