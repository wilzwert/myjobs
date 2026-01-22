const fs = require('fs');
const path = require('path');
const dotenv = require('dotenv');

dotenv.config({ path: path.resolve(process.cwd(), '.env') });

const envVars = {
  prod: process.env.PROD === 'true' || false,
  apiUrl: process.env.API_URL || '',
  recaptchaKey: process.env.GOOGLE_RECAPTCHA_SITE_KEY || '',
};

const content = `// generated file, do not commit
window['env'] = ${JSON.stringify(envVars, null, 2)};
`;

const targetPath = path.resolve(process.cwd(), 'src/assets/env.js');

fs.writeFileSync(targetPath, content, { encoding: 'utf-8' });
console.log(`✅  assets/env.js generated at ${targetPath}`);
