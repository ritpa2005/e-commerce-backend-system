# E-Commerce Backend System

A production-grade backend system for an e-commerce platform built using Java and Spring Boot.  
The system manages users, products, shopping carts, orders, payments, and inventory, and exposes **RESTful APIs** that can be consumed by web or mobile applications.

---

# Business Overview

The backend system manages online shopping operations such as:

- User registration and login
- Product listing and inventory
- Shopping cart management
- Order placement and tracking
- Payment processing

The system exposes **REST APIs** that can be integrated with web or mobile frontend applications.

---

# Technical Stack

| Component | Technology |
|-----------|------------|
| Language | Java 17+ |
| Framework | Spring Boot 3+ |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL |
| API Type | RESTful API |
| Build Tool | Maven |
| Security | Spring Security + JWT |
| Logging | SLF4J / Logback |
| Testing | JUnit 5, Mockito |
| Containerization | Docker Compose |
| API Documentation | Swagger / OpenAPI |

---

# Project Architecture

### Package Structure

com.incture.e_commerce
┣ controller
┣ service
┣ repository
┣ entity
┣ dto
┣ exception
┣ config
┗ utils

### Layer Responsibilities

**Controller**
- Handles HTTP requests
- Validates input
- Returns API responses

**Service**
- Contains business logic
- Handles application workflows

**Repository**
- Data access layer
- Uses Spring Data JPA

**Entity**
- Database models

**DTO**
- Request and response objects
- Prevents exposing internal entities

---

# Core Features

## User Management

- User registration and login
- Profile update and password change
- Role-based access control
- Admin can manage users

Supported roles: ADMIN, CUSTOMER

---

## Product Management

Admin can:
- Add products
- Update products
- Delete products

Customers can:
- View product listings
- View paginated results

---

## Shopping Cart

Each user maintains their own cart.

Features include:
- Add products to cart
- Remove products from cart
- Update quantity
- View total price before checkout

---

## Order Management

Cart items are converted into orders during checkout.

Possible statuses: PLACED, SHIPPED, DELIVERED, CANCELLED

Customers can also view order history.

---

## Payment Management (Simulation)

The system simulates a payment gateway.

Possible payment statuses: PENDING, SUCCESSFUL, UNSUCCESSFUL, CANCELLED

---

## Inventory Management

Inventory is managed automatically:
- Stock decreases after successful order
- Checkout is prevented if stock is insufficient

---

# Database Schema

## User

| Field | Description |
|------|-------------|
| id | Primary Key |
| name | User name |
| email | Unique email |
| password | Encrypted password |
| role | ADMIN or CUSTOMER |

---

## Product

| Field | Description |
|------|-------------|
| id | Primary Key |
| name | Product name |
| description | Product description |
| price | Product price |
| stock | Available stock |
| category | Product category |
| image_url | Product image |
| rating | Product rating |

---

## Cart

| Field | Description |
|------|-------------|
| id | Primary Key |
| user_id | FK to User |
| total_price | Total cart value |

---

## CartItem

| Field | Description |
|------|-------------|
| id | Primary Key |
| cart_id | FK to Cart |
| product_id | FK to Product |
| quantity | Product quantity |

---

## Order

| Field | Description |
|------|-------------|
| id | Primary Key |
| user_id | FK to User |
| total_amount | Order total |
| order_date | Timestamp |
| payment_status | Payment result |
| order_status | Order state |

---

## OrderItem

| Field | Description |
|------|-------------|
| id | Primary Key |
| order_id | FK to Order |
| product_id | FK to Product |
| quantity | Ordered quantity |
| price | Product price at purchase |

---

# API Endpoints

## User APIs

| Method | Endpoint | Description |
|------|-----------|-------------|
POST | `/api/users/register` | Register user |
POST | `/api/users/login` | Login |
GET | `/api/users/{id}` | Get user |
PUT | `/api/users/{id}` | Update user |
DELETE | `/api/users/{id}` | Delete user (Admin) |

---

## Product APIs

| Method | Endpoint | Description |
|------|-----------|-------------|
POST | `/api/products` | Add product (Admin) |
GET | `/api/products` | Get all products |
GET | `/api/products/{id}` | Get product |
PUT | `/api/products/{id}` | Update product (Admin) |
DELETE | `/api/products/{id}` | Delete product (Admin) |

---

## Cart APIs

| Method | Endpoint | Description |
|------|-----------|-------------|
POST | `/api/cart/add/{productId}` | Add product to cart |
PUT | `/api/cart/update/{productId}` | Update quantity |
DELETE | `/api/cart/remove/{productId}` | Remove product |
GET | `/api/cart` | View cart |

---

## Order APIs

| Method | Endpoint | Description |
|------|-----------|-------------|
POST | `/api/orders/checkout` | Checkout cart |
GET | `/api/orders` | View order history |
GET | `/api/orders/{id}` | Get order |
PUT | `/api/orders/{id}/status` | Update order status (Admin) |

---

# How to Run the Project Locally

### 1. Clone Repository

```bash
git clone https://github.com/your-username/ecommerce-backend.git
```

### 2. Configure Database

Update application.properties
```
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### 3. Build Project
```
mvn clean install
```

### 4. Run Application
```
mvn spring-boot:run
```

Application runs at:  http://localhost:8080


## API Documentation

Swagger UI:  http://localhost:8080/swagger-ui.html

## Running Tests
```
mvn test
```

## Postman Collection

Access the Postman Collection for this project here: https://url.enigmavssut.in/ttH5WeD

