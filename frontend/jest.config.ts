import type { Config } from 'jest';
import presets from 'jest-preset-angular/presets';

export default {
  ...presets.createCjsPreset(),
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  coverageDirectory: './coverage/jest',

  moduleNameMapper: {
    // replace environment with test environment
    '^@environments/(.*)$': '<rootDir>/src/environments/environment.test.ts',
    '^@app/(.*)$': '<rootDir>/src/app/$1',
    '^@core/(.*)$': '<rootDir>/src/app/core/$1',
    '^@features/(.*)$': '<rootDir>/src/app/features/$1',
    '^@layout/(.*)$': '<rootDir>/src/app/layout/$1',
    '^@lang/(.*)$': '<rootDir>/src/lang/$1',
  },
} satisfies Config;