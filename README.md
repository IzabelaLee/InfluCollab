# InfluCollab

InfluCollab is a backend application designed to help influencers discover potential collaboration partners and manage collaboration requests.

The project is being developed incrementally using an Agile approach, with each iteration delivering a complete and tested piece of functionality.

---

## Current Features

### User Management

The application currently supports:

* Creating influencer profiles
* Retrieving all profiles
* Retrieving a profile by ID
* Updating profile information
* Deleting profiles

### Validation & Error Handling

Implemented API validation and exception handling for common scenarios:

* Invalid request data (`400 Bad Request`)
* Non-existing resources (`404 Not Found`)
* Duplicate email addresses (`409 Conflict`)

### API Documentation

Interactive API documentation is available through Swagger UI.

After starting the application:

`http://localhost:8080/swagger-ui/index.html`

### API Testing

A Postman collection is included and contains both positive and negative test scenarios for all implemented endpoints.

Location:

`/postman/User.postman_collection.json`

---

## Technology Stack

* Java 21
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Maven
* Swagger / OpenAPI
* Postman

---

## Project Structure

The application follows a layered architecture:

* Controller layer
* Service layer
* Repository layer
* Persistence layer (PostgreSQL)

Additional components:

* Global exception handling
* Request validation
* OpenAPI documentation

---

## Running the Application

1. Start PostgreSQL
2. Configure database connection in `application.properties`
3. Run the Spring Boot application
4. Open Swagger UI to explore available endpoints
