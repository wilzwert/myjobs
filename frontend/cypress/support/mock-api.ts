/// <reference types="cypress" />

export function setupMockApi() {
    cy.intercept('GET', '/api/jobs', { fixture: 'jobs.json' }).as('getJobs')
  
    cy.intercept('POST', '/api/jobs', {
      statusCode: 201,
      body: { id: 'mock-job-id', message: 'Job created (mock)' },
    }).as('createJob')
}
  