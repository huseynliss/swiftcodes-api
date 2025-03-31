
# SWIFT Code Management System API

## Overview
This project provides a comprehensive REST API for managing SWIFT codes (Bank Identifier Codes) for financial institutions worldwide. The system allows you to store, retrieve, add, and delete SWIFT code information, distinguishing between bank headquarters and their branches.

## Table of Contents
- Features
- Technology Stack
- Setup Instructions
- Prerequisites
- Running with Docker
- Running Locally
- API Documentation
- Endpoints
- Postman Collection
- Testing
- Database Schema
- Troubleshooting
- License

## Features
- **Excel Data Import**: Automatically imports SWIFT codes from an Excel file on startup.
- **Headquarter/Branch Detection**: Identifies headquarters (codes ending with "XXX") and their branches.
- **Country-based Filtering**: Retrieve all SWIFT codes for a specific country.
- **Comprehensive API**: CRUD operations for SWIFT code management.
- **Validation**: Ensures proper SWIFT code format and data integrity.
- **Containerized**: Ready-to-run with Docker and Docker Compose.

## Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.2.3
- **Database**: PostgreSQL 15
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **Testing**: JUnit 5, Mockito, TestContainers

## Setup Instructions

### Prerequisites
- Java 17 JDK
- Maven 3.8+
- Docker 20.10+
- Docker Compose 2.0+
- (Optional) Postman for API testing

### Running with Docker (Recommended)
#### Clone the repository:
```sh
git clone https://github.com/huseynliss/swiftcodes-api.git
cd swiftcodes-api
```
#### Build and start the containers:
```sh
docker-compose -f docker/docker-compose.yml up -d --build
```
The application will be available at:
- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html

To stop the containers:
```sh
docker-compose -f docker/docker-compose.yml down
```

### Running Locally
#### Clone the repository:
```sh
git clone https://github.com/huseynliss/swiftcodes-api.git
cd swiftcodes-api
```
#### Start PostgreSQL in Docker:
```sh
docker run -d --name swift-postgres -p 5432:5432   -e POSTGRES_USER=postgres   -e POSTGRES_PASSWORD=postgres   -e POSTGRES_DB=swift_codes_db   postgres:15-alpine
```
#### Build and run the application:
```sh
./mvnw clean install
./mvnw spring-boot:run
```
The application will be available at:
- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## API Documentation
### Endpoints

#### 1. Get SWIFT Code Details
**URL:** `GET /v1/swift-codes/{swift-code}`

**Description:** Retrieves details of a specific SWIFT code. If the code belongs to a headquarters, it also returns all its branches.

**Example Request:**
```sh
GET http://localhost:8080/v1/swift-codes/BNPAFRPPXXX
```

**Example Response (Headquarters):**
```json
{
  "address": "16 BOULEVARD DES ITALIENS, PARIS",
  "bankName": "BNP PARIBAS",
  "countryISO2": "FR",
  "countryName": "FRANCE",
  "isHeadquarter": true,
  "swiftCode": "BNPAFRPPXXX",
  "branches": [
    {
      "address": "123 CHAMPS ELYSEES, PARIS",
      "bankName": "BNP PARIBAS BRANCH",
      "countryISO2": "FR",
      "isHeadquarter": false,
      "swiftCode": "BNPAFRPP123"
    }
  ]
}
```

#### 2. Get SWIFT Codes by Country
**URL:** `GET /v1/swift-codes/country/{countryISO2code}`

**Example Request:**
```sh
GET http://localhost:8080/v1/swift-codes/country/FR
```

**Example Response:**
```json
{
  "countryISO2": "FR",
  "countryName": "FRANCE",
  "swiftCodes": [
    {
      "address": "16 BOULEVARD DES ITALIENS, PARIS",
      "bankName": "BNP PARIBAS",
      "countryISO2": "FR",
      "isHeadquarter": true,
      "swiftCode": "BNPAFRPPXXX"
    },
    {
      "address": "123 CHAMPS ELYSEES, PARIS",
      "bankName": "BNP PARIBAS BRANCH",
      "countryISO2": "FR",
      "isHeadquarter": false,
      "swiftCode": "BNPAFRPP123"
    }
  ]
}
```

### Postman Collection
You can test the API using our Postman collection. Import the following JSON into Postman:
```json
{
  "info": {
    "_postman_id": "a1b2c3d4-e5f6-7890",
    "name": "SWIFT Code API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Get SWIFT Code Details",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/v1/swift-codes/BNPAFRPPXXX"
      }
    }
  ]
}
```

## Testing
To run the test suite:
```sh
./mvnw test
```

## Database Schema
```sql
CREATE TABLE swift_codes (
    swift_code VARCHAR(11) PRIMARY KEY,
    bank_name VARCHAR(100) NOT NULL,
    address TEXT,
    country_iso2 CHAR(2) NOT NULL,
    country_name VARCHAR(50) NOT NULL,
    is_headquarter BOOLEAN NOT NULL,
    headquarter_code VARCHAR(8),
    time_zone VARCHAR(50)
);
```

## Troubleshooting
### Common Issues
- **Port 8080 already in use:**
  ```sh
  netstat -ano | findstr :8080
  taskkill /PID 12345 /F
  ```
- **Database connection issues:** Verify PostgreSQL is running.

## License
This project is licensed under the MIT License. See the LICENSE file for details.
