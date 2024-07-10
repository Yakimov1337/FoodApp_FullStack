import { Client, Account, Databases, Storage, Avatars } from "appwrite";
import { environment } from '../../../environments/environment.local';

export const client = new Client();

client
  .setEndpoint(environment.appwriteEndpoint) // Use the endpoint from the environment
  .setProject(environment.appwriteProjectId); // Use the project ID from the environment

export const account = new Account(client);
export const databases = new Databases(client);
export const storage = new Storage(client);
export const avatars = new Avatars(client);
export { ID } from 'appwrite';
