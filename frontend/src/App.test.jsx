import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { act, cleanup, fireEvent, render, screen, within } from '@testing-library/react'
import App from './App.jsx'

const services = [
  {
    id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    organisationId: '11111111-1111-1111-1111-111111111111',
    name: 'Farmácia da Boavista',
    source: 'Diretório oficial da plataforma',
    type: 'PHARMACY',
    coarseLocation: 'Boavista',
    operatingHours: '08:00 - 22:00',
    currentStatus: 'OPEN',
    contactPhone: '+351220000001',
    verified: true,
    lastUpdated: '2026-10-12T15:30:00Z'
  }
]

describe('App', () => {
  beforeEach(() => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => services
    })
  })

  afterEach(() => {
    cleanup()
    window.localStorage.clear()
    vi.useRealTimers()
    vi.restoreAllMocks()
  })

  it('renders services returned by the Care Platform API', async () => {
    render(<App />)

    expect(screen.getByRole('heading', { name: /carebnb/i, level: 1 })).toBeInTheDocument()
    expect(await screen.findByText('Farmácia da Boavista')).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /ligar para farmácia da boavista/i }))
      .toHaveAttribute('href', 'tel:+351220000001')
    expect(screen.getByText('Estado Oficial')).toBeInTheDocument()
    expect(screen.getByText('Diretório oficial da plataforma', { selector: 'dd' })).toBeInTheDocument()
    const result = screen.getByRole('heading', { name: 'Farmácia da Boavista' }).closest('article')
    expect(within(result).getByText(/disponibilidade em tempo real não é garantida.*confirme.*telefone/i))
      .toBeInTheDocument()
  })

  it('lets a platform admin verify an organisation and updates its service badge', async () => {
    const unverifiedServices = services.map((service) => ({ ...service, verified: false }))
    const updatedOrganisation = {
      id: services[0].organisationId,
      name: services[0].source,
      verified: true,
      verifiedAt: '2026-11-26T10:00:00Z'
    }
    const adminOrganisation = {
      id: services[0].organisationId,
      name: services[0].source,
      verified: false,
      officialEmail: null,
      officialPhone: null,
      website: null,
      coarseLocation: null
    }
    global.fetch = vi.fn((url, options = {}) => {
      if (url === '/api/v1/auth/login') {
        return Promise.resolve({
          ok: true,
          json: async () => ({
            token: 'admin-token',
            role: 'PLATFORM_ADMIN',
            permissions: ['VERIFY_ORGANISATIONS']
          })
        })
      }
      if (url === `/api/v1/admin/organisations/${services[0].organisationId}/verification`) {
        return Promise.resolve({ ok: true, json: async () => updatedOrganisation })
      }
      if (url === '/api/v1/admin/organisations') {
        return Promise.resolve({ ok: true, json: async () => [adminOrganisation] })
      }
      return Promise.resolve({ ok: true, json: async () => unverifiedServices })
    })

    render(<App />)
    await screen.findByText('Fonte comunitária (não verificada)')
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'admin@example.com' } })
    fireEvent.change(screen.getByLabelText('Palavra-passe'), { target: { value: 'password' } })
    fireEvent.click(screen.getByRole('button', { name: 'Entrar' }))

    expect(await screen.findByRole('heading', { name: 'Verificação de organizações' }))
      .toBeInTheDocument()
    fireEvent.click(screen.getByRole('button', { name: 'Verificar organização' }))

    expect(await screen.findByRole('status')).toHaveTextContent('foi verificada')
    expect(screen.getAllByText('Estado Oficial')).toHaveLength(1)
    expect(global.fetch).toHaveBeenCalledWith(
      `/api/v1/admin/organisations/${services[0].organisationId}/verification`,
      expect.objectContaining({
        method: 'PUT',
        headers: expect.objectContaining({ Authorization: 'Bearer admin-token' }),
        body: JSON.stringify({ verified: true })
      })
    )
  })

  it('lets a platform admin assign a provider to an organisation', async () => {
    const organisation = {
      id: services[0].organisationId,
      name: 'Farmácia da Boavista',
      verified: true,
      officialEmail: null,
      officialPhone: null,
      website: null,
      coarseLocation: null
    }
    const provider = {
      id: 'provider-1',
      email: 'sofia@example.com',
      organisationId: null,
      organisationName: null,
      organisationVerified: false
    }
    const assignedProvider = {
      ...provider,
      organisationId: organisation.id,
      organisationName: organisation.name,
      organisationVerified: true
    }
    global.fetch = vi.fn((url, options = {}) => {
      if (url === '/api/v1/auth/login') {
        return Promise.resolve({
          ok: true,
          json: async () => ({
            token: 'admin-token',
            role: 'PLATFORM_ADMIN',
            permissions: ['VERIFY_ORGANISATIONS']
          })
        })
      }
      if (url === '/api/v1/admin/organisations') {
        return Promise.resolve({ ok: true, json: async () => [organisation] })
      }
      if (url === '/api/v1/admin/providers') {
        return Promise.resolve({ ok: true, json: async () => [provider] })
      }
      if (url === '/api/v1/admin/providers/provider-1/organisation') {
        expect(options.body).toBe(JSON.stringify({ organisationId: organisation.id }))
        return Promise.resolve({ ok: true, json: async () => assignedProvider })
      }
      return Promise.resolve({ ok: true, json: async () => services })
    })

    render(<App />)
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'admin@example.com' } })
    fireEvent.change(screen.getByLabelText('Palavra-passe'), { target: { value: 'password' } })
    fireEvent.click(screen.getByRole('button', { name: 'Entrar' }))

    expect(await screen.findByText('sofia@example.com')).toBeInTheDocument()
    fireEvent.change(screen.getByLabelText('Organização'), { target: { value: organisation.id } })
    fireEvent.click(screen.getByRole('button', { name: 'Guardar associação' }))

    expect(await screen.findByRole('status')).toHaveTextContent(
      'sofia@example.com foi associado a Farmácia da Boavista'
    )
    expect(screen.getByText(/Organização atual: Farmácia da Boavista/)).toBeInTheDocument()
  })

  it('lets a platform admin edit public organisation details', async () => {
    const emptyDetailsServices = services.map((service) => ({
      ...service,
      organisationEmail: null,
      organisationPhone: null,
      organisationWebsite: null,
      organisationCoarseLocation: null
    }))
    const updatedOrganisation = {
      id: services[0].organisationId,
      name: services[0].source,
      verified: true,
      verifiedAt: null,
      officialEmail: 'contact@example.org',
      officialPhone: '+351 220 000 000',
      website: 'https://example.org',
      coarseLocation: 'Boavista'
    }
    const adminOrganisation = {
      ...updatedOrganisation,
      officialEmail: 'old-contact@example.org'
    }
    global.fetch = vi.fn((url) => {
      if (url === '/api/v1/auth/login') {
        return Promise.resolve({
          ok: true,
          json: async () => ({
            token: 'admin-token',
            role: 'PLATFORM_ADMIN',
            permissions: ['VERIFY_ORGANISATIONS']
          })
        })
      }
      if (url === `/api/v1/admin/organisations/${services[0].organisationId}`) {
        return Promise.resolve({ ok: true, json: async () => updatedOrganisation })
      }
      if (url === '/api/v1/admin/organisations') {
        return Promise.resolve({ ok: true, json: async () => [adminOrganisation] })
      }
      return Promise.resolve({ ok: true, json: async () => emptyDetailsServices })
    })

    render(<App />)
    await screen.findByText('Farmácia da Boavista')
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'admin@example.com' } })
    fireEvent.change(screen.getByLabelText('Palavra-passe'), { target: { value: 'password' } })
    fireEvent.click(screen.getByRole('button', { name: 'Entrar' }))

    await screen.findByRole('heading', { name: 'Verificação de organizações' })
    expect(await screen.findByDisplayValue('old-contact@example.org')).toBeInTheDocument()
    fireEvent.change(screen.getByLabelText('Email oficial'), { target: { value: updatedOrganisation.officialEmail } })
    fireEvent.change(screen.getByLabelText('Telefone oficial'), { target: { value: updatedOrganisation.officialPhone } })
    fireEvent.change(screen.getByLabelText('Website (HTTP ou HTTPS)'), {
      target: { value: updatedOrganisation.website }
    })
    fireEvent.change(screen.getByLabelText('Zona aproximada'), {
      target: { value: updatedOrganisation.coarseLocation }
    })
    fireEvent.click(screen.getByRole('button', { name: 'Guardar dados públicos' }))

    expect(await screen.findByRole('status')).toHaveTextContent('dados públicos')
    expect(screen.getByRole('link', { name: updatedOrganisation.officialEmail }))
      .toHaveAttribute('href', `mailto:${updatedOrganisation.officialEmail}`)
    expect(screen.getByRole('link', { name: updatedOrganisation.officialPhone }))
      .toHaveAttribute('href', `tel:${updatedOrganisation.officialPhone}`)
    expect(screen.getByRole('link', { name: 'Visitar site da organização' }))
      .toHaveAttribute('href', updatedOrganisation.website)
    expect(screen.getAllByText('Boavista')).not.toHaveLength(0)
    expect(global.fetch).toHaveBeenCalledWith(
      `/api/v1/admin/organisations/${services[0].organisationId}`,
      expect.objectContaining({
        method: 'PATCH',
        headers: expect.objectContaining({ Authorization: 'Bearer admin-token' }),
        body: JSON.stringify({
          officialEmail: updatedOrganisation.officialEmail,
          officialPhone: updatedOrganisation.officialPhone,
          website: updatedOrganisation.website,
          coarseLocation: updatedOrganisation.coarseLocation
        })
      })
    )
  })

  it('hides organisation details immediately when verification is revoked', async () => {
    const verifiedService = {
      ...services[0],
      organisationEmail: 'contact@example.org',
      organisationPhone: '+351 220 000 000',
      organisationWebsite: 'https://example.org',
      organisationCoarseLocation: 'Boavista'
    }
    const adminOrganisation = {
      id: verifiedService.organisationId,
      name: verifiedService.source,
      verified: true,
      officialEmail: verifiedService.organisationEmail,
      officialPhone: verifiedService.organisationPhone,
      website: verifiedService.organisationWebsite,
      coarseLocation: verifiedService.organisationCoarseLocation
    }
    const unverifiedOrganisation = { ...adminOrganisation, verified: false }
    global.fetch = vi.fn((url) => {
      if (url === '/api/v1/auth/login') {
        return Promise.resolve({
          ok: true,
          json: async () => ({
            token: 'admin-token',
            role: 'PLATFORM_ADMIN',
            permissions: ['VERIFY_ORGANISATIONS']
          })
        })
      }
      if (url === '/api/v1/admin/organisations') {
        return Promise.resolve({ ok: true, json: async () => [adminOrganisation] })
      }
      if (url === `/api/v1/admin/organisations/${verifiedService.organisationId}/verification`) {
        return Promise.resolve({ ok: true, json: async () => unverifiedOrganisation })
      }
      return Promise.resolve({ ok: true, json: async () => [verifiedService] })
    })

    render(<App />)
    expect(await screen.findByTestId('organisation-public-details')).toHaveTextContent('contact@example.org')
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'admin@example.com' } })
    fireEvent.change(screen.getByLabelText('Palavra-passe'), { target: { value: 'password' } })
    fireEvent.click(screen.getByRole('button', { name: 'Entrar' }))

    fireEvent.click(await screen.findByRole('button', { name: 'Revogar verificação' }))

    expect(await screen.findByText('Fonte comunitária (não verificada)')).toBeInTheDocument()
    expect(screen.queryByTestId('organisation-public-details')).not.toBeInTheDocument()
  })

  it('does not show verification controls to non-admin users', async () => {
    global.fetch = vi.fn((url) => {
      if (url === '/api/v1/auth/login') {
        return Promise.resolve({
          ok: true,
          json: async () => ({
            token: 'caregiver-token',
            role: 'CAREGIVER',
            permissions: ['SEARCH_SERVICES']
          })
        })
      }
      return Promise.resolve({ ok: true, json: async () => services })
    })

    render(<App />)
    await screen.findByText('Farmácia da Boavista')
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'caregiver@example.com' } })
    fireEvent.change(screen.getByLabelText('Palavra-passe'), { target: { value: 'password' } })
    fireEvent.click(screen.getByRole('button', { name: 'Entrar' }))

    expect(await screen.findByText('Cuidador informal')).toBeInTheDocument()
    expect(screen.queryByRole('heading', { name: 'Verificação de organizações' }))
      .not.toBeInTheDocument()
  })

  it('does not offer a call link when a service has no phone number', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => [{ ...services[0], contactPhone: '  ' }]
    })

    render(<App />)
    expect(await screen.findByText('Contacto telefónico indisponível.')).toBeInTheDocument()
    expect(screen.queryByRole('link', { name: /ligar para farmácia da boavista/i })).not.toBeInTheDocument()
  })

  it('opens the service detail page showing source, last update, call button and disclaimer', async () => {
    const serviceDetail = {
      ...services[0],
      source: 'Farmácia da Boavista, Lda.'
    }

    global.fetch = vi.fn((url) => {
      if (url === `/api/v1/services/${services[0].id}`) {
        return Promise.resolve({ ok: true, json: async () => serviceDetail })
      }
      return Promise.resolve({ ok: true, json: async () => services })
    })

    render(<App />)
    await screen.findByText('Farmácia da Boavista')

    fireEvent.click(screen.getByRole('button', { name: /ver detalhes/i }))

    expect(await screen.findByRole('heading', { name: 'Farmácia da Boavista', level: 2 })).toBeInTheDocument()
    expect(screen.getByTestId('service-detail-source')).toHaveTextContent('Farmácia da Boavista, Lda.')
    expect(screen.getByTestId('service-detail-last-updated')).not.toBeEmptyDOMElement()
    expect(screen.getByRole('link', { name: /ligar para farmácia da boavista/i }))
      .toHaveAttribute('href', 'tel:+351220000001')
    expect(screen.getByText(/disponibilidade em tempo real não é garantida/i)).toBeInTheDocument()

    fireEvent.click(screen.getByRole('button', { name: /voltar aos resultados/i }))
    expect(screen.queryByTestId('service-detail-source')).not.toBeInTheDocument()
  })

  it('shows an error when the service detail request fails', async () => {
    global.fetch = vi.fn((url) => {
      if (url === `/api/v1/services/${services[0].id}`) {
        return Promise.resolve({ ok: false, status: 404 })
      }
      return Promise.resolve({ ok: true, json: async () => services })
    })

    render(<App />)
    await screen.findByText('Farmácia da Boavista')

    fireEvent.click(screen.getByRole('button', { name: /ver detalhes/i }))

    expect(await screen.findByRole('alert')).toHaveTextContent(/não foi possível carregar os detalhes/i)
  })

  it('sends a plain-language question and renders the navigator answer', async () => {
    const navigatorResponse = {
      answer:
        'Encontrei 1 resultado para farmácias com estado aberto perto de Boavista. ' +
        'Farmácia da Boavista (Boavista; estado: aberto; horário: 08:00 - 22:00; telefone: +351220000001).',
      services,
      isFallback: true,
      limitations: ['A disponibilidade em tempo real não é garantida.'],
      sourceContext: {
        source: 'Diretório de serviços verificado da plataforma',
        retrievedAt: '2026-11-17T10:00:00Z',
        allowedFields: ['name', 'currentStatus', 'lastUpdated'],
        recordCount: 1
      }
    }

    global.fetch = vi.fn()
      .mockResolvedValueOnce({ ok: true, json: async () => services })
      .mockResolvedValueOnce({ ok: true, json: async () => navigatorResponse })

    render(<App />)
    await screen.findByText('Farmácia da Boavista')

    fireEvent.change(screen.getByLabelText(/qual é a sua necessidade/i), {
      target: { value: 'farmácias abertas perto da Boavista' }
    })
    fireEvent.click(screen.getByRole('button', { name: /procurar ajuda/i }))

    expect(await screen.findByText(/Encontrei 1 resultado para farmácias/i)).toBeInTheDocument()
    expect(screen.getByText(/a pergunta foi interpretada por palavras-chave/i)).toBeInTheDocument()
    expect(screen.getByTestId('assistant-source-context')).toHaveTextContent(
      /Fonte: Diretório de serviços verificado da plataforma · 1 registo\(s\) verificado\(s\)/
    )
    const assistantResult = screen.getByText('Farmácia da Boavista', { selector: 'span' }).closest('li')
    expect(within(assistantResult).getByRole('link', { name: /ligar para farmácia da boavista/i }))
      .toHaveAttribute('href', 'tel:+351220000001')
    expect(within(assistantResult).getByText(/disponibilidade em tempo real não é garantida.*confirme.*telefone/i))
      .toBeInTheDocument()
    expect(global.fetch).toHaveBeenCalledWith(
      '/api/v1/ai/navigate',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ query: 'farmácias abertas perto da Boavista' })
      })
    )
  })

  it('shows refusal and human contacts for an emergency question', async () => {
    global.fetch = vi.fn()
      .mockResolvedValueOnce({ ok: true, json: async () => services })
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          answer: 'Não posso gerir uma emergência. Ligue 112. Fonte: Orientação de segurança da plataforma.',
          services: [],
          isFallback: false,
          limitations: ['O assistente não substitui serviços de emergência.'],
          sourceContext: {
            source: 'Orientação de segurança da plataforma',
            retrievedAt: '2026-11-25T10:00:00Z',
            allowedFields: [],
            recordCount: 0
          },
          refusal: {
            category: 'EMERGENCY',
            reason: 'Não posso gerir uma emergência. Ligue imediatamente para o 112.',
            contacts: [
              { label: 'Emergência', phoneNumber: '112', uri: 'tel:112' },
              { label: 'SNS 24', phoneNumber: '808242424', uri: 'tel:808242424' }
            ]
          }
        })
      })

    render(<App />)
    await screen.findByText('Farmácia da Boavista')

    fireEvent.change(screen.getByLabelText(/qual é a sua necessidade/i), {
      target: { value: 'é uma emergência' }
    })
    fireEvent.click(screen.getByRole('button', { name: /procurar ajuda/i }))

    const refusal = await screen.findByTestId('assistant-refusal')
    expect(refusal).toHaveTextContent(/não posso gerir uma emergência/i)
    expect(within(refusal).getByRole('link', { name: /emergência \(112\)/i })).toHaveAttribute('href', 'tel:112')
    expect(within(refusal).getByRole('link', { name: /sns 24/i })).toHaveAttribute('href', 'tel:808242424')
    expect(screen.queryByText(/modo de contingência/i)).not.toBeInTheDocument()
  })

  it('offers the standard search when the assistant is unavailable without retaining an old answer', async () => {
    global.fetch = vi.fn()
      .mockResolvedValueOnce({ ok: true, json: async () => services })
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({ answer: 'Resposta anterior', services: [] })
      })
      .mockResolvedValueOnce({ ok: false, status: 503 })
      .mockResolvedValueOnce({ ok: true, json: async () => services })

    render(<App />)
    await screen.findByText('Farmácia da Boavista')
    fireEvent.change(screen.getByLabelText(/qual é a sua necessidade/i), {
      target: { value: 'farmácias' }
    })
    fireEvent.click(screen.getByRole('button', { name: /procurar ajuda/i }))
    expect(await screen.findByText('Resposta anterior')).toBeInTheDocument()

    fireEvent.click(screen.getByRole('button', { name: /procurar ajuda/i }))
    expect(await screen.findByRole('alert')).toHaveTextContent(/assistente.*indisponível/i)
    expect(screen.queryByText('Resposta anterior')).not.toBeInTheDocument()
    expect(screen.getByRole('link', { name: /usar a pesquisa normal/i }))
      .toHaveAttribute('href', '#search-title')

    fireEvent.change(screen.getByLabelText('Zona'), { target: { value: 'Boavista' } })
    fireEvent.click(screen.getByRole('button', { name: /^pesquisar$/i }))
    expect(await screen.findByText('Farmácia da Boavista')).toBeInTheDocument()
    expect(global.fetch).toHaveBeenLastCalledWith('/api/v1/services?coarseLocation=Boavista')
  })

  it('shows the standard search fallback when the assistant times out', async () => {
    global.fetch = vi.fn()
      .mockResolvedValueOnce({ ok: true, json: async () => services })
      .mockImplementationOnce((_url, { signal }) => new Promise((_resolve, reject) => {
        signal.addEventListener('abort', () => reject(new DOMException('Aborted', 'AbortError')))
      }))

    render(<App />)
    await screen.findByText('Farmácia da Boavista')
    vi.useFakeTimers()
    fireEvent.change(screen.getByLabelText(/qual é a sua necessidade/i), {
      target: { value: 'farmácias' }
    })
    fireEvent.click(screen.getByRole('button', { name: /procurar ajuda/i }))
    await act(async () => {
      await vi.advanceTimersByTimeAsync(8000)
    })

    expect(screen.getByRole('alert')).toHaveTextContent(/demorou demasiado tempo/i)
    expect(screen.getByRole('link', { name: /usar a pesquisa normal/i }))
      .toHaveAttribute('href', '#search-title')
    expect(screen.getByRole('button', { name: /procurar ajuda/i })).toBeEnabled()
  })
})
