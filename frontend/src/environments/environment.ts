export const environment = {
    prod: true,
    recaptcha_key: process.env["GOOGLE_RECAPTCHA_SITE_KEY"] || 'TODO_PRODUCTION_RECAPTCHA_KEY',
    apiUrl: process.env["API_URL"] || "https://api.my-jobs.org"
};