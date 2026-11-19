import { afterEach, describe, expect, it, vi } from 'vitest'
import { askNavigator } from './api'

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
