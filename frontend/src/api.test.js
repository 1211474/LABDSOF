import { afterEach, describe, expect, it, vi } from 'vitest'
import {
  assignProviderOrganisation,
  askNavigator,
  fetchAdminOrganisations,
  fetchAdminProviders,
  setOrganisationVerification,
  updateOrganisationDetails
} from './api'

describe('askNavigator', () => {
  afterEach(() => {
    vi.useRealTimers()
    vi.restoreAllMocks()
  })

  it('aborts a stalled request and reports a timeout', async () => {
    vi.useFakeTimers()
    global.fetch = vi.fn((_url, { signal }) => new Promise((_resolve, reject) => {
      signal.addEventListener('abort', () => reject(new DOMException('Aborted', 'AbortError')))
    }))

    const request = askNavigator('farmácias')
    const assertion = expect(request).rejects.toThrow('O assistente demorou demasiado tempo a responder.')
    await vi.advanceTimersByTimeAsync(8000)
    await assertion
    expect(global.fetch.mock.calls[0][1].signal.aborted).toBe(true)
  })

  it('reports network errors instead of exposing a browser error', async () => {
    global.fetch = vi.fn().mockRejectedValue(new TypeError('Failed to fetch'))
    await expect(askNavigator('farmácias'))
      .rejects.toThrow('Não foi possível obter uma resposta do assistente.')
  })

  it('rejects a response without an answer', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: true, json: async () => ({}) })
    await expect(askNavigator('farmácias'))
      .rejects.toThrow('O assistente devolveu uma resposta inválida.')
  })
})

describe('setOrganisationVerification', () => {
  afterEach(() => {
    vi.restoreAllMocks()
    window.localStorage.clear()
  })

  it('sends an authenticated verification update', async () => {
    window.localStorage.setItem('carebnb.token', 'admin-token')
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ id: 'org-1', verified: true })
    })

    await expect(setOrganisationVerification('org-1', true))
      .resolves.toEqual({ id: 'org-1', verified: true })
    expect(global.fetch).toHaveBeenCalledWith(
      '/api/v1/admin/organisations/org-1/verification',
      expect.objectContaining({
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer admin-token'
        },
        body: JSON.stringify({ verified: true })
      })
    )
  })

  it('surfaces authorization errors from the API', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: false,
      json: async () => ({ detail: 'Acesso proibido' })
    })

    await expect(setOrganisationVerification('org-1', true))
      .rejects.toThrow('Acesso proibido')
  })
})

describe('fetchAdminOrganisations', () => {
  afterEach(() => {
    vi.restoreAllMocks()
    window.localStorage.clear()
  })

  it('loads organisation details using the authenticated admin endpoint', async () => {
    window.localStorage.setItem('carebnb.token', 'admin-token')
    const organisations = [{ id: 'org-1', verified: false, officialEmail: 'contact@example.org' }]
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => organisations
    })

    await expect(fetchAdminOrganisations()).resolves.toEqual(organisations)
    expect(global.fetch).toHaveBeenCalledWith(
      '/api/v1/admin/organisations',
      { headers: { Authorization: 'Bearer admin-token' } }
    )
  })
})

describe('provider organisation assignment', () => {
  afterEach(() => {
    vi.restoreAllMocks()
    window.localStorage.clear()
  })

  it('loads providers and assigns one to an organisation using the admin token', async () => {
    window.localStorage.setItem('carebnb.token', 'admin-token')
    const providers = [{ id: 'provider-1', email: 'provider@example.org', organisationId: null }]
    const assignedProvider = {
      id: 'provider-1',
      email: 'provider@example.org',
      organisationId: 'org-1',
      organisationName: 'Community Centre',
      organisationVerified: true
    }
    global.fetch = vi.fn()
      .mockResolvedValueOnce({ ok: true, json: async () => providers })
      .mockResolvedValueOnce({ ok: true, json: async () => assignedProvider })

    await expect(fetchAdminProviders()).resolves.toEqual(providers)
    await expect(assignProviderOrganisation('provider-1', 'org-1')).resolves.toEqual(assignedProvider)
    expect(global.fetch).toHaveBeenNthCalledWith(1, '/api/v1/admin/providers', {
      headers: { Authorization: 'Bearer admin-token' }
    })
    expect(global.fetch).toHaveBeenNthCalledWith(
      2,
      '/api/v1/admin/providers/provider-1/organisation',
      expect.objectContaining({
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer admin-token'
        },
        body: JSON.stringify({ organisationId: 'org-1' })
      })
    )
  })
})

describe('updateOrganisationDetails', () => {
  afterEach(() => {
    vi.restoreAllMocks()
    window.localStorage.clear()
  })

  it('sends an authenticated public-details update', async () => {
    window.localStorage.setItem('carebnb.token', 'admin-token')
    const details = {
      officialEmail: 'contact@example.org',
      officialPhone: '+351 220 000 000',
      website: 'https://example.org',
      coarseLocation: 'Boavista'
    }
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ id: 'org-1', ...details })
    })

    await expect(updateOrganisationDetails('org-1', details))
      .resolves.toEqual({ id: 'org-1', ...details })
    expect(global.fetch).toHaveBeenCalledWith(
      '/api/v1/admin/organisations/org-1',
      expect.objectContaining({
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer admin-token'
        },
        body: JSON.stringify(details)
      })
    )
  })
})
