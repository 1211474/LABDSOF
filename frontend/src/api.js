const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')

function apiUrl(path) {
  return `${API_BASE_URL}${path}`
}

export async function fetchServices({ type, coarseLocation } = {}) {
  const params = new URLSearchParams()
  if (type) params.set('type', type)
  if (coarseLocation) params.set('coarseLocation', coarseLocation)

  const query = params.toString()
  const response = await fetch(apiUrl(`/api/v1/services${query ? `?${query}` : ''}`))
  if (!response.ok) throw new Error('Não foi possível carregar os serviços.')
  return response.json()
}

export async function fetchServiceDetails(serviceId) {
  const response = await fetch(apiUrl(`/api/v1/services/${serviceId}`))
  if (!response.ok) throw new Error('Não foi possível carregar os detalhes do serviço.')
  return response.json()
}

export async function login(email, password) {
  const response = await fetch(apiUrl('/api/v1/auth/login'), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(body.detail || 'Não foi possível iniciar sessão.')
  return body
}

export async function fetchCurrentUser(token) {
  const response = await fetch(apiUrl('/api/v1/auth/me'), {
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) throw new Error('A sessão expirou.')
  return response.json()
}

function authenticatedHeaders() {
  const token = window.localStorage.getItem('carebnb.token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export async function fetchAdminOrganisations() {
  const response = await fetch(apiUrl('/api/v1/admin/organisations'), {
    headers: authenticatedHeaders()
  })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) {
    throw new Error(body.detail || 'Não foi possível carregar as organizações.')
  }
  return body
}

export async function setOrganisationVerification(organisationId, verified) {
  const response = await fetch(apiUrl(`/api/v1/admin/organisations/${organisationId}/verification`), {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      ...authenticatedHeaders()
    },
    body: JSON.stringify({ verified })
  })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) {
    throw new Error(body.detail || 'Não foi possível atualizar a verificação da organização.')
  }
  return body
}

export async function updateOrganisationDetails(organisationId, details) {
  const response = await fetch(apiUrl(`/api/v1/admin/organisations/${organisationId}`), {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      ...authenticatedHeaders()
    },
    body: JSON.stringify(details)
  })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) {
    throw new Error(body.detail || 'Não foi possível atualizar os dados da organização.')
  }
  return body
}

async function profileRequest(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...authenticatedHeaders(),
      ...options.headers
    }
  })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(body.detail || 'Não foi possível guardar o perfil.')
  return body
}

export function fetchCaregiverProfile() {
  return profileRequest(apiUrl('/api/v1/caregivers/me'))
}

export function createCaregiverProfile(profile) {
  return profileRequest(apiUrl('/api/v1/caregivers'), {
    method: 'POST',
    body: JSON.stringify(profile)
  })
}

export function updateCaregiverProfile(caregiverId, profile) {
  return profileRequest(apiUrl(`/api/v1/caregivers/${caregiverId}`), {
    method: 'PATCH',
    body: JSON.stringify(profile)
  })
}

export function addCareRecipient(caregiverId, recipient) {
  return profileRequest(apiUrl(`/api/v1/caregivers/${caregiverId}/recipients`), {
    method: 'POST',
    body: JSON.stringify(recipient)
  })
}

export function updateCareRecipient(caregiverId, recipientId, recipient) {
  return profileRequest(apiUrl(`/api/v1/caregivers/${caregiverId}/recipients/${recipientId}`), {
    method: 'PATCH',
    body: JSON.stringify(recipient)
  })
}

export async function removeCareRecipient(caregiverId, recipientId) {
  const response = await fetch(apiUrl(`/api/v1/caregivers/${caregiverId}/recipients/${recipientId}`), {
    method: 'DELETE',
    headers: authenticatedHeaders()
  })
  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    throw new Error(body.detail || 'Não foi possível remover a pessoa acompanhada.')
  }
}

export function addEmergencyContact(caregiverId, contact) {
  return profileRequest(apiUrl(`/api/v1/caregivers/${caregiverId}/emergency-contacts`), {
    method: 'POST',
    body: JSON.stringify(contact)
  })
}

export function updateEmergencyContact(caregiverId, contactId, contact) {
  return profileRequest(apiUrl(`/api/v1/caregivers/${caregiverId}/emergency-contacts/${contactId}`), {
    method: 'PATCH',
    body: JSON.stringify(contact)
  })
}

export async function removeEmergencyContact(caregiverId, contactId) {
  const response = await fetch(apiUrl(`/api/v1/caregivers/${caregiverId}/emergency-contacts/${contactId}`), {
    method: 'DELETE',
    headers: authenticatedHeaders()
  })
  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    throw new Error(body.detail || 'Não foi possível remover o contacto de emergência.')
  }
}

export async function askNavigator(query) {
  const controller = new AbortController()
  const timeout = setTimeout(() => controller.abort(), 8000)

  try {
    const response = await fetch(apiUrl('/api/v1/ai/navigate'), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ query }),
      signal: controller.signal
    })
    if (!response.ok) throw new Error('O assistente está temporariamente indisponível.')

    const body = await response.json()
    if (typeof body?.answer !== 'string' || !body.answer.trim()) {
      throw new Error('O assistente devolveu uma resposta inválida.')
    }
    return body
  } catch (error) {
    if (controller.signal.aborted) {
      throw new Error('O assistente demorou demasiado tempo a responder.')
    }
    if (error instanceof TypeError || error instanceof SyntaxError) {
      throw new Error('Não foi possível obter uma resposta do assistente.')
    }
    throw error
  } finally {
    clearTimeout(timeout)
  }
}
