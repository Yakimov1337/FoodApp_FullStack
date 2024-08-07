const fs = require('fs');
const path = require('path');

//VERCEL DEPLOY SCRIPT TO EXECUTE ON BUILD SO ENV GETS POPULATED
// Define the path to the environment file
const targetPath = path.join(__dirname, './src/environments/environment.prod.ts');

// Parse the JSON strings from environment variables
const adminCredentials = JSON.parse(process.env.adminCredentials);
const moderatorCredentials = JSON.parse(process.env.moderatorCredentials);

// Environment file content, ensuring the variable names match your Vercel setup
const envConfigFile = `export const environment = {
  production: true,
  apiUrl: '${process.env.apiUrl}',
  stripePublishKey: '${process.env.stripePublishKey}',
  adminCredentials: {
    email: '${adminCredentials.email}',
    password: '${adminCredentials.password}',
  },
  moderatorCredentials: {
    email: '${moderatorCredentials.email}',
    password: '${moderatorCredentials.password}',
  },
};
`;

// Write the environment file
fs.writeFile(targetPath, envConfigFile, 'utf8', (err) => {
  if (err) {
    return console.log(err);
  }

  console.log(`Environment variables were successfully written to ${targetPath}`);
});
