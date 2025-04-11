export function createMock<T>(): jest.Mocked<T> {
    return new Proxy({}, {
      get: (_target, prop) => {
        if (typeof prop === 'string') {
          return jest.fn();
        }
        return null;
      }
    }) as jest.Mocked<T>;
  }
  