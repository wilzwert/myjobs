// in development environment, especially with ng serve only one locale is built
// in this case we mustn't try to redirect the user based on their language
// the only way we can do that is by telling the app no langs are available
export const AVAILABLE_LANGS: String[] = [];