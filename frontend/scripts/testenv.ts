const dotenv = require("dotenv");

try {
const env = dotenv.config();
}
catch(e) {

}

console.log(process.env);