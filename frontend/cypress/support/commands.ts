// ***********************************************
// This example namespace declaration will help
// with Intellisense and code completion in your
// IDE or Text Editor.
// ***********************************************
// declare namespace Cypress {
//   interface Chainable<Subject = any> {
//     customCommand(param: any): typeof customCommand;
//   }
// }
//
// function customCommand(param: any): void {
//   console.warn(param);
// }
//
// NOTE: You can use it like so:
// Cypress.Commands.add('customCommand', customCommand);
//
// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
Cypress.Commands.add('login', () => {
    cy.visit('/login');

    if (Cypress.env('MOCK_API') === 'true') {
        cy.intercept(
            {
                method: 'POST',
                url:  '/api/auth/login'
            }, 
            req => {
                console.log('salut');
                req.reply({
                    email: 'user@example.com',
                    username: 'username',
                    role: 'USER',
                  })
            }
        ).as('login');
    
        cy.intercept(
            {
              method: 'GET',
              url: '/api/jobs?page=0&itemsPerPage=10&sort=createdAt,desc',
              times: 1
            },
            (req) => { req.reply([]);}
        ).as('jobs')
    }
  
    cy.get('input[formControlName=email]').type("user@example.com");
    cy.get('input[formControlName=password]').type("password{enter}{enter}");
    cy.url().should('include', '/jobs');
  });