export const environment = {
    prod: false,
    recaptcha_key: process.env["GOOGLE_RECAPTCHA_SITE_KEY"] || 'TODO_INTEGRATION_RECAPTCHA_KEY',
    apiUrl: process.env["API_URL"] || 'http://localhost:8080'
};