import { defineConfig } from 'cypress'
import * as dotenv from 'dotenv'

// charge les variables d'environnement du système
dotenv.config()

export default defineConfig({
  e2e: {
    baseUrl: process.env.CYPRESS_BASE_URL || 'http://localhost:4201',
    supportFile: 'cypress/support/e2e.ts',
    fixturesFolder: 'cypress/fixtures',
    specPattern: 'cypress/e2e/**/*.cy.ts', // pour tes tests
    setupNodeEvents(on, config) {
      return require('@cypress/code-coverage/task')(on, config)
    }
  },
})