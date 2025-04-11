// scripts/merge-coverage.js
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const tmpDir = path.join('coverage', 'tmp');
const mergedReport = path.join('coverage', 'merged-coverage.json');

// Ensure tmp dir exists
if (!fs.existsSync(tmpDir)) {
  fs.mkdirSync(tmpDir, { recursive: true });
}

// Copy coverage files
try {
  fs.copyFileSync('coverage/jest/coverage-final.json', path.join(tmpDir, 'from-jest.json'));
  fs.copyFileSync('coverage/e2e/coverage-final.json', path.join(tmpDir, 'from-e2e.json'));
} catch (err) {
  console.error('❌ Coverage files not found. Make sure both jest and e2e coverage reports exist.');
  process.exit(1);
}

// Merge and generate report
try {
  execSync(`npx nyc merge ${tmpDir} ${mergedReport}`, { stdio: 'inherit' });
  execSync(`npx nyc report --reporter=html --report-dir=coverage/merged --temp-dir=${tmpDir}`, { stdio: 'inherit' });
  console.log('✅ Merged coverage report generated at coverage/merged/index.html');
} catch (err) {
  console.error('❌ Failed to merge or generate coverage report:', err);
  process.exit(1);
}
