# Banking API

A comprehensive REST API for banking operations built with Java and Spring Boot.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Error Handling](#error-handling)
- [Development](#development)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Overview

Banking API is a robust REST API designed to handle core banking operations including account management, fund transfers, transaction history, and customer information management. The API follows RESTful principles and provides secure, scalable endpoints for financial transactions.

## Features

- **Account Management**: Create, view, and manage bank accounts
- **Fund Transfers**: Secure inter-bank and intra-bank fund transfers
- **Transaction History**: Track all account transactions with detailed information
- **Customer Management**: Manage customer profiles and personal information
- **Balance Inquiry**: Real-time account balance queries
- **Deposit & Withdrawal**: Process deposits and withdrawals
- **Interest Calculation**: Automatic interest calculation for savings accounts
- **Security**: JWT-based authentication and role-based access control
- **Audit Logging**: Comprehensive audit trail for all transactions

## Technology Stack

- **Java**: 11+ (92% of codebase)
- **Framework**: Spring Boot 2.7+
- **Build Tool**: Maven
- **Database**: MySQL/PostgreSQL
- **Security**: Spring Security with JWT
- **API Documentation**: Swagger/OpenAPI
- **Logging**: SLF4J with Logback
- **Frontend**: HTML5 (8% of codebase)

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- Maven 3.6 or higher
- MySQL 5.7+ or PostgreSQL 10+
- Git

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/MAROOTS/bankingapi.git
cd bankingapi
```

### 2. Configure Database

Create a MySQL database:

```sql
CREATE DATABASE banking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Update Application Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/banking_db
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  application:
    name: banking-api
```

### 4. Build the Application

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## Configuration

### Application Properties

Key configuration properties in `application.yml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api/v1

logging:
  level:
    root: INFO
    com.banking: DEBUG

jwt:
  secret: your-secret-key-here
  expiration: 86400000  # 24 hours in milliseconds

banking:
  transaction:
    daily-limit: 100000.00
    transfer-fee: 50.00
  interest:
    savings-rate: 0.04
    current-rate: 0.01
```

### Security Configuration

The API uses JWT tokens for authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | User login |
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/logout` | User logout |
| POST | `/api/v1/auth/refresh` | Refresh JWT token |

### Account Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/accounts` | List all accounts |
| GET | `/api/v1/accounts/{accountId}` | Get account details |
| POST | `/api/v1/accounts` | Create new account |
| PUT | `/api/v1/accounts/{accountId}` | Update account information |
| DELETE | `/api/v1/accounts/{accountId}` | Close account |
| GET | `/api/v1/accounts/{accountId}/balance` | Get account balance |

### Transactions

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/transactions` | Get all transactions |
| GET | `/api/v1/transactions/{transactionId}` | Get transaction details |
| GET | `/api/v1/accounts/{accountId}/transactions` | Get account transactions |
| POST | `/api/v1/transactions/transfer` | Transfer funds |
| POST | `/api/v1/transactions/deposit` | Deposit funds |
| POST | `/api/v1/transactions/withdraw` | Withdraw funds |

### Customer Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/customers` | List all customers |
| GET | `/api/v1/customers/{customerId}` | Get customer details |
| POST | `/api/v1/customers` | Create new customer |
| PUT | `/api/v1/customers/{customerId}` | Update customer information |
| DELETE | `/api/v1/customers/{customerId}` | Delete customer |

## Authentication

### Login Request

```json
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "user@banking.com",
  "password": "password123"
}
```

### Login Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000,
  "message": "Login successful"
}
```

## Error Handling

The API uses standard HTTP status codes and returns errors in the following format:

```json
{
  "timestamp": "2024-06-22T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid account number",
  "path": "/api/v1/accounts/123"
}
```

### Common Error Codes

| Status | Error | Description |
|--------|-------|-------------|
| 400 | Bad Request | Invalid request parameters |
| 401 | Unauthorized | Missing or invalid authentication |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource already exists |
| 500 | Internal Server Error | Server error |

## Development

### Project Structure

```
bankingapi/
├── src/
│   ├── main/
│   │   ├── java/com/banking/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── security/
│   │   │   └── config/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
└── README.md
```

### Running Tests

```bash
mvn test
```

### Building for Production

```bash
mvn clean package -DskipTests
java -jar target/bankingapi-1.0.0.jar
```

## Troubleshooting

### Database Connection Error
- Verify MySQL/PostgreSQL is running
- Check database credentials in `application.yml`
- Ensure database exists

### Port 8080 Already in Use
```bash
# Change port in application.yml or use
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Authentication Failures
- Verify JWT secret key is configured
- Check token expiration
- Ensure Authorization header format is correct

### Transaction Failures
- Verify account balance is sufficient
- Check transaction daily limits
- Ensure both accounts exist and are active

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style Guidelines

- Follow Java naming conventions
- Use meaningful variable names
- Write Javadoc for public methods
- Keep methods focused and concise
- Add unit tests for new features

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or suggestions, please open an issue on the GitHub repository or contact the development team.

---

**Last Updated**: June 2026
**Version**: 1.0.0
