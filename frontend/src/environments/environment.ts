export const environment = {
    prod: (window as any)['env']?.prod ?? false,
    recaptcha_key: (window as any)['env']?.recaptchaKey ?? 'TODO_RECAPTCHA_KEY',
    apiUrl: (window as any)['env']?.apiUrl ?? ""
};