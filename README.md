
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

### Running Locally
#### Clone the repository:
```sh
git clone https://github.com/huseynliss/swiftcodes-api.git
cd swiftcodes-api
```
#### Start PostgreSQL in Docker:
```sh
docker-compose up -d --build
```
#### Build and run the application:
```sh
./mvnw clean install
./mvnw spring-boot:run
```
The application will be available at:
- **API Base URL**: http://localhost:8080

## API Documentation

### Endpoints

#### 1. **Get SWIFT Code Details**
- **URL:** `/v1/swift-codes/{swiftCode}`
- **Method:** `GET`
- **Description:** Retrieves details of a specific SWIFT code by its identifier.
  
##### Parameters:
- **Path Parameter:** 
  - `swiftCode` (string): The SWIFT code you want to retrieve.

##### Responses:
- **200 OK:** Successfully retrieved the SWIFT code details.
  - Example response:
  ```json
  {
    "swiftCode": "BNPAFRPPXXX",
    "bankName": "BNP PARIBAS",
    "address": "16 BOULEVARD DES ITALIENS, PARIS",
    "countryISO2": "FR",
    "countryName": "FRANCE",
    "isHeadquarter": true,
    "branches": [
      {
        "swiftCode": "BNPAFRPP123",
        "bankName": "BNP PARIBAS BRANCH",
        "address": "123 CHAMPS ELYSEES, PARIS",
        "countryISO2": "FR",
        "isHeadquarter": false
      }
    ]
  }
  ```
- **404 Not Found:** SWIFT code not found.
  - Example response:
  ```json
  {
    "message": "SWIFT code not found"
  }
  ```

#### 2. **Get SWIFT Codes by Country**
- **URL:** `/v1/swift-codes/country/{countryISO2}`
- **Method:** `GET`
- **Description:** Retrieves all SWIFT codes associated with a specific country, based on the ISO 3166-1 alpha-2 code.

##### Parameters:
- **Path Parameter:** 
  - `countryISO2` (string): The 2-letter ISO code of the country (e.g., `US` for the United States, `FR` for France).

##### Responses:
- **200 OK:** Successfully retrieved SWIFT codes for the specified country.
  - Example response:
  ```json
  {
    "countryISO2": "FR",
    "countryName": "FRANCE",
    "swiftCodes": [
      {
        "swiftCode": "BNPAFRPPXXX",
        "bankName": "BNP PARIBAS",
        "address": "16 BOULEVARD DES ITALIENS, PARIS",
        "isHeadquarter": true
      },
      {
        "swiftCode": "BNPAFRPP123",
        "bankName": "BNP PARIBAS BRANCH",
        "address": "123 CHAMPS ELYSEES, PARIS",
        "isHeadquarter": false
      }
    ]
  }
  ```
- **404 Not Found:** No SWIFT codes found for the specified country.
  - Example response:
  ```json
  {
    "message": "No SWIFT codes found for the country"
  }
  ```

#### 3. **Add a New SWIFT Code**
- **URL:** `/v1/swift-codes`
- **Method:** `POST`
- **Description:** Adds a new SWIFT code to the system.

##### Request Body:
- **Content-Type:** `application/json`
- **Body Parameters:** 
  - `swiftCode` (string): The SWIFT code to be added.
  - `bankName` (string): The name of the bank.
  - `address` (string): The address of the bank.
  - `countryISO2` (string): The 2-letter ISO code of the country.
  - `countryName` (string): The full name of the country.
  - `isHeadquarter` (boolean): Whether the SWIFT code represents a headquarter.

##### Responses:
- **201 Created:** Successfully created the SWIFT code.
  - Example response:
  ```json
  {
    "message": "SWIFT code created successfully"
  }
  ```
- **400 Bad Request:** Invalid data in the request.
  - Example response:
  ```json
  {
    "message": "Invalid data in request"
  }
  ```

#### 4. **Delete a SWIFT Code**
- **URL:** `/v1/swift-codes/{swiftCode}`
- **Method:** `DELETE`
- **Description:** Deletes a SWIFT code from the system.

##### Parameters:
- **Path Parameter:** 
  - `swiftCode` (string): The SWIFT code to delete.

##### Responses:
- **200 OK:** Successfully deleted the SWIFT code.
  - Example response:
  ```json
  {
    "message": "SWIFT code deleted successfully"
  }
  ```
- **404 Not Found:** SWIFT code not found.
  - Example response:
  ```json
  {
    "message": "SWIFT code not found"
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
