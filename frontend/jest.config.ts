import type { Config } from 'jest';
import presets from 'jest-preset-angular/presets';

export default {
  ...presets.createCjsPreset(),
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  coverageDirectory: './coverage/jest',

  moduleNameMapper: {
    // replace environment with test environment
    '^(.*)/environments/(.*)$': '<rootDir>/src/environments/environment.test.ts',
  },
} satisfies Config;