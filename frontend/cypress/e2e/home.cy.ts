describe('Home page', () => {
  it('Visits the home page', () => {
    cy.visit('/')
    cy.get('h1').should('exist')
  })
})
