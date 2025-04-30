export const environment = {
    prod: false,
    recaptcha_key: process.env["GOOGLE_RECAPTCHA_SITE_KEY"] || 'TODO_RECAPTCHA_KEY',
    apiUrl: ''  // handled by proxy in dev environment
};