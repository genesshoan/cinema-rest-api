## Overview

**Problem it solves:**

**Target users:**

**Core functionality:**

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
- Performance:
- Security:
- Scalability:

## Data Model

### Entities

**Entity Name:**
```
- field1: type
- field2: type
- relationships:
```

### Database Decisions
- **Normalization level:** 
- **Index strategy:**
  - Index on X because:
  - Composite index on Y, Z because:
- **Why this schema:**

### Diagram
Link to Excalidraw or embed here.

## API Design

### Endpoints

```
POST   /api/resource        - Description
GET    /api/resource        - Description
GET    /api/resource/{id}   - Description
PUT    /api/resource/{id}   - Description
DELETE /api/resource/{id}   - Description
```

### Design Decisions
- **RESTful conventions:**
- **Versioning strategy:**
- **Request/Response format:**
- **Error handling approach:**

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

**[[Pattern Name]]**
- Where: 
- Why:
- Alternative considered:

**[[Pattern Name]]**
- Where:
- Why:
- Trade-offs:

## Security Considerations

### Authentication
- Strategy:
- Why this approach:

### Authorization
- Role-based/Permission-based:
- Implementation:

### Data validation
- Where:
- How:

### Other security measures:

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
