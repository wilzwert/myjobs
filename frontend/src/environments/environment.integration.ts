// should be used for e2e, e2e:ci
// in local context, env var API_URL should be set only if the dev / local backend doesn't run on localhost:8080
// in staging workflow, API_URL is set by the github action

export const environment = {
    prod: false,
    recaptcha_key: process.env["GOOGLE_RECAPTCHA_SITE_KEY"] || 'TODO_INTEGRATION_RECAPTCHA_KEY',
    apiUrl: process.env["API_URL"] || 'http://localhost:8080'
};