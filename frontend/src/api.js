export async function fetchServices({ type, coarseLocation } = {}) {
  const params = new URLSearchParams()
  if (type) {
    params.set('type', type)
  }
  if (coarseLocation) {
    params.set('coarseLocation', coarseLocation)
  }

  const query = params.toString()
  const response = await fetch(`/api/v1/services${query ? `?${query}` : ''}`)

  if (!response.ok) {
    throw new Error('Não foi possível carregar os serviços.')
  }

  return response.json()
}

export async function askNavigator(query) {
  const response = await fetch('/api/v1/ai/navigate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ query })
  })

  const body = await response.json().catch(() => ({}))

  if (!response.ok) {
    throw new Error(body.error || 'O assistente está temporariamente indisponível.')
  }

  return body
}
