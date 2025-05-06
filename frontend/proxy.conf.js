require('dotenv').config({systemvars: true});
const process = require("process");

module.exports = {
  "/api": {
    "target": process.env["API_URL"] || 'http://localhost:8080',
    "changeOrigin": true,
    "secure": process.env["API_SECURE"] === 'true' || true,
    "logLevel": "debug"
  }
};