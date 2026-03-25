## Overview

**Problem it solves:**
Cinema REST API provides a comprehensive backend solution for managing cinema operations including movies, rooms, showtimes, seats, and ticket bookings. It solves the problem of efficiently managing cinema resources and providing a seamless booking experience.

**Target users:**
- Cinema administrators and staff
- Third-party cinema booking applications
- Frontend developers building cinema interfaces

**Core functionality:**
- Movie catalog management with genre filtering and search
- Cinema room and seat configuration management
- Showtime scheduling and availability tracking
- Ticket booking and reservation system with seat selection

## Tech Stack

**Backend:**
- Framework: Spring
- Language/Version: Java 21
- Build tool: Maven
- Database: PostgreSQL for production and H2 for development and testing
- Testing: JUnit, AssertJ, Mockito

## Requirements

### Functional Requirements

**Movie Management**
1. Create a movie with title, duration, genre and description.
2. List all the active movies.
3. Update the information of a movie.
4. Delete a movie, only if it does not have shows scheduled.
5. Search movies by title or genre.

**Room Management**
1. Create a room with a specific number of rows and seats per each one of them.
2. List all the cinema's room.
3. See the configuration of seats in a specific room.
4. Update room information.
5. Delete a room, only if it does not have active show.



### Non-Functional Requirements
- **Performance:** Fast response times for seat availability queries and booking operations
- **Security:** Input validation, SQL injection prevention, data integrity constraints
- **Scalability:** Designed to handle multiple concurrent bookings and seat reservations

## Data Model

### Entities

**Movie:**
```
- id: Long
- title: String
- description: String
- duration: Integer (minutes)
- genre: String
- releaseDate: LocalDate
- relationships: One-to-Many with Showtime
```

**Room:**
```
- id: Long
- name: String
- rows: Integer
- seatsPerRow: Integer
- relationships: One-to-Many with Seat, One-to-Many with Showtime
```

**Seat:**
```
- id: Long
- rowNumber: Integer
- seatNumber: Integer
- status: SeatStatus (AVAILABLE, SOLD)
- relationships: Many-to-One with Room, One-to-Many with Ticket
```

**Showtime:**
```
- id: Long
- startTime: LocalDateTime
- endTime: LocalDateTime
- status: ShowtimeStatus (SCHEDULED, ONGOING, COMPLETED, CANCELLED)
- relationships: Many-to-One with Movie, Many-to-One with Room, One-to-Many with Ticket
```

**Ticket:**
```
- id: Long
- price: BigDecimal
- purchaseDate: LocalDateTime
- status: TicketStatus (ACTIVE, CONSUMED, CANCELLED)
- relationships: Many-to-One with Showtime, Many-to-One with Seat
```

### Database Decisions
- **Normalization level:** 3NF - entities are properly normalized to reduce redundancy
- **Index strategy:**
  - Index on `Movie.title` and `Movie.genre` for efficient search operations
  - Index on `Showtime.startTime` for efficient showtime queries
  - Composite index on `Seat.room_id`, `Seat.rowNumber`, `Seat.seatNumber` for seat lookups
- **Why this schema:** Supports efficient queries for availability checks, prevents overbooking through constraints, and maintains data integrity

### Diagram
<!-- TODO: Add ER Diagram -->
```
[Placeholder for Entity Relationship Diagram]

Expected diagram showing:
- Movie ←→ Showtime (1:N)
- Room ←→ Showtime (1:N)
- Room ←→ Seat (1:N)
- Showtime ←→ Ticket (1:N)
- Seat ←→ Ticket (1:N)
```

**Diagram Link:** [Add Excalidraw/Draw.io link here]

## API Design

### Endpoints

**Movies:**
```
POST   /api/movies              - Create a new movie
GET    /api/movies              - List all active movies (with optional filters: title, genre)
GET    /api/movies/{id}         - Get movie details
PUT    /api/movies/{id}         - Update movie information
DELETE /api/movies/{id}         - Delete movie (only if no scheduled shows)
GET    /api/movies/search       - Search movies by title or genre
```

**Rooms:**
```
POST   /api/rooms               - Create a room with seat configuration
GET    /api/rooms               - List all rooms
GET    /api/rooms/{id}          - Get room details with seat layout
PUT    /api/rooms/{id}          - Update room information
DELETE /api/rooms/{id}          - Delete room (only if no active shows)
GET    /api/rooms/{id}/seats    - Get seat configuration for a room
```

**Showtimes:**
```
POST   /api/showtimes           - Schedule a new showtime
GET    /api/showtimes           - List showtimes (with filters: movie, date, room)
GET    /api/showtimes/{id}      - Get showtime details with availability
PUT    /api/showtimes/{id}      - Update showtime
DELETE /api/showtimes/{id}      - Cancel showtime
GET    /api/showtimes/{id}/availability - Get seat availability
```

**Tickets:**
```
POST   /api/tickets             - Book tickets (reserve seats)
GET    /api/tickets/{id}        - Get ticket details
PUT    /api/tickets/{id}/confirm - Confirm ticket purchase
PUT    /api/tickets/{id}/cancel  - Cancel ticket
GET    /api/tickets             - List tickets (with filters)
```

### Design Decisions
- **RESTful conventions:** Standard HTTP methods (GET, POST, PUT, DELETE) with proper status codes
- **Versioning strategy:** No explicit version prefix currently; endpoints are exposed directly under resource paths (e.g. `/movies`, `/tickets`)
- **Request/Response format:** JSON request/response DTOs by resource
- **Error handling approach:** Global exception handling with HTTP Problem Details

## Architecture

### Layer Structure
```
Controller Layer
    ↓
Service Layer
    ↓
Repository Layer
    ↓
Database
```

### Design Patterns Used

**Repository Pattern**
- Where: Data access layer using Spring Data JPA repositories
- Why: Abstracts database operations and provides clean separation of concerns
- Alternative considered: Direct JDBC access (too verbose and error-prone)

**DTO (Data Transfer Object) Pattern**
- Where: API request/response handling
- Why: Decouples internal entity structure from API contract, enables validation
- Trade-offs: Additional mapping code, but better encapsulation and API stability

**Service Layer Pattern**
- Where: Business logic between controllers and repositories
- Why: Centralizes business rules, transaction management, and orchestration
- Alternative considered: Fat controllers (violates single responsibility principle)

**Builder Pattern**
- Where: Entity and DTO construction (via Lombok @Builder)
- Why: Improves readability and makes object creation more flexible
- Trade-offs: None with Lombok, as it's generated at compile time

## Security Considerations

### Authentication
- Strategy: Currently not implemented (planned for future iteration)
- Why this approach: Focus on core functionality first, authentication layer to be added

### Authorization
- Role-based/Permission-based: Planned for future (Admin, Staff, Customer roles)
- Implementation: Will use Spring Security with JWT tokens

### Data validation
- Where: Controller layer using Bean Validation annotations
- How: `@Valid` annotations on DTOs, custom validators for business rules

### Other security measures:
- SQL injection prevention through JPA/Hibernate parameterized queries
- Input sanitization through validation constraints
- Database constraints to prevent data integrity violations
- Proper error messages that don't leak sensitive information

## Testing Strategy

### What needs testing

**Unit tests:**
- Service layer logic
- Edge cases:
- Mock dependencies:

**Integration tests:**
- Repository layer with actual DB
- API endpoints
- What scenarios:

**Test cases identified from design:**
1. 
2. 
3. 

## Challenges & Solutions

### Challenge 1:
**Problem:**
**Solution:**
**Why this solution:**
**Related concept:** [[]]

## What I Learned

### Technical learnings:
- 

### Design learnings:
- 

### Mistakes made:
- 

## Future Improvements

### Known limitations:
- 

### Potential enhancements:
- 

### Refactoring ideas:
- 

## References
- Documentation:
- Tutorials used:
- Similar projects:
