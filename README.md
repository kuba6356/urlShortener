# URL Shortener Service

A Spring Boot application for shortening URLs with user authentication, analytics, and email verification.

## Features

- User registration and authentication with JWT
- Email verification for new accounts
- Password reset functionality
- URL shortening with analytics tracking
- Click tracking with IP and location information
- REST API for all operations

## Technologies

- Java 24
- Spring Boot 3.4.4
- Spring Security
- JWT Authentication
- Hibernate/JPA
- MySQL Database
- Maven

## API Endpoints

### User Management

| Method | Endpoint              | Description                          |
|--------|-----------------------|--------------------------------------|
| POST   | `/register`           | Register a new user                  |
| GET    | `/validate`           | Validate email with token            |
| POST   | `/login`              | User login                           |
| GET    | `/user/{id}`          | Get user details                     |
| PUT    | `/passwordChange`     | Change password                      |
| GET    | `/resetPassword`      | Request password reset               |
| PUT    | `/resetPasswordToken` | Reset password with token            |

### URL Management

| Method | Endpoint        | Description                          |
|--------|-----------------|--------------------------------------|
| POST   | `/create`       | Create a short URL                   |
| PUT    | `/update/{id}`  | Update a URL                         |
| DELETE | `/delete/{id}`  | Delete a URL                         |
| GET    | `/get/{id}`     | Get URL details                      |
| GET    | `/r/{id}`       | Redirect to original URL (tracks analytics) |

## Security Configuration

The application uses JWT for authentication. Protected endpoints require an `Authorization` header with a Bearer token:
Authorization: Bearer <your-jwt-token>



## Getting Started

1. Clone the repository
2. Configure your database in `application.properties`
3. Set up email properties for verification emails
4. Build and run the application:

bash
mvn spring-boot:run

Configuration

Required application.properties settings:
# Database

spring.datasource.url=jdbc:h2:mem:testdb

spring.datasource.driverClassName=org.h2.Driver

spring.datasource.username=sa

spring.datasource.password=

spring.h2.console.enabled=true

# JWT

jwt.secret=your-secret-key-here

# Email

spring.mail.host=smtp.example.com

spring.mail.port=587

spring.mail.username=your-email@example.com

spring.mail.password=your-email-password

spring.mail.properties.mail.smtp.auth=true

spring.mail.properties.mail.smtp.starttls.enable=true

Dependencies

Key dependencies include:

    Spring Boot Starter Web

    Spring Boot Starter Security

    Spring Boot Starter Data JPA

    JJWT (for JWT handling)

    Spring Boot Starter Mail

    MySql (or your preferred database)

Error Handling

The application provides structured error responses for:

    Invalid JWT tokens (401 Unauthorized)

    User not found (404 Not Found)

    Email sending failures (500 Internal Server Error)

    Invalid user access attempts (403 Forbidden)
