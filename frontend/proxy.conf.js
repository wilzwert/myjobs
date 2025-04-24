require('dotenv').config({systemvars: true});

console.log('CONFIGURING PROXY');

const process = require("process");
console.log(process.env);
const api_target = process.env["API_TARGET"] || 'http://localhost:8080';
const secure = process.env["API_SECURE"] || true;
console.log('Proxy configuration: ', api_target, secure);
const PROXY_CONFIG = {
  "/api/*": {
    target: `${api_target}`,
    secure: secure,
    changeOrigin: true,
    logLevel: "debug"
  }
}
module.exports = PROXY_CONFIG;
console.log(PROXY_CONFIG);