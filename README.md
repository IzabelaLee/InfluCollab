# InfluCollab

## Overview

InfluCollab is a platform that helps influencers discover and organize collaboration opportunities with other creators.

The application allows influencers to create profiles, showcase upcoming events and activities, browse other influencers, and send collaboration requests.

The main goal is to simplify networking and collaboration between content creators.

---

## Product Vision

Influencers often discover collaboration opportunities through social media, personal networks, or chance encounters.

InfluCollab provides a dedicated platform where creators can:

* Create professional profiles
* Share upcoming events and activities
* Discover other influencers
* Send and manage collaboration requests

---

## MVP Scope

The first version focuses exclusively on influencers.

Manager accounts, authentication, notifications, chat, and other advanced features are intentionally excluded from the MVP.

### Core Features

#### User Profiles

Influencers can:

* Create a profile
* View profiles
* Update profile information
* Delete profiles

---

#### Influencer Discovery

Influencers can:

* Browse other influencers
* View profile details
* View upcoming events published by other influencers

---

#### Events

Influencers can:

* Create events
* Update events
* Delete events
* View events

---

#### Collaboration Requests

Influencers can:

* Send collaboration requests to other influencers
* View received requests
* Accept requests
* Reject requests

---

## Domain Model

### User

Represents an influencer using the platform.

Fields:

* id
* name
* email
* city
* bio

---

### Event

Represents an upcoming activity, appearance, campaign, or content opportunity.

Fields:

* id
* title
* description
* location
* startDate
* endDate
* owner

Relationship:

User 1 → N Events

---

### CollaborationRequest

Represents a collaboration proposal between two influencers.

Fields:

* id
* sender
* receiver
* message
* status
* createdAt

Relationships:

User 1 → N Sent Requests

User 1 → N Received Requests

---

## Project Structure

```text
com.influcollab

├── controller
├── service
├── repository
├── entity
├── enums
└── InfluCollabApplication
```

Layer responsibilities:

* Controller → handles HTTP requests
* Service → contains business logic
* Repository → communicates with the database
* Entity → represents database tables

---

## Development Roadmap

### Sprint 1

* Spring Boot setup
* Database configuration
* User entity
* User CRUD API

### Sprint 2

* Event entity
* Event CRUD API
* User–Event relationship

### Sprint 3

* CollaborationRequest entity
* Collaboration request workflow

### Sprint 4

* Validation
* Exception handling
* Custom queries

---

## Future Enhancements

* Authentication and authorization
* Manager accounts
* Notifications
* Chat system
* Social media integrations

