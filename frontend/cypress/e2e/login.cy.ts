describe('Login', () => {
  it('Login successfull', () => {
    // see command.ts
    cy.login();
  })
  /*
  it('Login failed', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401
    })
   
    cy.get('input[formControlName=email]').type("test@example.com")
    cy.get('input[formControlName=password]').type("password{enter}{enter}")
    cy.get('p.error').should('contain', 'An error occurred');

  })
  
  it('Login successful after failed attempts', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401
    }).as('loginFailure')

    cy.get('input[formControlName=email]').type("test");
    cy.get('input[formControlName=password]').type("password");
    cy.get('input[formControlName=email]').should('have.class', 'ng-invalid');
    cy.get('input[formControlName=password]').should('have.class', 'ng-valid');
    cy.get('button[type=submit]').should('be.disabled');

    cy.get('input[formControlName=password]').clear();
    cy.get('input[formControlName=email]').type("test@example.com{enter}{enter}");
    cy.get('input[formControlName=email]').should('have.class', 'ng-valid');
    cy.get('input[formControlName=password]').should('have.class', 'ng-invalid');
    cy.get('button[type=submit]').should('be.disabled');

    cy.wait('@loginFailure');

    cy.get('p.error').should('be.visible').and('contain', 'An error occurred');

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    }).as('loginSuccess')

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    
    cy.get('input[formControlName=email]').clear();
    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=email]').should('have.class', 'ng-valid');
    cy.get('input[formControlName=password]').type("test!1234");
    cy.get('input[formControlName=password]').should('have.class', 'ng-valid');
    cy.get('button[type=submit]').should('be.enabled');
    cy.get('input[formControlName=password]').type("{enter}{enter}");

    cy.wait('@loginSuccess');
    
    cy.url().should('to.match', /\/sessions$/)
  })*/
});