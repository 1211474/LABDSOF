export async function fetchServices({ type, coarseLocation } = {}) {
  const params = new URLSearchParams()
  if (type) params.set('type', type)
  if (coarseLocation) params.set('coarseLocation', coarseLocation)

  const query = params.toString()
  const response = await fetch(`/api/v1/services${query ? `?${query}` : ''}`)
  if (!response.ok) throw new Error('Não foi possível carregar os serviços.')
  return response.json()
}

export async function login(email, password) {
  const response = await fetch('/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(body.detail || 'Não foi possível iniciar sessão.')
  return body
}

export async function fetchCurrentUser(token) {
  const response = await fetch('/api/v1/auth/me', {
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) throw new Error('A sessão expirou.')
  return response.json()
}

function authenticatedHeaders() {
  const token = window.localStorage.getItem('carebnb.token')
  return token ? { Authorization: `Bearer ${token}` } : {}
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
  return profileRequest('/api/v1/caregivers/me')
}

export function createCaregiverProfile(profile) {
  return profileRequest('/api/v1/caregivers', {
    method: 'POST',
    body: JSON.stringify(profile)
  })
}

export function updateCaregiverProfile(caregiverId, profile) {
  return profileRequest(`/api/v1/caregivers/${caregiverId}`, {
    method: 'PATCH',
    body: JSON.stringify(profile)
  })
}

export function addCareRecipient(caregiverId, recipient) {
  return profileRequest(`/api/v1/caregivers/${caregiverId}/recipients`, {
    method: 'POST',
    body: JSON.stringify(recipient)
  })
}

export function updateCareRecipient(caregiverId, recipientId, recipient) {
  return profileRequest(`/api/v1/caregivers/${caregiverId}/recipients/${recipientId}`, {
    method: 'PATCH',
    body: JSON.stringify(recipient)
  })
}

export async function removeCareRecipient(caregiverId, recipientId) {
  const response = await fetch(`/api/v1/caregivers/${caregiverId}/recipients/${recipientId}`, {
    method: 'DELETE',
    headers: authenticatedHeaders()
  })
  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    throw new Error(body.detail || 'Não foi possível remover a pessoa acompanhada.')
  }
}

export function addEmergencyContact(caregiverId, contact) {
  return profileRequest(`/api/v1/caregivers/${caregiverId}/emergency-contacts`, {
    method: 'POST',
    body: JSON.stringify(contact)
  })
}

export function updateEmergencyContact(caregiverId, contactId, contact) {
  return profileRequest(`/api/v1/caregivers/${caregiverId}/emergency-contacts/${contactId}`, {
    method: 'PATCH',
    body: JSON.stringify(contact)
  })
}

export async function removeEmergencyContact(caregiverId, contactId) {
  const response = await fetch(`/api/v1/caregivers/${caregiverId}/emergency-contacts/${contactId}`, {
    method: 'DELETE',
    headers: authenticatedHeaders()
  })
  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    throw new Error(body.detail || 'Não foi possível remover o contacto de emergência.')
  }
}

export async function askNavigator(query) {
  const response = await fetch('/api/v1/ai/navigate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query })
  })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(body.error || 'O assistente está temporariamente indisponível.')
  return body
}
