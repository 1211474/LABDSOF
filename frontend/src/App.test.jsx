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
    vi.useRealTimers()
    vi.restoreAllMocks()
  })

  it('renders services returned by the Care Platform API', async () => {
    render(<App />)

    expect(screen.getByRole('heading', { name: /carebnb/i, level: 1 })).toBeInTheDocument()
    expect(await screen.findByText('Farmácia da Boavista')).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /ligar para farmácia da boavista/i }))
      .toHaveAttribute('href', 'tel:+351220000001')
    expect(screen.getByText('Fonte oficial verificada')).toBeInTheDocument()
    expect(screen.getByText('Diretório oficial da plataforma', { selector: 'dd' })).toBeInTheDocument()
    const result = screen.getByRole('heading', { name: 'Farmácia da Boavista' }).closest('article')
    expect(within(result).getByText(/disponibilidade em tempo real não é garantida.*confirme.*telefone/i))
      .toBeInTheDocument()
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
