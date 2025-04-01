# Car Parts Management System

A Spring Boot application for managing car parts, suppliers, and orders with JWT authentication.

## Features

- User authentication and authorization using JWT
- Role-based access control (ADMIN, USER)
- CRUD operations for car parts
- Supplier management
- Order processing
- RESTful API endpoints
- Comprehensive test coverage

## Technologies Used

- Java 17
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- H2 Database (In-memory)
- JWT (JSON Web Tokens)
- Maven
- JUnit 5
- Mockito

## Prerequisites

- JDK 17 or later
- Maven 3.6 or later
- Git Bash (for Windows users)

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/ievamoo/spring_security_jwt.git
cd spring_security_jwt
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring:boot run
```

The application will be available at `http://localhost:8080`

### H2 Console Access
The H2 in-memory database console is available at `http://localhost:8080/h2-console` when the application is running.
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `admin`
- Password: ``

### Running the Application

#### Using Git Bash (Windows):
1. Open Git Bash
2. Navigate to your project directory:
```bash
cd /c/Users/YourUsername/Desktop/spring security/demo
```
3. Run the application:
```bash
mvn spring:boot run
```

#### Using Command Prompt (Windows):
1. Open Command Prompt
2. Navigate to your project directory:
```cmd
cd C:\Users\YourUsername\Desktop\spring security\demo
```
3. Run the application:
```cmd
mvn spring:boot run
```

## API Endpoints

### Authentication
- `POST /api/register` - Register a new user
- `POST /api/login` - Login and get JWT token

### Car Parts
- `GET /api/car-parts` - Get all car parts
- `GET /api/car-parts/{id}` - Get car part by ID
- `POST /api/car-parts` - Create new car part (ADMIN only)
- `PUT /api/car-parts/{id}` - Update car part (ADMIN only)
- `DELETE /api/car-parts/{id}` - Delete car part (ADMIN only)

### Suppliers
- `GET /api/suppliers` - Get all suppliers
- `GET /api/suppliers/{id}` - Get supplier by ID
- `POST /api/suppliers` - Create new supplier
- `PUT /api/suppliers/{id}` - Update supplier
- `DELETE /api/suppliers/{id}` - Delete supplier

### Orders
- `GET /api/orders` - Get user's orders
- `GET /api/orders/all` - Get all orders (ADMIN only)
- `POST /api/orders` - Create new order

## Testing

The project includes comprehensive unit tests and integration tests. To run the tests:

```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── demo/
│   │               ├── config/         # Configuration classes
│   │               ├── controller/     # REST controllers
│   │               ├── dto/            # Data Transfer Objects
│   │               ├── model/          # Entity classes
│   │               ├── repository/     # JPA repositories
│   │               ├── security/       # Security configuration
│   │               ├── service/        # Business logic
│   │               └── utils/          # Utility classes
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/
            └── example/
                └── demo/
                    ├── controller/     # Controller tests
                    ├── service/        # Service tests
                    └── security/       # Security tests
```

## Security

The application uses JWT for authentication and Spring Security for authorization. Key security features:

- JWT token-based authentication
- Role-based access control
- Password encryption
- Protected endpoints
- Stateless session management





