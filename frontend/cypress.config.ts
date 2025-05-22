import { defineConfig } from 'cypress'
const dotenv = require('dotenv').config({systemvars: true})

export default defineConfig({
  
  e2e: {
    projectId: "h8iz18",
    video: true,
    record: true,
    baseUrl: process.env["CYPRESS_BASE_URL"] || 'http://localhost:4200',
    fixturesFolder: 'cypress/fixtures',
    env: {
      MOCK_API: process.env["MOCK_API"] || 'true'
    },
    setupNodeEvents(on, config) {
      return require('@cypress/code-coverage/task')(on, config)
    },
  },
  
})