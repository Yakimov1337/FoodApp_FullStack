# 🍔 Food Squad 🍽️
Welcome to **Food Squad**, a cutting-edge, fully responsive web application designed to elevate the dining experience. Built with Angular 17 and integrated with Appwrite for backend services, Food Squad caters to food lovers with its dynamic and user-friendly interface. Offering a secure and seamless ordering system, the application also features role-based authentication, ensuring a personalized and secure user journey.

## 🌍 Deployment
- [Vercel Deployment](https://food-app-deploy.vercel.app/)
- [Netlify Deployment](https://foodsquad.netlify.app/)

## 🚀 Features

- **🍏 Fully Responsive Design**: Enjoy a seamless experience across all devices, with a layout that adjusts perfectly to fit your
- **🔍 Theme Customization**: Choose between dark and light themes, with 7 custom color schemes to personalize your app appearance.
- **💳 Secure Stripe Payments**: Integrated Stripe for safe and efficient financial transactions. (use 424242... as a test credit/debit card, everywhere just put 42)
- **📄 Profile Customization**: Users can edit their profiles and upload pictures, making their user experience more personal.

## 🛠️ Tech Stack

- **🔹 Frontend**: Angular 17, Tailwind CSS for styling, with a focus on responsive and adaptive design.
- **🔸 Backend**: Appwrite, ensuring a robust and scalable server-side solution.
- **🌐 Payment Processing**:  Stripe, for secure online transactions.


## 📸 Pages description and Screenshots
### 🔍 Admin Dashboard
 ![Admin dashboard Screenshot](https://i.ibb.co/XSz8YRr/Screenshot-2024-04-03-232830.png)

**🔹Designed for moderators and administrators, the dashboard provides insights into the restaurant's operations and customer preferences:**

- **Overview**: Displays revenue and product performance.
- **CRUD Operations**: Allows for managing Menu Items, Orders, and Users.

### 🏠 Home Page
  ![Home Page Screenshot](https://i.ibb.co/fHgVrP7/Screenshot-2024-04-03-232914.png)

**🔹The Home Page serves as the welcoming interface for users, highlighting the latest offers, top dishes, and features of Food Squad.**

### 🛒 Menu Page
![Menu page Screenshot](https://i.ibb.co/8d4QwpY/Screenshot-2024-04-03-232927.png)

**🔹The Menu Page showcases the variety of food items available, categorized for easy access:**

### ☘️ Categories Selection
 ![Category Page Screenshot](https://i.ibb.co/KNDx6MY/Screenshot-2024-04-03-232943.png)
 
 **🔹The Category Page allows users to browse the menu based on specific food categories, enhancing the user experience by simplifying navigation.**
 
### 👩‍🎓 Role based page
 ![Role Page Screenshot](https://i.ibb.co/CJ3KR6q/Screenshot-2024-04-03-235001.png)
 
 **🔹Our "Role-Based Access" page is designed to streamline user experience by customizing the interface and functionalities according to user roles, ensuring a tailored and efficient navigation through Food Squad.**
 
 
### 🙅‍♂️Sing up Page
 ![Sing up Page Screenshot](https://i.ibb.co/X3F01MD/Screenshot-2024-04-03-234925.png)
 
 **🔹The "Sign Up Page" provides a simple and secure gateway for new users to join Food Squad, enabling them to access personalized features and start their gourmet journey with just a few clicks.**
 
### 🤵️Account settings page
 ![Settings Page Screenshot](https://i.ibb.co/ykwWdY6/Screenshot-2024-04-03-235418.png)
 
 **🔹The "Account Settings" page offers users a centralized hub to manage their personal information, preferences, and security settings, empowering them to customize their Food Squad experience to their liking.**
 
### 💰 Our plans
 
![Plans Screenshot](https://i.ibb.co/kH1zvw4/Screenshot-2024-04-03-232953.png)

**🔹Our Pricing Plans section offers a variety of options tailored to meet the diverse needs of our users, ensuring everyone can enjoy the full benefits of Food Squad with transparent, competitive, and value-driven subscription models.**

## 👤 Permissions Overview

| Feature                | Admin | Moderator |
|------------------------|:-----:|:---------:|
| Delete Menu Items      |  ✔️   |     ❌    |
| Update Default Items   |  ✔️   |     ❌  |
| Update Non-default Items      |  ✔️   |    ✔️ ️️   |
| CRUD Orders            |  ✔️   |     Limited   |
| CRUD Users             |  ✔️   | Limited   |

## 🏠 Routing Logic

| Route                         | Unauthenticated | Authenticated (Normal) | Authenticated (Admin/Moderator) |
|-------------------------------|:---------------:|:-----------------------:|:--------------------------------:|
| /auth/sign-up, /auth/sign-in  |        ✔️       |            ❌           |                 ❌               |
| Home, Contact, About, etc.    |        ❌       |            ✔️           |                 ✔️               |
| Admin Dashboard               |        ❌       |            ❌           |                 ✔️               |


## 📦 Installation and Setup

To set up the project locally, follow these steps:

```bash
1. Create a file named `environment.local.ts` inside `src/environments/`. 
2. Copy the content from `environment.ts` into `environment.local.ts`.
3. Replace the placeholder values in `environment.local.ts` with your actual Appwrite project keys.
4. Make sure to not commit `environment.local.ts` to the repository. This file is already listed in `.gitignore`.
5. npm install
6. ng serve
```


## 🖥️ Usage
This is a student project for education purpose!

This project use Hero Icons and Hero Patterns
