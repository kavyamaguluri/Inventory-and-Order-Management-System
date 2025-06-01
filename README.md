# Spring Boot E-Commerce API

A RESTful API built with Spring Boot for managing an e-commerce system with user authentication, item management, and order processing.

## Features

- **User Authentication & Authorization**: JWT-based authentication with role-based access control
- **Item Management**: CRUD operations for inventory items (Admin only)
- **Order Management**: Place orders, view order history
- **Role-based Access**: Admin and Customer roles with different permissions
- **Exception Handling**: Global exception handling with proper error responses
- **Database**: PostgreSQL integration with JPA/Hibernate

## Tech Stack

- **Java 21**
- **Spring Boot 3.5.0**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **PostgreSQL** database
- **Lombok** for boilerplate code reduction
- **Gradle** for build management

## Prerequisites

Before running the application, ensure you have:

- Java 21 or higher
- PostgreSQL database
- Gradle (or use the included Gradle wrapper)

## Database Setup

1. Install PostgreSQL and create a database named `DIATOZDB`
2. Update the database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/DIATOZDB
   spring.datasource.username=postgres
   spring.datasource.password=Kavya@123
   ```

## Installation & Running

1. **Clone the repository**
   ```bash
   git clone <(https://github.com/kavyamaguluri/Inventory-and-Order-Management-System)>
   cd demo
   ```

2. **Build the application**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

The application will start on `http://localhost:8080`

## API Documentation

### Authentication Endpoints

#### Register Admin
- **POST** `/api/auth/register/admin`
- **Body:**
  ```json
  {
    "username": "admin",
    "password": "password123"
  }
  ```
- **Response:**
  ```json
  {
    "token": "jwt_token_here"
  }
  ```

#### Register Customer
- **POST** `/api/auth/register/customer`
- **Body:**
  ```json
  {
    "username": "customer",
    "password": "password123"
  }
  ```

#### Login
- **POST** `/api/auth/login`
- **Body:**
  ```json
  {
    "username": "your_username",
    "password": "your_password"
  }
  ```

### Item Management Endpoints

#### Get All Items
- **GET** `/api/items`
- **Authorization:** Required (Admin or Customer)
- **Response:**
  ```json
  [
    {
      "id": 1,
      "name": "Product Name",
      "quantity": 100,
      "price": 29.99
    }
  ]
  ```

#### Create Item (Admin Only)
- **POST** `/api/items`
- **Authorization:** Admin role required
- **Body:**
  ```json
  {
    "name": "New Product",
    "quantity": 50,
    "price": 19.99
  }
  ```

#### Update Item (Admin Only)
- **PUT** `/api/items/{id}`
- **Authorization:** Admin role required
- **Body:**
  ```json
  {
    "name": "Updated Product",
    "quantity": 75,
    "price": 25.99
  }
  ```

#### Delete Item (Admin Only)
- **DELETE** `/api/items/{id}`
- **Authorization:** Admin role required

### Order Management Endpoints

#### Place Order (Customer Only)
- **POST** `/api/orders`
- **Authorization:** Customer role required
- **Body:**
  ```json
  {
    "items": [
      {
        "itemId": 1,
        "quantity": 2
      },
      {
        "itemId": 2,
        "quantity": 1
      }
    ]
  }
  ```

#### Get My Orders (Customer Only)
- **GET** `/api/orders/my`
- **Authorization:** Customer role required

#### Get All Orders (Admin Only)
- **GET** `/api/orders`
- **Authorization:** Admin role required

## Authentication

The API uses JWT (JSON Web Token) for authentication. After logging in or registering, include the token in the Authorization header:

```
Authorization: Bearer your_jwt_token_here
```

## User Roles

- **ADMIN**: Can manage items (create, update, delete) and view all orders
- **CUSTOMER**: Can view items, place orders, and view their own orders

## Error Handling

The API includes comprehensive error handling:

- **400 Bad Request**: Invalid input data
- **401 Unauthorized**: Authentication failed or missing token
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Unexpected server errors

Example error response:
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Item not found with id 999"
}
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/          # Security configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions and handlers
│   │   ├── repository/     # Data repositories
│   │   ├── security/       # JWT utilities and filters
│   │   └── service/        # Business logic services
│   └── resources/
│       └── application.properties  # Configuration
└── test/                   # Test files
```

## Testing

Run tests using:
```bash
./gradlew test
```

## Configuration

Key configuration properties in `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/DIATOZDB
spring.datasource.username=postgres
spring.datasource.password=your_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=your_jwt_secret_key
jwt.expiration.ms=3600000
```

## Security Features

- Password encryption using BCrypt
- JWT token-based authentication
- Role-based authorization
- CSRF protection disabled for REST API
- Stateless session management

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
