describe('Home page', () => {
  it('Visits the home page', () => {
    cy.visit('/')
    cy.get('h1').should('exist')
    /*cy.contains('Se connecter')
    cy.contains("S'inscrire")
    cy.get('app-menu nav').should('not.exist');*/
  })
})
