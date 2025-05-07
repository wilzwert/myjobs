console.log('loading test environment');
export const environment = {
    prod: false,
    recaptcha_key: process.env["GOOGLE_RECAPTCHA_SITE_KEY"] || 'TODO_TEST_RECAPTCHA_KEY',
    apiUrl: ''  // in test environment http calls are mocked, no apiUrl is expected
};