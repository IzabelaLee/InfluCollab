# InfluCollab

InfluCollab is a backend application designed to help content creators discover collaboration opportunities based on their travel plans.

Creators can publish upcoming trips and availability, allowing other creators to find potential collaborations in specific locations and time periods.

---
## Features

### 1. User Management

Creators can create and manage their influencer profiles.

Implemented endpoints include:

- Create influencer profiles
- Retrieve all profiles
- Retrieve a profile by ID
- Update profile information
- Delete profiles

### 2. Collaboration Opportunities
Creators can publish and manage their own collaboration opportunities, while all users can browse opportunities created by others.

Implemented endpoints include:
- Create a collaboration opportunity
- Retrieve all opportunities
- Retrieve a single opportunity
- Retrieve all opportunities created by a specific user
- Retrieve a specific opportunity belonging to a user
- Update an opportunity (PUT)
- Partially update an opportunity (PATCH)
- Delete an opportunity

Business rules implemented:

Users can only update or delete their own opportunities
Opportunity travel dates must form a valid date range
Opportunity ownership is verified before protected operations

### 3. Collaboration Requests

Creators can send collaboration requests for available opportunities.

A collaboration request represents a connection between a creator interested in collaboration and an existing collaboration opportunity.

Implemented endpoints include:

- Create a collaboration request for an opportunity
- Retrieve requests sent by a user
- Retrieve requests received by an opportunity owner
- Accept a collaboration request
- Reject a collaboration request

Request lifecycle is managed using request statuses: PENDING, ACCEPTED, REJECTED.

Business rules implemented:

- A user cannot send a request to their own opportunity
- New requests are always created with PENDING status
- Only the opportunity owner can accept or reject requests
- Only pending requests can change their status

### Opportunity Search, Filtering, Sorting and Pagination

The opportunity board supports advanced searching functionality.

Implemented features:

- Filter opportunities by city
- Filter opportunities by start date
- Filter opportunities by end date
- Filter opportunities by owner
- Combine multiple filters using dynamic queries
- Sort results by selected fields
- Paginate large result sets

### Example requests

| Description | Endpoint |
|---|---|
| Filter by city | `GET /opportunities?city=Barcelona` |
| Filter by city and start date | `GET /opportunities?city=Barcelona&from=2026-08-10` |
| Sort by start date ascending | `GET /opportunities?sort=startDate,asc` |
| Get paginated results | `GET /opportunities?page=0&size=10` |


The filtering system is implemented using Spring Data JPA Specifications, allowing dynamic query construction without complex conditional logic.

Pagination is implemented using Spring Data's `Pageable` mechanism.

## Validation & Error Handling

Implemented validation and exception handling for common API scenarios:

- Invalid request data (`400 Bad Request`)
- Non-existing users (`404 Not Found`)
- Non-existing collaboration opportunities (`404 Not Found`)
- Duplicate email addresses (`409 Conflict`)
- Business validation for travel date ranges

## API Documentation

Interactive API documentation is available through Swagger UI:

`http://localhost:8080/swagger-ui/index.html`

![img.png](images/swagger.png)

## API Testing

A Postman collection is included with positive and negative test scenarios.

Location:`/postman-collections/`

![img_1.png](images/postman.png)

---

## Automated Testing & CI

The project includes automated tests covering the main application layers and security functionality.

- Unit and controller tests using JUnit 5, Mockito, and Spring Boot Test
- Tests covering controllers, services, authentication, authorization, and JWT functionality
- Positive and negative test scenarios for API behaviour and validation
- GitHub Actions automatically runs the Maven test suite on pull requests
- Pull requests must pass the test suite before being merged

The CI workflow uses Java 21 and Maven to automatically verify that changes do not introduce regressions.

## Running tests

Unit and integration tests are provided under `src/test/java`.

Run locally with Maven:

```bash
./mvnw test
```

Tests use JUnit 5 and Mockito (included via `spring-boot-starter-test`).

---

## Security & Authentication

The application implements JWT-based stateless authentication with role-based authorization.

### Authentication

- User registration with email and password
- Passwords are securely hashed with BCrypt before storage
- Login endpoint (`POST /auth/login`) validates credentials and returns JWT token
- Client includes token in `Authorization: Bearer <token>` header for subsequent requests
- Token is validated on every request via JWT filter

### Authorization

**Protected Endpoints:**
- POST/PUT/DELETE operations on users and opportunities require authentication
- GET endpoints remain public (anyone can browse users and opportunities)

**Ownership-Based Access:**
- Users can only modify their own profile and opportunities
- Attempting to modify another user's resource is rejected with 403 Forbidden

**Role-Based Access:**
- Default role (`ROLE_USER`): regular user can only delete their own account
- Admin role (`ROLE_ADMIN`): can delete any user account

### Error Responses

| Status                      | Scenario |
|-----------------------------|----------|
| 401 Unauthorized            | Missing or invalid token |
| 403 Forbidden               | Valid token but insufficient permissions (e.g., modifying another user's data) |

---

## Backend Concepts Demonstrated

The project currently demonstrates:

- REST API design
- Layered architecture
- Spring Data JPA
- Entity relationships
- DTO pattern
- Entity-to-DTO mapping
- Global exception handling
- Custom exceptions
- Repository query methods
- CRUD operations
- Swagger / OpenAPI documentation
- Spring Security & Authentication
- JWT token generation & validation
- BCrypt password hashing
- Role-based authorization
- Method-level security with @PreAuthorize
- Ownership-based access control
- Unit testing
- Integration / controller testing
- Continuous Integration (CI)

---

## Technology Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- Swagger / OpenAPI
- Postman
- JUnit 5
- Mockito
- GitHub Actions

---

## Project Structure

The application follows a layered architecture:

- **Controller** — handles HTTP requests and API responses
- **Service** — contains business logic
- **Repository** — handles data access through Spring Data JPA
- **Entity** — represents persisted domain objects

## Local Setup
### Prerequisites

Java 21
PostgreSQL
Maven

#### 1. Clone the repository
```bash
git clone https://github.com/IzabelaLee/InfluCollab.git
cd InfluCollab
```

#### 2. Configure the database
Create a PostgreSQL database and configure the connection in application.properties (or environment variables).

#### 3. Configure JWT
Set the JWT secret and expiration using environment variables or application properties.

#### 4. Run the application
```bash
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080`.

