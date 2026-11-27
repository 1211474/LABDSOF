import { useEffect, useState } from 'react'
import {
  addCareRecipient,
  addEmergencyContact,
  assignProviderOrganisation,
  askNavigator,
  createCaregiverProfile,
  fetchCaregiverProfile,
  fetchAdminOrganisations,
  fetchAdminProviders,
  fetchCurrentUser,
  fetchServiceDetails,
  fetchServices,
  fetchProviderServices,
  login,
  removeCareRecipient,
  removeEmergencyContact,
  setOrganisationVerification,
  updateCareRecipient,
  updateCaregiverProfile,
  updateEmergencyContact,
  updateOrganisationDetails,
  updateProviderServiceStatus
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

const ROLE_LABELS = {
  CAREGIVER: 'Cuidador informal',
  PROVIDER: 'Prestador',
  PLATFORM_ADMIN: 'Administrador da plataforma'
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

function ServiceContact({ service }) {
  const phone = typeof service.contactPhone === 'string' ? service.contactPhone.trim() : ''

  return (
    <div className="mt-4">
      {phone ? (
        <a
          className="inline-flex min-h-12 items-center justify-center rounded-xl bg-emerald-800 px-6 py-3 text-lg font-bold text-white hover:bg-emerald-900"
          href={`tel:${phone}`}
          aria-label={`Ligar para ${service.name}`}
        >
          Ligar agora
        </a>
      ) : (
        <p className="font-semibold text-slate-700">Contacto telefónico indisponível.</p>
      )}
      <p className="mt-2 text-sm font-semibold text-amber-950">
        A disponibilidade em tempo real não é garantida. Confirme sempre diretamente por telefone.
      </p>
    </div>
  )
}

function OrganisationPublicDetails({ service }) {
  const hasDetails = service.organisationEmail
    || service.organisationPhone
    || service.organisationWebsite
    || service.organisationCoarseLocation

  if (!hasDetails) return null

  return (
    <dl className="mt-4 grid gap-2 sm:grid-cols-2" data-testid="organisation-public-details">
      {service.organisationCoarseLocation ? (
        <div>
          <dt className="font-semibold">Zona da organização</dt>
          <dd>{service.organisationCoarseLocation}</dd>
        </div>
      ) : null}
      {service.organisationEmail ? (
        <div>
          <dt className="font-semibold">Email da organização</dt>
          <dd>
            <a className="break-all underline" href={`mailto:${service.organisationEmail}`}>
              {service.organisationEmail}
            </a>
          </dd>
        </div>
      ) : null}
      {service.organisationPhone ? (
        <div>
          <dt className="font-semibold">Telefone da organização</dt>
          <dd>
            <a className="underline" href={`tel:${service.organisationPhone}`}>
              {service.organisationPhone}
            </a>
          </dd>
        </div>
      ) : null}
      {service.organisationWebsite ? (
        <div>
          <dt className="font-semibold">Website</dt>
          <dd>
            <a
              className="break-all underline"
              href={service.organisationWebsite}
              rel="noreferrer"
              target="_blank"
            >
              Visitar site da organização
            </a>
          </dd>
        </div>
      ) : null}
    </dl>
  )
}

function publicOrganisationFields(organisation) {
  return {
    organisationEmail: organisation.verified ? organisation.officialEmail : null,
    organisationPhone: organisation.verified ? organisation.officialPhone : null,
    organisationWebsite: organisation.verified ? organisation.website : null,
    organisationCoarseLocation: organisation.verified ? organisation.coarseLocation : null
  }
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
  const [contactName, setContactName] = useState('')
  const [contactPhone, setContactPhone] = useState('')
  const [contactRelationship, setContactRelationship] = useState('')
  const [editingContactId, setEditingContactId] = useState(null)
  const [contactError, setContactError] = useState('')
  const [contactSuccess, setContactSuccess] = useState('')
  const [services, setServices] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [serviceDetail, setServiceDetail] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const [detailError, setDetailError] = useState('')
  const [verificationLoadingId, setVerificationLoadingId] = useState(null)
  const [verificationError, setVerificationError] = useState('')
  const [verificationMessage, setVerificationMessage] = useState('')
  const [adminOrganisations, setAdminOrganisations] = useState(null)
  const [adminOrganisationsError, setAdminOrganisationsError] = useState('')
  const [adminProviders, setAdminProviders] = useState(null)
  const [adminProvidersError, setAdminProvidersError] = useState('')
  const [providerOrganisationDrafts, setProviderOrganisationDrafts] = useState({})
  const [providerAssignmentSavingId, setProviderAssignmentSavingId] = useState(null)
  const [providerAssignmentMessage, setProviderAssignmentMessage] = useState('')
  const [providerAssignmentError, setProviderAssignmentError] = useState('')
  const [organisationDrafts, setOrganisationDrafts] = useState({})
  const [detailsSavingId, setDetailsSavingId] = useState(null)
  const [detailsError, setDetailsError] = useState('')
  const [detailsMessage, setDetailsMessage] = useState('')
  const [providerServices, setProviderServices] = useState(null)
  const [providerServicesError, setProviderServicesError] = useState('')
  const [providerSavingId, setProviderSavingId] = useState(null)
  const [providerStatusMessage, setProviderStatusMessage] = useState('')

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

  useEffect(() => {
    if (authUser?.role !== 'PLATFORM_ADMIN') {
      setAdminOrganisations([])
      setAdminOrganisationsError('')
      return
    }

    let active = true
    setAdminOrganisations(null)
    setAdminOrganisationsError('')
    fetchAdminOrganisations()
      .then((organisations) => {
        if (active) setAdminOrganisations(organisations)
      })
      .catch((requestError) => {
        if (active) {
          setAdminOrganisations([])
          setAdminOrganisationsError(requestError.message)
        }
      })

    return () => {
      active = false
    }
  }, [authUser?.role])

  useEffect(() => {
    if (authUser?.role !== 'PLATFORM_ADMIN') {
      setAdminProviders([])
      setAdminProvidersError('')
      setProviderOrganisationDrafts({})
      return
    }

    let active = true
    setAdminProviders(null)
    setAdminProvidersError('')
    fetchAdminProviders()
      .then((providers) => {
        if (!active) return
        setAdminProviders(providers)
        setProviderOrganisationDrafts(Object.fromEntries(
          providers.map((provider) => [provider.id, provider.organisationId ?? ''])
        ))
      })
      .catch((requestError) => {
        if (active) {
          setAdminProviders([])
          setAdminProvidersError(requestError.message)
        }
      })

    return () => {
      active = false
    }
  }, [authUser?.role])

  useEffect(() => {
    if (authUser?.role !== 'PROVIDER') {
      setProviderServices(null)
      setProviderServicesError('')
      return
    }

    let active = true
    setProviderServices(null)
    setProviderServicesError('')
    fetchProviderServices()
      .then((providerServiceList) => {
        if (active) setProviderServices(providerServiceList)
      })
      .catch((requestError) => {
        if (active) setProviderServicesError(requestError.message)
      })

    return () => {
      active = false
    }
  }, [authUser?.role])

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
    setAdminOrganisations([])
    setOrganisationDrafts({})
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

  function startEditingContact(contact) {
    setEditingContactId(contact.id)
    setContactName(contact.displayName)
    setContactPhone(contact.phoneNumber)
    setContactRelationship(contact.relationship ?? '')
    setContactError('')
    setContactSuccess('')
  }

  function resetContactForm() {
    setEditingContactId(null)
    setContactName('')
    setContactPhone('')
    setContactRelationship('')
  }

  async function handleContactSubmit(event) {
    event.preventDefault()
    setContactError('')
    setContactSuccess('')

    try {
      const request = {
        displayName: contactName.trim(),
        phoneNumber: contactPhone.trim(),
        relationship: contactRelationship.trim()
      }
      const contact = editingContactId
        ? await updateEmergencyContact(careProfile.id, editingContactId, request)
        : await addEmergencyContact(careProfile.id, request)
      setCareProfile((current) => ({
        ...current,
        emergencyContacts: editingContactId
          ? current.emergencyContacts.map((item) => item.id === contact.id ? contact : item)
          : [...(current.emergencyContacts ?? []), contact]
      }))
      resetContactForm()
      setContactSuccess(editingContactId ? 'Contacto de emergência atualizado.' : 'Contacto de emergência adicionado.')
    } catch (requestError) {
      setContactError(requestError.message)
    }
  }

  async function handleRemoveContact(contactId) {
    if (!window.confirm('Remover este contacto de emergência?')) return
    setContactError('')
    setContactSuccess('')
    try {
      await removeEmergencyContact(careProfile.id, contactId)
      setCareProfile((current) => ({
        ...current,
        emergencyContacts: current.emergencyContacts.filter((item) => item.id !== contactId)
      }))
      if (editingContactId === contactId) resetContactForm()
      setContactSuccess('Contacto de emergência removido.')
    } catch (requestError) {
      setContactError(requestError.message)
    }
  }

  function handleSearch(event) {
    event.preventDefault()
    void loadServices({ type, coarseLocation: location })
  }

  async function handleViewDetails(serviceId) {
    setDetailLoading(true)
    setDetailError('')
    setServiceDetail(null)

    try {
      const detail = await fetchServiceDetails(serviceId)
      setServiceDetail(detail)
    } catch (requestError) {
      setDetailError(requestError.message)
    } finally {
      setDetailLoading(false)
    }
  }

  async function handleProviderStatusSave(event, service) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    setProviderSavingId(service.id)
    setProviderStatusMessage('')
    setProviderServicesError('')

    try {
      const updated = await updateProviderServiceStatus(service.id, {
        status: form.get('status'),
        operatingHours: form.get('operatingHours').trim(),
        officialNotice: form.get('officialNotice').trim()
      })
      setProviderServices((current) => current.map((item) => (
        item.id === updated.id ? updated : item
      )))
      setServices((current) => current.map((item) => (
        item.id === updated.id ? updated : item
      )))
      setServiceDetail((current) => current?.id === updated.id ? updated : current)
      setProviderStatusMessage(`${updated.name}: estado oficial guardado.`)
    } catch (requestError) {
      setProviderServicesError(requestError.message)
    } finally {
      setProviderSavingId(null)
    }
  }

  async function handleVerificationChange(organisation) {
    setVerificationLoadingId(organisation.id)
    setVerificationError('')
    setVerificationMessage('')

    try {
      const updatedOrganisation = await setOrganisationVerification(
        organisation.id,
        !organisation.verified
      )
      setAdminOrganisations((current) => current.map((item) => (
        item.id === updatedOrganisation.id ? updatedOrganisation : item
      )))
      setServices((current) => current.map((service) => (
        service.organisationId === updatedOrganisation.id
          ? {
            ...service,
            verified: updatedOrganisation.verified,
            ...publicOrganisationFields(updatedOrganisation)
          }
          : service
      )))
      setServiceDetail((current) => (
        current?.organisationId === updatedOrganisation.id
          ? {
            ...current,
            verified: updatedOrganisation.verified,
            ...publicOrganisationFields(updatedOrganisation)
          }
          : current
      ))
      setVerificationMessage(
        updatedOrganisation.verified
          ? `${updatedOrganisation.name} foi verificada.`
          : `A verificação de ${updatedOrganisation.name} foi removida.`
      )
    } catch (requestError) {
      setVerificationError(requestError.message)
    } finally {
      setVerificationLoadingId(null)
    }
  }

  async function handleProviderAssignmentSubmit(event, provider) {
    event.preventDefault()
    const organisationId = providerOrganisationDrafts[provider.id]
    if (!organisationId) {
      setProviderAssignmentError('Selecione uma organização.')
      return
    }

    setProviderAssignmentSavingId(provider.id)
    setProviderAssignmentMessage('')
    setProviderAssignmentError('')

    try {
      const updatedProvider = await assignProviderOrganisation(provider.id, organisationId)
      setAdminProviders((current) => current.map((item) => (
        item.id === updatedProvider.id ? updatedProvider : item
      )))
      setProviderOrganisationDrafts((current) => ({
        ...current,
        [updatedProvider.id]: updatedProvider.organisationId
      }))
      setProviderAssignmentMessage(
        `${updatedProvider.email} foi associado a ${updatedProvider.organisationName}.`
      )
    } catch (requestError) {
      setProviderAssignmentError(requestError.message)
    } finally {
      setProviderAssignmentSavingId(null)
    }
  }

  function handleOrganisationFieldChange(organisation, field, value) {
    setOrganisationDrafts((current) => ({
      ...current,
      [organisation.id]: {
        ...(current[organisation.id] ?? {
          officialEmail: organisation.officialEmail ?? '',
          officialPhone: organisation.officialPhone ?? '',
          website: organisation.website ?? '',
          coarseLocation: organisation.coarseLocation ?? ''
        }),
        [field]: value
      }
    }))
  }

  async function handleOrganisationDetailsSubmit(event, organisation) {
    event.preventDefault()
    setDetailsSavingId(organisation.id)
    setDetailsError('')
    setDetailsMessage('')

    const details = organisationDrafts[organisation.id] ?? {
      officialEmail: organisation.officialEmail ?? '',
      officialPhone: organisation.officialPhone ?? '',
      website: organisation.website ?? '',
      coarseLocation: organisation.coarseLocation ?? ''
    }

    try {
      const updatedOrganisation = await updateOrganisationDetails(organisation.id, details)
      const updatedDetails = {
        officialEmail: updatedOrganisation.officialEmail ?? '',
        officialPhone: updatedOrganisation.officialPhone ?? '',
        website: updatedOrganisation.website ?? '',
        coarseLocation: updatedOrganisation.coarseLocation ?? ''
      }
      setAdminOrganisations((current) => current.map((item) => (
        item.id === updatedOrganisation.id ? updatedOrganisation : item
      )))
      setOrganisationDrafts((current) => ({ ...current, [organisation.id]: updatedDetails }))
      setServices((current) => current.map((service) => (
        service.organisationId === updatedOrganisation.id
          ? {
            ...service,
            ...publicOrganisationFields(updatedOrganisation)
          }
          : service
      )))
      setServiceDetail((current) => (
        current?.organisationId === updatedOrganisation.id
          ? {
            ...current,
            ...publicOrganisationFields(updatedOrganisation)
          }
          : current
      ))
      setDetailsMessage(`Os dados públicos de ${updatedOrganisation.name} foram guardados.`)
    } catch (requestError) {
      setDetailsError(requestError.message)
    } finally {
      setDetailsSavingId(null)
    }
  }

  function handleCloseDetails() {
    setServiceDetail(null)
    setDetailError('')
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
              <p className="text-sm">{ROLE_LABELS[authUser.role] ?? 'Utilizador'}</p>
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
            Entre com a sua conta para aceder às funcionalidades protegidas.
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

      {authUser?.role === 'PLATFORM_ADMIN' ? (
        <section
          className="mb-8 rounded-2xl border-2 border-violet-800 bg-violet-50 p-5"
          aria-labelledby="organisation-verification-title"
        >
          <h2 id="organisation-verification-title" className="text-2xl font-bold">
            Verificação de organizações
          </h2>
          <p className="mt-2 text-slate-800">
            Os contactos e a localização só ficam públicos depois de verificar a organização.
            Os dados detalhados permanecem disponíveis aqui para análise e edição administrativa.
            A autorização também é validada pelo servidor.
          </p>

          {adminOrganisationsError ? (
            <p className="mt-4 font-semibold text-red-950" role="alert">
              {adminOrganisationsError}
            </p>
          ) : null}

          {verificationMessage ? (
            <p className="mt-4 font-semibold text-emerald-900" role="status">
              {verificationMessage}
            </p>
          ) : null}
          {verificationError ? (
            <p className="mt-4 font-semibold text-red-950" role="alert">
              {verificationError}
            </p>
          ) : null}
          {detailsMessage ? (
            <p className="mt-4 font-semibold text-emerald-900" role="status">
              {detailsMessage}
            </p>
          ) : null}
          {detailsError ? (
            <p className="mt-4 font-semibold text-red-950" role="alert">
              {detailsError}
            </p>
          ) : null}

          {adminOrganisations === null ? (
            <p className="mt-4 rounded-xl border-2 border-slate-300 bg-white p-4" role="status">
              A carregar organizações...
            </p>
          ) : adminOrganisationsError ? null : adminOrganisations.length === 0 ? (
            <p className="mt-4 rounded-xl border-2 border-slate-300 bg-white p-4">
              Não há organizações registadas.
            </p>
          ) : (
            <ul className="mt-4 space-y-3">
              {adminOrganisations.map((organisation) => (
                <li
                  className="rounded-xl border-2 border-slate-300 bg-white p-4"
                  key={organisation.id}
                >
                  <div className="flex flex-wrap items-center justify-between gap-3">
                    <div>
                      <h3 className="font-bold">{organisation.name}</h3>
                      <p className="text-sm font-semibold text-slate-700">
                        {organisation.verified ? 'Verificada' : 'Por verificar'}
                      </p>
                    </div>
                    <button
                      className="min-h-12 rounded-xl border-2 border-violet-900 px-5 py-3 font-bold text-violet-950 hover:bg-violet-100 disabled:cursor-wait disabled:opacity-60"
                      type="button"
                      disabled={verificationLoadingId !== null || detailsSavingId !== null}
                      onClick={() => void handleVerificationChange(organisation)}
                    >
                      {verificationLoadingId === organisation.id
                        ? 'A guardar...'
                        : organisation.verified
                          ? 'Revogar verificação'
                          : 'Verificar organização'}
                    </button>
                  </div>
                  <h4 className="mt-4 font-bold">Contactos públicos e localização</h4>
                  <form
                    className="mt-3 grid gap-3 sm:grid-cols-2"
                    onSubmit={(event) => void handleOrganisationDetailsSubmit(event, organisation)}
                  >
                    <label>
                      <span className="block font-semibold">Email oficial</span>
                      <input
                        className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-3 py-2"
                        type="email"
                        maxLength={320}
                        value={(organisationDrafts[organisation.id] ?? organisation).officialEmail ?? ''}
                        onChange={(event) => handleOrganisationFieldChange(
                          organisation, 'officialEmail', event.target.value
                        )}
                        disabled={detailsSavingId !== null || verificationLoadingId !== null}
                      />
                    </label>
                    <label>
                      <span className="block font-semibold">Telefone oficial</span>
                      <input
                        className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-3 py-2"
                        type="tel"
                        maxLength={40}
                        value={(organisationDrafts[organisation.id] ?? organisation).officialPhone ?? ''}
                        onChange={(event) => handleOrganisationFieldChange(
                          organisation, 'officialPhone', event.target.value
                        )}
                        disabled={detailsSavingId !== null || verificationLoadingId !== null}
                      />
                    </label>
                    <label>
                      <span className="block font-semibold">Website (HTTP ou HTTPS)</span>
                      <input
                        className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-3 py-2"
                        type="url"
                        maxLength={2048}
                        value={(organisationDrafts[organisation.id] ?? organisation).website ?? ''}
                        onChange={(event) => handleOrganisationFieldChange(
                          organisation, 'website', event.target.value
                        )}
                        disabled={detailsSavingId !== null || verificationLoadingId !== null}
                      />
                    </label>
                    <label>
                      <span className="block font-semibold">Zona aproximada</span>
                      <input
                        className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-3 py-2"
                        type="text"
                        maxLength={120}
                        value={(organisationDrafts[organisation.id] ?? organisation).coarseLocation ?? ''}
                        onChange={(event) => handleOrganisationFieldChange(
                          organisation, 'coarseLocation', event.target.value
                        )}
                        disabled={detailsSavingId !== null || verificationLoadingId !== null}
                      />
                    </label>
                    <button
                      className="min-h-12 rounded-xl bg-violet-900 px-5 py-3 font-bold text-white hover:bg-violet-950 disabled:cursor-wait disabled:opacity-60 sm:col-span-2"
                      type="submit"
                      disabled={detailsSavingId !== null || verificationLoadingId !== null}
                    >
                      {detailsSavingId === organisation.id ? 'A guardar...' : 'Guardar dados públicos'}
                    </button>
                  </form>
                </li>
              ))}
            </ul>
          )}

          <div className="mt-8 border-t-2 border-violet-200 pt-5">
            <h3 className="text-xl font-bold">Associar prestadores a organizações</h3>
            <p className="mt-2 text-slate-800">
              A associação identifica a organização do prestador; só organizações verificadas podem publicar estado oficial.
            </p>
            {adminProvidersError ? (
              <p className="mt-4 font-semibold text-red-950" role="alert">{adminProvidersError}</p>
            ) : null}
            {providerAssignmentMessage ? (
              <p className="mt-4 font-semibold text-emerald-950" role="status">{providerAssignmentMessage}</p>
            ) : null}
            {providerAssignmentError ? (
              <p className="mt-4 font-semibold text-red-950" role="alert">{providerAssignmentError}</p>
            ) : null}
            {adminProviders === null ? (
              <p className="mt-4 rounded-xl border-2 border-slate-300 bg-white p-4" role="status">
                A carregar prestadores...
              </p>
            ) : adminProvidersError ? null : adminProviders.length === 0 ? (
              <p className="mt-4">Não há contas de prestador registadas.</p>
            ) : (
              <ul className="mt-4 space-y-3">
                {adminProviders.map((provider) => (
                  <li key={provider.id} className="rounded-xl border-2 border-slate-300 bg-white p-4">
                    <p className="font-bold">{provider.email}</p>
                    <p className="text-sm text-slate-700">
                      Organização atual: {provider.organisationName ?? 'Sem organização'}
                      {provider.organisationName
                        ? ` · ${provider.organisationVerified ? 'Verificada' : 'Por verificar'}`
                        : ''}
                    </p>
                    <form
                      className="mt-3 grid gap-3 sm:grid-cols-[1fr_auto]"
                      onSubmit={(event) => void handleProviderAssignmentSubmit(event, provider)}
                    >
                      <label className="grid gap-1">
                        Organização
                        <select
                          className="min-h-12 rounded-lg border-2 border-slate-500 bg-white px-3"
                          value={providerOrganisationDrafts[provider.id] ?? ''}
                          onChange={(event) => setProviderOrganisationDrafts((current) => ({
                            ...current,
                            [provider.id]: event.target.value
                          }))}
                          required
                          disabled={!adminOrganisations?.length || providerAssignmentSavingId !== null}
                        >
                          <option value="" disabled>Selecione uma organização</option>
                          {(adminOrganisations ?? []).map((organisation) => (
                            <option key={organisation.id} value={organisation.id}>
                              {organisation.name}{organisation.verified ? ' · Verificada' : ' · Por verificar'}
                            </option>
                          ))}
                        </select>
                      </label>
                      <button
                        className="min-h-12 self-end rounded-xl bg-violet-900 px-5 py-3 font-bold text-white hover:bg-violet-950 disabled:cursor-wait disabled:opacity-60"
                        type="submit"
                        disabled={!adminOrganisations?.length || providerAssignmentSavingId !== null}
                      >
                        {providerAssignmentSavingId === provider.id ? 'A guardar...' : 'Guardar associação'}
                      </button>
                    </form>
                  </li>
                ))}
              </ul>
            )}
          </div>
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

          {careProfile ? (
            <form className="mt-6 border-t-2 border-emerald-200 pt-5" onSubmit={handleContactSubmit}>
              <h3 className="text-xl font-bold">
                {editingContactId ? 'Editar contacto de emergência' : 'Adicionar contacto de emergência'}
              </h3>
              <p className="mt-2 text-slate-800">
                Guarde quem deve ser contactado rapidamente se a sua rotina de apoio falhar.
              </p>
              <div className="mt-4 grid gap-4 sm:grid-cols-2">
                <label>
                  <span className="block font-semibold">Nome</span>
                  <input
                    className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
                    value={contactName}
                    onChange={(event) => setContactName(event.target.value)}
                    maxLength={120}
                    required
                  />
                </label>
                <label>
                  <span className="block font-semibold">Telefone</span>
                  <input
                    className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
                    value={contactPhone}
                    onChange={(event) => setContactPhone(event.target.value)}
                    placeholder="+351 912 345 678"
                    type="tel"
                    required
                  />
                </label>
                <label className="sm:col-span-2">
                  <span className="block font-semibold">Relação (opcional)</span>
                  <input
                    className="mt-1 min-h-12 w-full rounded-xl border-2 border-slate-500 bg-white px-4 py-3 text-lg"
                    value={contactRelationship}
                    onChange={(event) => setContactRelationship(event.target.value)}
                    placeholder="Ex.: Filha, vizinho"
                    maxLength={60}
                  />
                </label>
              </div>
              <button
                className="mt-4 min-h-12 rounded-xl bg-sky-800 px-6 py-3 text-lg font-bold text-white hover:bg-sky-900"
                type="submit"
              >
                {editingContactId ? 'Guardar alterações' : 'Adicionar contacto'}
              </button>
              {editingContactId ? (
                <button
                  className="ml-3 min-h-12 rounded-xl border-2 border-slate-600 px-6 py-3 text-lg font-bold hover:bg-white"
                  type="button"
                  onClick={resetContactForm}
                >
                  Cancelar
                </button>
              ) : null}
              {contactError ? <p className="mt-3 font-semibold text-red-950" role="alert">{contactError}</p> : null}
              {contactSuccess ? <p className="mt-3 font-semibold text-emerald-950" role="status">{contactSuccess}</p> : null}
            </form>
          ) : null}

          {careProfile?.emergencyContacts?.length ? (
            <div className="mt-5">
              <h3 className="font-bold">Contactos de emergência</h3>
              <ul className="mt-2 space-y-2">
                {careProfile.emergencyContacts.map((contact) => (
                  <li key={contact.id} className="rounded-xl border-2 border-emerald-200 bg-white p-3">
                    <div className="flex flex-wrap items-start justify-between gap-3">
                      <div>
                        <p className="font-bold">{contact.displayName}</p>
                        <p className="text-sm text-slate-700">
                          {contact.phoneNumber}{contact.relationship ? ` · ${contact.relationship}` : ''}
                        </p>
                      </div>
                      <div className="flex gap-2">
                        <a
                          className="rounded-lg border-2 border-emerald-700 px-3 py-2 font-bold text-emerald-900"
                          href={`tel:${contact.phoneNumber}`}
                          aria-label={`Ligar para ${contact.displayName}`}
                        >
                          Ligar
                        </a>
                        <button
                          className="rounded-lg border-2 border-sky-700 px-3 py-2 font-bold text-sky-900"
                          type="button"
                          onClick={() => startEditingContact(contact)}
                        >
                          Editar
                        </button>
                        <button
                          className="rounded-lg border-2 border-red-700 px-3 py-2 font-bold text-red-900"
                          type="button"
                          onClick={() => void handleRemoveContact(contact.id)}
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
          <div className="mt-4 rounded-xl border-2 border-red-700 bg-red-50 p-4 font-semibold text-red-950" role="alert">
            <p>{assistantError}</p>
            <a className="mt-2 inline-block underline" href="#search-title">
              Usar a pesquisa normal de serviços
            </a>
          </div>
        ) : null}

        {assistantResult ? (
          <div className="mt-4 rounded-xl border-2 border-slate-300 bg-slate-50 p-4" aria-live="polite">
            {assistantResult.refusal ? (
              <div className="mt-1 rounded-xl border-2 border-red-700 bg-red-50 p-4" data-testid="assistant-refusal" role="alert">
                <p className="font-bold text-red-950">Não posso ajudar com esse pedido</p>
                <p className="mt-1 text-red-950">{assistantResult.refusal.reason}</p>
                <ul className="mt-2 space-y-1">
                  {assistantResult.refusal.contacts?.map((contact) => (
                    <li key={contact.uri}>
                      <a className="font-bold underline" href={contact.uri}>
                        {contact.label} ({contact.phoneNumber})
                      </a>
                    </li>
                  ))}
                </ul>
              </div>
            ) : (
              <p className="font-semibold">{assistantResult.answer}</p>
            )}
            {!assistantResult.refusal && assistantResult.isFallback ? (
              <p className="mt-2 text-sm font-semibold text-amber-900">
                Modo de contingência: a pergunta foi interpretada por palavras-chave sobre os dados do diretório.
              </p>
            ) : null}
            {!assistantResult.refusal && !assistantResult.isFallback ? (
              <p className="mt-2 text-sm font-semibold text-sky-900">
                Resposta gerada com IA apenas a partir dos registos verificados do diretório.
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
                    <ServiceContact service={service} />
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
          {providerServices === null ? (
            <p className="mt-4" role="status">A carregar os seus serviços...</p>
          ) : providerServicesError ? (
            <p className="mt-4 font-semibold text-red-950" role="alert">{providerServicesError}</p>
          ) : providerServices.length === 0 ? (
            <p className="mt-4">Nenhum serviço está associado à sua organização.</p>
          ) : (
            <div className="mt-4 space-y-4">
              {providerStatusMessage ? (
                <p className="font-semibold text-emerald-950" role="status">{providerStatusMessage}</p>
              ) : null}
              {providerServices.map((service) => (
                <form
                  key={service.id}
                  className="grid gap-3 rounded-xl border-2 border-amber-300 bg-white p-4"
                  onSubmit={(event) => void handleProviderStatusSave(event, service)}
                >
                  <div className="flex flex-wrap items-center justify-between gap-2">
                    <h3 className="text-lg font-bold">{service.name}</h3>
                    {service.verified ? (
                      <span className="rounded-lg border-2 border-emerald-700 bg-emerald-50 px-3 py-1 text-sm font-bold text-emerald-950">
                        Estado Oficial
                      </span>
                    ) : (
                      <span className="rounded-lg border-2 border-amber-700 bg-amber-50 px-3 py-1 text-sm font-bold">
                        Organização por verificar
                      </span>
                    )}
                  </div>
                  <label className="grid gap-1">
                    Estado de funcionamento
                    <select name="status" defaultValue={service.currentStatus} className="min-h-12 rounded-lg border-2 border-slate-500 bg-white px-3">
                      <option value="OPEN">Aberto</option>
                      <option value="CLOSED">Fechado</option>
                      <option value="TEMPORARILY_CLOSED">Temporariamente fechado</option>
                      <option value="UNKNOWN">Estado desconhecido</option>
                    </select>
                  </label>
                  <label className="grid gap-1">
                    Horário
                    <input
                      name="operatingHours"
                      defaultValue={service.operatingHours}
                      maxLength="120"
                      className="min-h-12 rounded-lg border-2 border-slate-500 px-3"
                    />
                  </label>
                  <label className="grid gap-1">
                    Aviso oficial
                    <textarea
                      name="officialNotice"
                      defaultValue={service.officialNotice ?? ''}
                      maxLength="500"
                      rows="3"
                      className="rounded-lg border-2 border-slate-500 px-3 py-2"
                    />
                  </label>
                  <button
                    className="min-h-12 rounded-xl bg-emerald-800 px-5 py-3 text-lg font-bold text-white hover:bg-emerald-900 disabled:opacity-60"
                    type="submit"
                    disabled={providerSavingId === service.id || !service.verified}
                  >
                    {providerSavingId === service.id ? 'A guardar...' : 'Guardar estado oficial'}
                  </button>
                  {!service.verified ? (
                    <p className="text-sm font-semibold text-amber-950">
                      Só organizações verificadas podem publicar Estado Oficial.
                    </p>
                  ) : null}
                </form>
              ))}
            </div>
          )}
        </section>
      )}

      <section className="mb-8" aria-labelledby="search-title">
        <h2 id="search-title" tabIndex="-1" className="text-2xl font-bold">
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

      {detailLoading || detailError || serviceDetail ? (
        <section className="mb-8" aria-labelledby="service-detail-title">
          <button
            className="mb-4 font-bold text-sky-900 underline"
            type="button"
            onClick={handleCloseDetails}
          >
            ← Voltar aos resultados
          </button>

          {detailLoading ? (
            <p className="rounded-xl border-2 border-slate-300 bg-white p-5" role="status">
              A carregar detalhes do serviço...
            </p>
          ) : null}

          {detailError ? (
            <p className="rounded-xl border-2 border-red-700 bg-red-50 p-5 font-semibold text-red-950" role="alert">
              {detailError}
            </p>
          ) : null}

          {serviceDetail ? (
            <article className="rounded-2xl border-2 border-slate-300 bg-white p-5 shadow-sm">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <h2 id="service-detail-title" className="text-2xl font-bold">{serviceDetail.name}</h2>
                  <p className="text-slate-700">
                    {TYPE_LABELS[serviceDetail.type] ?? serviceDetail.type} · {serviceDetail.coarseLocation}
                  </p>
                </div>
                <span
                  className={`inline-block rounded-full border-2 px-4 py-1 text-sm font-bold ${
                    STATUS_STYLES[serviceDetail.currentStatus] ?? STATUS_STYLES.UNKNOWN
                  }`}
                >
                  {STATUS_LABELS[serviceDetail.currentStatus] ?? serviceDetail.currentStatus}
                </span>
              </div>

              <dl className="mt-4 grid gap-2 sm:grid-cols-2">
                <div>
                  <dt className="font-semibold">Horário</dt>
                  <dd>{serviceDetail.operatingHours}</dd>
                </div>
                {serviceDetail.officialNotice ? (
                  <div>
                    <dt className="font-semibold">Aviso oficial</dt>
                    <dd>{serviceDetail.officialNotice}</dd>
                  </div>
                ) : null}
                <div>
                  <dt className="font-semibold">Fonte da informação</dt>
                  <dd data-testid="service-detail-source">{serviceDetail.source}</dd>
                </div>
                <div>
                  <dt className="font-semibold">Última atualização</dt>
                  <dd data-testid="service-detail-last-updated">{formatDate(serviceDetail.lastUpdated)}</dd>
                </div>
              </dl>

              <OrganisationPublicDetails service={serviceDetail} />

              <div className="mt-4 flex flex-wrap items-center justify-between gap-3">
                <span
                  className={`rounded-lg border-2 px-3 py-1 text-sm font-bold ${
                    serviceDetail.verified
                      ? 'border-emerald-700 bg-emerald-50 text-emerald-950'
                      : 'border-amber-700 bg-amber-50 text-amber-950'
                  }`}
                >
                  {serviceDetail.verified ? 'Estado Oficial' : 'Fonte comunitária (não verificada)'}
                </span>

                <a
                  className="inline-flex min-h-12 items-center justify-center rounded-xl bg-emerald-800 px-6 py-3 text-lg font-bold text-white hover:bg-emerald-900"
                  href={`tel:${serviceDetail.contactPhone}`}
                  aria-label={`Ligar para ${serviceDetail.name}`}
                >
                  Ligar agora
                </a>
              </div>

              <p className="mt-6 rounded-xl border-2 border-amber-700 bg-amber-50 p-4 font-semibold text-amber-950">
                A disponibilidade em tempo real não é garantida. Confirme sempre diretamente por telefone.
              </p>
            </article>
          ) : null}
        </section>
      ) : (

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
                  {service.officialNotice ? (
                    <div>
                      <dt className="font-semibold">Aviso oficial</dt>
                      <dd>{service.officialNotice}</dd>
                    </div>
                  ) : null}
                  <div>
                    <dt className="font-semibold">Última atualização</dt>
                    <dd>{formatDate(service.lastUpdated)}</dd>
                  </div>
                  <div>
                    <dt className="font-semibold">Fonte</dt>
                    <dd>{service.source ?? 'Diretório de serviços da plataforma'}</dd>
                  </div>
                </dl>

                <OrganisationPublicDetails service={service} />

                <div className="mt-4 flex flex-wrap items-center justify-between gap-3">
                  <span
                    className={`rounded-lg border-2 px-3 py-1 text-sm font-bold ${
                      service.verified
                        ? 'border-emerald-700 bg-emerald-50 text-emerald-950'
                        : 'border-amber-700 bg-amber-50 text-amber-950'
                    }`}
                  >
                    {service.verified ? 'Estado Oficial' : 'Fonte comunitária (não verificada)'}
                  </span>

                  <button
                    className="inline-flex min-h-12 items-center justify-center rounded-xl border-2 border-sky-800 px-6 py-3 text-lg font-bold text-sky-900 hover:bg-sky-50"
                    type="button"
                    onClick={() => void handleViewDetails(service.id)}
                  >
                    Ver detalhes
                  </button>
                </div>
                <ServiceContact service={service} />
              </article>
            </li>
          ))}
        </ul>

      </section>
      )}
    </main>
  )
}
