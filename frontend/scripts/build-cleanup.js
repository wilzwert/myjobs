#!/usr/bin/env node
const fs = require('fs');
const path = require('path');

// Liste des fichiers à supprimer / actions à ajouter plus tard
const filesToRemove = [
  path.join(__dirname, '..', 'src/assets/env.js')
];

filesToRemove.forEach((filePath) => {
  if (fs.existsSync(filePath)) {
    fs.unlinkSync(filePath);
    console.log(`✅ Removed: ${filePath}`);
  } else {
    console.log(`⚠️  File not found, skipping: ${filePath}`);
  }
});

console.log('Build cleanup done!');
