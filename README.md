# eWallet

Welcome to the eWallet repository! 

This repository contains the source code and documentation for a Java-based electronic wallet application. The eWallet application allows users to manage their digital transactions, store virtual currencies, and perform various financial operations in a secure and convenient manner.

## Features

- User Registration and Authentication: Users can create an account and securely log in to the eWallet application. Authentication mechanisms are implemented to ensure the privacy and security of user data.

- Wallet Management: Users can create and manage multiple wallets to store different types of virtual currencies or digital assets. The application provides functionalities to view wallet balances, and transaction history, and perform transactions between wallets.

- Transaction History: The eWallet application maintains a comprehensive transaction history for each user. Users can review their transaction details, including transaction type, amount, date, and associated wallets.

- Funds Transfer: Users can transfer funds between their own wallets or send money to other registered users of the eWallet application. The transfer process includes validation checks to ensure sufficient funds and transaction security.

- Security Measures: The application incorporates robust security measures, including encryption of sensitive user data, secure password hashing, and protection against common vulnerabilities like SQL injection and cross-site scripting (XSS).

## Installation
To set up the eWallet application locally(temporary, later it'll be dockerized), follow these steps:

1. Clone the repository:

```bash
git clone https://github.com/isvyry/ewallet.git
```

2. Set up your Java development environment (e.g., JDK, IDE).

3. Open the cloned project in your preferred Java IDE.

4. Configure the database connection by updating the appropriate properties file (e.g., src/main/resources/application.properties) with your database credentials.

5. Build the project using your IDE's build tools or by running the appropriate command.

6. Run the application using your IDE's run configuration or by executing the generated JAR file.

7. Open your web browser and access the application at http://localhost:8080 (or the appropriate port specified in your configuration).

## Usage
Once the application is set up and running, follow these instructions to use the eWallet application:

- Create a new account by registering with your details.

- Log in to your account using the registered credentials.

- Create one or more wallets to store virtual currency.

- Perform transactions, such as transfers between wallets or sending money to other users.

- Monitor your transaction history and wallet balances to keep track of your financial activities.

## Contributing
Contributions to the eWallet project are welcome! If you'd like to contribute, please follow these steps:

1. Fork the repository and clone it to your local machine.

2. Create a new branch for your feature or bug fix:

```bash
git checkout -b my-feature
```
3. Make the necessary code changes and ensure that the existing tests pass.

4. Commit your changes:

```bash
git commit -m "Add new feature"
```
5. Push your changes to your forked repository:

```bash
git push origin my-feature
```
6. Create a new pull request detailing your changes for review.
