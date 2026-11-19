import '@testing-library/jest-dom/vitest'

// Some Node/jsdom combinations do not expose window.localStorage. The app reads the auth token
// from it on mount, so provide an in-memory stand-in for tests.
if (!window.localStorage) {
  const store = new Map()
  Object.defineProperty(window, 'localStorage', {
    configurable: true,
    value: {
      getItem: (key) => (store.has(key) ? store.get(key) : null),
      setItem: (key, value) => store.set(key, String(value)),
      removeItem: (key) => store.delete(key),
      clear: () => store.clear()
    }
  })
}
