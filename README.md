# RESTful Account and Currency Exchange Service

## Overview
This application is designed as a RESTful service that allows users to manage accounts and perform currency exchanges between PLN and USD. It provides robust functionality, including account creation, balance retrieval, and currency conversion based on current exchange rates from an external provider (NBP API).

## Key Functionalities
- **Account Management**: Users can create accounts with initial balances in PLN and USD.
- **Currency Exchange**: Allows conversion between PLN and USD with live rates.
- **Error Handling**: Comprehensive error handling for invalid account states, insufficient funds, and missing currencies.
- **Validation**: Ensures requests meet required criteria for account creation and currency operations.

## Technologies Used
- **Spring Boot 3.3.4**: Core framework for application and dependency injection.
- **Spring Data JPA**: Handles database interactions with a PostgreSQL database.
- **Spring Validation**: Validates incoming request payloads.
- **MapStruct**: Converts entities to DTOs.
- **OpenAPI/Swagger**: Provides API documentation and testing interface.
- **Feign Client**: Integrates with NBP API to retrieve current currency exchange rates.
- **Testcontainers**: Manages PostgreSQL instances for testing purposes in a containerized environment.
- **Docker and Docker Compose**: Containerize the application and set up a consistent development environment.

## Approach to Problem Solving

### 1. Service Structure
- **AccountService**: Manages account-related operations including creation and retrieval.
- **CurrencyExchangeService**: Manages exchange between PLN and USD currencies, with validation for account existence and sufficient balance.
- **AccountRequestValidator**: Validates request payloads for completeness and validity, ensuring at least one PLN account, no duplicates, and non-negative balances.

### 2. Database and Persistence
- Used PostgreSQL as the main database, configured via Docker Compose, enabling consistent local and production-like environments.
- JPA annotations for entity modeling and relationship management.

### 3. Error Handling
- Custom exception classes provide descriptive error messages.
- A global exception handler delivers structured JSON responses for different HTTP status codes.

### 4. Testing Approach
- **Unit Tests**: Mocked dependencies to isolate service logic.
- **Integration Tests**: Employed Testcontainers for database integration, verifying actual service behavior against the database and Feign client interactions.

# Running the Application

## Prerequisites
- **Docker**: Ensure Docker and Docker Compose are installed for a consistent environment setup.

# Steps

1. **Clone the repository**
2. **Start the application**: In the project root, run: ```docker-compose up --build```
3. **Access Swagger UI**: Open http://localhost:8080/swagger-ui/index.html#/

# Example Manual Tests (Swagger UI)

To test the application manually via Swagger UI, you can use the following endpoints and example requests.

# API Endpoints and Example Requests

## 1. Create a New Account
- **Endpoint**: `POST /api/accounts`
- **Description**: Creates a new account with an initial balance in PLN and optionally other currencies.

- **Request Payload**:
    ```json
    {
      "firstName": "John",
      "lastName": "Doe",
      "currencyAccounts": [
        { "symbol": "PLN", "balance": 1000.00 },
        { "symbol": "USD", "balance": 250.00 }
      ]
    }
    ```
- **Expected Response**: `201 Created`
- **Response Body**: Account UUID.

- **Validation Scenario**: Attempting to create an account without a PLN balance will result in a `400 Bad Request` with the following error message:
    ```json
    {
      "status": "BAD_REQUEST",
      "message": "Account must include an initial balance in PLN."
    }
    ```

## 2. Retrieve Account Details
- **Endpoint**: `GET /api/accounts/{accountId}`
- **Path Variable**: `accountId` (UUID of an existing account)
- **Description**: Retrieves details of an account, including balances in different currencies.

- **Expected Response**: `200 OK`
- **Response Body**:
    ```json
    {
      "firstName": "John",
      "lastName": "Doe",
      "currencyAccounts": [
        { "symbol": "PLN", "balance": 1000.00 },
        { "symbol": "USD", "balance": 250.00 }
      ]
    }
    ```

- **Error Scenario**: Attempting to retrieve an account with a non-existent `accountId` will result in a `404 Not Found` with the following message:
    ```json
    {
      "status": "NOT_FOUND",
      "message": "There is no account with id : '{accountId}'"
    }
    ```

## 3. Perform Currency Exchange
- **Endpoint**: `POST /api/currency-exchange/{accountId}/exchange`
- **Path Variable**: `accountId`
- **Parameters**:
    - `amount`: Amount to exchange, e.g., `200.00`
    - `fromCurrency`: Currency to exchange from (e.g., PLN)
    - `toCurrency`: Currency to exchange to (e.g., USD)
- **Description**: Exchanges a specified amount from one currency to another within the account.

- **Expected Response**: `204 No Content`
- **Response**: No body, but the account's balances will update. Verify using the `/api/accounts/{accountId}` endpoint.

- **Error Scenarios**:
    - **Insufficient Funds**: If the account does not have enough funds in the `fromCurrency` account, expect a `400 Bad Request` with the message:
        ```json
        {
          "status": "BAD_REQUEST",
          "message": "Insufficient funds in the account."
        }
        ```
    - **Invalid Account**: If the account does not exist or lacks the specified currency account, expect a `404 Not Found` with a relevant message.

## 4. Retrieve Account Balance
- **Endpoint**: `GET /api/currency-exchange/{accountId}/balance/{symbol}`
- **Path Variables**:
    - `accountId`: UUID of the account.
    - `symbol`: Currency symbol, either PLN or USD.
- **Description**: Retrieves the current balance of a specific currency within an account.

- **Expected Response**: `200 OK`
- **Response Body**:
    ```json
    {
      "balance": 1000.00
    }
    ```

- **Error Scenario**: Attempting to retrieve a balance for a currency not associated with the account will result in a `404 Not Found` with the error message:
    ```json
    {
      "status": "NOT_FOUND",
      "message": "There is no currency account with accountId : '{accountId}' and symbol : 'PLN'"
    }
    ```

These tests cover common user actions and error scenarios, ensuring that account creation, retrieval, and currency exchange functionalities work as expected and are validated properly. For additional exploration of specific cases, Swagger UI provides an interactive way to test these endpoints and view detailed responses.


# Future Improvements

- **Enhanced Rate Caching**: Implement a short-lived cache for currency rates to reduce API calls and increase performance.
- **Audit Logs**: Track user operations, providing transparency and better analysis options for currency exchanges.
- **Security**: Add authentication and authorization mechanisms for securing endpoints.
- **Asynchronous Processing**: Convert currency operations to asynchronous tasks for better user experience in high-load scenarios.

ui.html to interact with the API.