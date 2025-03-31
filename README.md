
# SWIFT Code Management System API

## Overview
This project provides a comprehensive REST API for managing SWIFT codes (Bank Identifier Codes) for financial institutions worldwide. The system allows you to store, retrieve, add, and delete SWIFT code information, distinguishing between bank headquarters and their branches.

## Table of Contents
1. [Features](#features)
2. [Technology Stack](#technology-stack)
3. [Setup Instructions](#setup-instructions)
   - [Prerequisites](#prerequisites)
   - [Running with Docker](#running-with-docker)
   - [Running Locally](#running-locally)
4. [API Documentation](#api-documentation)
   - [Endpoints](#endpoints)
   - [Postman Collection](#postman-collection)
5. [Testing](#testing)
6. [Database Schema](#database-schema)
7. [Troubleshooting](#troubleshooting)
8. [License](#license)

## Features
- **Excel Data Import**: Automatically imports SWIFT codes from an Excel file on startup
- **Headquarter/Branch Detection**: Identifies headquarters (codes ending with "XXX") and their branches
- **Country-based Filtering**: Retrieve all SWIFT codes for a specific country
- **Comprehensive API**: CRUD operations for SWIFT code management
- **Validation**: Ensures proper SWIFT code format and data integrity
- **Containerized**: Ready-to-run with Docker and Docker Compose

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
- **Java 17 JDK**
- **Maven 3.8+**
- **Docker 20.10+**
- **Docker Compose 2.0+**
- (Optional) **Postman** for API testing

### Running with Docker (Recommended)

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/swift-code-management.git
    cd swift-code-management
    ```

2. Build and start the containers:
    ```bash
    docker-compose -f docker/docker-compose.yml up -d --build
    ```

3. The application will be available at:
   - **API Base URL**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui.html

4. To stop the containers:
    ```bash
    docker-compose -f docker/docker-compose.yml down
    ```

### Running Locally

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/swift-code-management.git
    cd swift-code-management
    ```

2. Start PostgreSQL in Docker:
    ```bash
    docker run -d --name swift-postgres -p 5432:5432       -e POSTGRES_USER=postgres       -e POSTGRES_PASSWORD=postgres       -e POSTGRES_DB=swift_codes_db       postgres:15-alpine
    ```

3. Build and run the application:
    ```bash
    ./mvnw clean install
    ./mvnw spring-boot:run
    ```

4. The application will be available at:
   - **API Base URL**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui.html

## API Documentation

### Endpoints

1. **Get SWIFT Code Details**
   - URL: `GET /v1/swift-codes/{swift-code}`
   - Description: Retrieves details of a specific SWIFT code. If the code belongs to a headquarters, it also returns all its branches.

   **Example Request:**
   ```bash
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

   **Example Response (Branch):**
   ```json
   {
     "address": "123 CHAMPS ELYSEES, PARIS",
     "bankName": "BNP PARIBAS BRANCH",
     "countryISO2": "FR",
     "countryName": "FRANCE",
     "isHeadquarter": false,
     "swiftCode": "BNPAFRPP123"
   }
   ```

2. **Get SWIFT Codes by Country**
   - URL: `GET /v1/swift-codes/country/{countryISO2code}`
   - Description: Returns all SWIFT codes for a specific country (both headquarters and branches).

   **Example Request:**
   ```bash
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

3. **Add New SWIFT Code**
   - URL: `POST /v1/swift-codes`
   - Description: Adds a new SWIFT code to the database.

   **Example Request:**
   ```bash
   POST http://localhost:8080/v1/swift-codes
   Content-Type: application/json

   {
     "address": "1 Wall Street",
     "bankName": "Test Bank",
     "countryISO2": "US",
     "countryName": "United States",
     "isHeadquarter": true,
     "swiftCode": "TESTUSNYXXX"
   }
   ```

   **Example Response:**
   ```json
   {
     "message": "Swift code added successfully"
   }
   ```

4. **Delete SWIFT Code**
   - URL: `DELETE /v1/swift-codes/{swift-code}`
   - Description: Deletes a SWIFT code from the database.

   **Example Request:**
   ```bash
   DELETE http://localhost:8080/v1/swift-codes/TESTUSNYXXX
   ```

   **Example Response:**
   ```json
   {
     "message": "Swift code deleted successfully"
   }
   ```

### Postman Collection
You can test the API using our Postman collection:

Import the following JSON into Postman:
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
        "header": [],
        "url": {
          "raw": "http://localhost:8080/v1/swift-codes/BNPAFRPPXXX",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["v1","swift-codes","BNPAFRPPXXX"]
        }
      }
    },
    {
      "name": "Get SWIFT Codes by Country",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/v1/swift-codes/country/FR",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["v1","swift-codes","country","FR"]
        }
      }
    },
    {
      "name": "Add New SWIFT Code",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{
  "address": "1 Wall Street",
  "bankName": "Test Bank",
  "countryISO2": "US",
  "countryName": "United States",
  "isHeadquarter": true,
  "swiftCode": "TESTUSNYXXX"
}"
        },
        "url": {
          "raw": "http://localhost:8080/v1/swift-codes",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["v1","swift-codes"]
        }
      }
    },
    {
      "name": "Delete SWIFT Code",
      "request": {
        "method": "DELETE",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/v1/swift-codes/TESTUSNYXXX",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["v1","swift-codes","TESTUSNYXXX"]
        }
      }
    }
  ]
}
```

Set up environment variables in Postman if needed (e.g., for different hosts/ports)

## Testing
To run the test suite:
```bash
./mvnw test
```

The project includes:
- Unit tests for service layer
- Integration tests with TestContainers (using real PostgreSQL instance)
- Controller tests with MockMvc

## Database Schema
The main database table structure:
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

Relationships:
- Branches reference their headquarters via headquarter_code (first 8 characters of the headquarters' SWIFT code)

## Troubleshooting

### Common Issues
- **Port 8080 already in use**:
    ```bash
    # Find the process using port 8080
    netstat -ano | findstr :8080
    # Kill the process (replace PID)
    taskkill /PID 12345 /F
    ```

- **Database connection issues**:
    - Verify PostgreSQL is running
    - Check credentials in `application.yml`
    - For Docker, check logs: `docker logs swift-postgres`

- **Excel file not found**:
    - Ensure the Excel file is in `src/main/resources/data/`
    - Verify the filename matches what's in `application.yml`

- **Swagger UI not working**:
    - Try accessing `http://localhost:8080/swagger-ui/index.html`
    - Check for errors in application logs

## License
This project is licensed under the MIT License. See the LICENSE file for details.
