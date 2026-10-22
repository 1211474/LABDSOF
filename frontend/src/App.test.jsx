import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, render, screen } from '@testing-library/react'
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

    expect(screen.getByRole('heading', { name: /rede de apoio/i })).toBeInTheDocument()
    expect(await screen.findByText('Farmácia da Boavista')).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /ligar para farmácia da boavista/i }))
      .toHaveAttribute('href', 'tel:+351220000001')
    expect(screen.getByText('Fonte oficial verificada')).toBeInTheDocument()
  })
})
