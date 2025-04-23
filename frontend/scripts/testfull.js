const { exec } = require('child_process');
const path = require('path');

const runCommand = (command) => {
  return new Promise((resolve, reject) => {
    exec(command, (error, stdout, stderr) => {
      if (error) {
        console.error(`Error executing command: ${command}`);
        console.error(stderr);
        resolve();
      } else {
        console.log(stdout);
        resolve();
      }
    });
  });
};

const runTests = async () => {
  try {
    // Run unit and integration tests with coverage
    await runCommand('npm run test:coverage');

    // Run e2e tests
    await runCommand('npm run e2e:ci');

    // Generate e2e coverage
    await runCommand('npm run e2e:coverage');

    // Merge coverage reports
    await runCommand('npm run merge-coverage');

    console.log('All coverage tasks completed successfully!');
  } catch (error) {
    console.error('Coverage tasks failed:', error);
    process.exit(1);
  }
};

runTests();