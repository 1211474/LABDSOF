import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import App from './App.jsx'

const services = [
  {
    id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    organisationId: '11111111-1111-1111-1111-111111111111',
    name: 'Farmácia da Boavista',
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
    vi.restoreAllMocks()
  })

  it('renders services returned by the Care Platform API', async () => {
    render(<App />)

    expect(screen.getByRole('heading', { name: /carebnb/i, level: 1 })).toBeInTheDocument()
    expect(await screen.findByText('Farmácia da Boavista')).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /ligar para farmácia da boavista/i }))
      .toHaveAttribute('href', 'tel:+351220000001')
    expect(screen.getByText('Fonte oficial verificada')).toBeInTheDocument()
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
    expect(global.fetch).toHaveBeenCalledWith(
      '/api/v1/ai/navigate',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ query: 'farmácias abertas perto da Boavista' })
      })
    )
  })
})
