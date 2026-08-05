# InfluCollab

InfluCollab is a backend application designed to help content creators discover collaboration opportunities based on their travel plans.

Creators can publish upcoming trips and availability, allowing other creators to find potential collaborations in specific locations and time periods.

The project is being developed incrementally using an Agile approach, with each iteration delivering a complete and tested piece of functionality.

---

## Current Features

### User Management

- Create influencer profiles
- Retrieve all profiles
- Retrieve a profile by ID
- Update profile information
- Delete profiles

### Collaboration Opportunities

Users can manage their own collaboration opportunities.

Implemented endpoints include:

- Create a collaboration opportunity
- Retrieve all opportunities
- Retrieve a single opportunity
- Retrieve all opportunities created by a specific user
- Retrieve a specific opportunity belonging to a user
- Update an opportunity (PUT)
- Partially update an opportunity (PATCH)
- Delete an opportunity

Each opportunity contains:

- title
- city
- travel dates
- description
- owner
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

### Collaboration Requests

Creators can send collaboration requests for available opportunities.

A collaboration request represents a connection between a creator interested in collaboration and an existing collaboration opportunity.

Implemented endpoints include:

- Create a collaboration request for an opportunity
- Retrieve requests sent by a user
- Retrieve requests received by an opportunity owner
- Accept a collaboration request
- Reject a collaboration request

Each collaboration request contains:

- sender
- collaboration opportunity
- message
- status
- creation timestamp

Request lifecycle is managed using request statuses: PENDING, ACCEPTED, REJECTED.

Business rules implemented:

- A user cannot send a request to their own opportunity
- New requests are always created with `PENDING` status
- Only the opportunity owner can accept or reject requests
- Only pending requests can change their status


### Validation & Error Handling

Implemented validation and exception handling for common API scenarios:

- Invalid request data (`400 Bad Request`)
- Non-existing users (`404 Not Found`)
- Non-existing collaboration opportunities (`404 Not Found`)
- Duplicate email addresses (`409 Conflict`)
- Business validation for travel date ranges

### API Documentation

Interactive API documentation is available through Swagger UI:

`http://localhost:8080/swagger-ui/index.html`

![img.png](images/swagger.png)

### API Testing

A Postman collection is included with positive and negative test scenarios.

Location:

`/postman/User.postman_collection.json`

![img_1.png](images/postman.png)

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
- Attempting to modify another user's resource returns 403 Forbidden

**Role-Based Access:**
- Default role (`ROLE_USER`): regular user can only delete their own account
- Admin role (`ROLE_ADMIN`): can delete any user account

### Error Responses

| Status | Scenario |
|--------|----------|
| 401 Unauthorized | Missing or invalid token |
| 403 Forbidden | Valid token but insufficient permissions (e.g., modifying another user's data) |

---

## Backend Concepts Demonstrated

The project currently demonstrates:

- REST API design
- Layered architecture
- Spring Data JPA
- Entity relationships
- DTO pattern
- Entity-to-DTO mapping
- Bean Validation
- Global exception handling
- Custom exceptions
- Repository query methods
- CRUD operations
- Swagger / OpenAPI documentation
- **Spring Security & Authentication**
- **JWT token generation & validation**
- **BCrypt password hashing**
- **Role-based authorization**
- **Method-level security with @PreAuthorize**
- **Ownership-based access control**

---

## Technology Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- Swagger / OpenAPI
- Postman

---

## Project Structure

The application follows a layered architecture:

- Controller
- Service
- Repository
- Persistence (PostgreSQL)

Additional components:

- DTO layer
- Global exception handling
- Request validation
- OpenAPI documentation

