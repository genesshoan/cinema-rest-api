# Cinema REST API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-Latest-red.svg)](https://maven.apache.org/)

A comprehensive RESTful API for managing cinema operations including movies, rooms, showtimes, and ticket bookings. Built with Spring Boot and designed for scalability and maintainability.

## 📋 Table of Contents

- [Features](#-features)
- [Architecture Overview](#-architecture-overview)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Project Structure](#-project-structure)
- [Testing](#-testing)
- [Configuration](#-configuration)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### Movie Management
- ✅ Create, read, update, and delete movies
- ✅ Search movies by title or genre
- ✅ Track movie details (title, duration, genre, description, release date)
- ✅ Prevent deletion of movies with scheduled shows

### Room Management
- ✅ Create cinema rooms with customizable seat layouts
- ✅ Configure rows and seats per row
- ✅ View seat configurations and availability
- ✅ Update room information
- ✅ Prevent deletion of rooms with active shows

### Showtime Management
- ✅ Schedule movie showtimes in specific rooms
- ✅ Track showtime status (Scheduled, Ongoing, Completed, Cancelled)
- ✅ View seat availability for showtimes
- ✅ Automatic time conflict detection

### Ticket & Booking System
- ✅ Reserve and book tickets for specific seats
- ✅ Track ticket status (Active, Consumed, Cancelled)
- ✅ Prevent double-booking with seat locking
- ✅ View booking history and details

## 🏗️ Architecture Overview

<!-- TODO: Add Architecture Diagram -->
```
┌─────────────────────────────────────────────┐
│           Client Applications                │
│  (Web, Mobile, Third-party Integrations)    │
└─────────────────┬───────────────────────────┘
                  │ HTTP/REST
┌─────────────────▼───────────────────────────┐
│         Controller Layer                     │
│  (Request handling, Validation, DTOs)       │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│         Service Layer                        │
│  (Business logic, Transactions)             │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│         Repository Layer                     │
│  (Data access, JPA repositories)            │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│         PostgreSQL Database                  │
│  (H2 for development/testing)               │
└─────────────────────────────────────────────┘
```

**[Placeholder for detailed architecture diagram]**

### Design Patterns
- **Repository Pattern**: Clean data access abstraction
- **DTO Pattern**: API contract separation from domain model
- **Service Layer Pattern**: Business logic encapsulation
- **Builder Pattern**: Fluent object construction

## 🛠️ Tech Stack

**Backend Framework:**
- Java 21
- Spring Boot 4.0.1
- Spring Data JPA
- Spring Web
- Bean Validation (JSR-380)

**Database:**
- PostgreSQL (Production)
- H2 (Development & Testing)

**Build Tool:**
- Apache Maven

**Additional Libraries:**
- Lombok (Boilerplate reduction)
- JUnit 5 (Testing)
- AssertJ (Assertions)
- Mockito (Mocking)

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
- **Apache Maven 3.6+**
- **Docker & Docker Compose** (for PostgreSQL)
- **Git** (for version control)
- **Postman or cURL** (for API testing)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/cinema-rest-api.git
cd cinema-rest-api
```

### 2. Set Up Environment Variables

Create a `.env` file in the project root:

```env
DB_NAME=cinema_db
DB_USER=cinema_user
DB_PASSWORD=your_secure_password
DB_PORT=5432
```

### 3. Start the Database

```bash
# Start PostgreSQL using Docker Compose
docker-compose up -d

# Verify the database is running
docker ps
```

### 4. Build the Project

```bash
# Clean and build with Maven
./mvnw clean install

# Skip tests if needed
./mvnw clean install -DskipTests
```

### 5. Run the Application

```bash
# Run with Maven
./mvnw spring-boot:run

# Or run the JAR directly
java -jar target/cinema-rest-api-0.0.1-SNAPSHOT.jar
```

The API will be available at: `http://localhost:8080`

### 6. Verify Installation

```bash
# Health check (if implemented)
curl http://localhost:8080/actuator/health

# Or test a basic endpoint
curl http://localhost:8080/api/movies
```

## 📚 API Documentation

### Base URL
```
http://localhost:8080
```

### Endpoints Overview

#### Movies
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/movies` | Create a new movie |
| GET | `/movies` | List all movies (with filters) |
| GET | `/movies/{id}` | Get movie by ID |
| PUT | `/movies/{id}` | Update movie |
| DELETE | `/movies/{id}` | Delete movie |
| GET | `/movies/search` | Search by title/genre |

#### Rooms
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/rooms` | Create a room |
| GET | `/rooms` | List all rooms |
| GET | `/rooms/{id}` | Get room details |
| PUT | `/rooms/{id}` | Update room |
| DELETE | `/rooms/{id}` | Delete room |
| GET | `/rooms/{id}/seats` | Get seat layout |

#### Showtimes
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/showtimes` | Schedule showtime |
| GET | `/showtimes` | List showtimes |
| GET | `/showtimes/{id}` | Get showtime details |
| PUT | `/showtimes/{id}` | Update showtime |
| DELETE | `/showtimes/{id}` | Cancel showtime |
| GET | `/showtimes/{id}/availability` | Check seat availability |

#### Tickets
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/tickets` | Book tickets |
| GET | `/tickets/{id}` | Get ticket details |
| PUT | `/tickets/{id}/confirm` | Confirm booking |
| PUT | `/tickets/{id}/cancel` | Cancel booking |
| GET | `/tickets` | List tickets |

### Example Requests

#### Create a Movie
```bash
curl -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Matrix",
    "duration": 136,
    "genre": "Sci-Fi",
    "description": "A computer hacker learns about the true nature of reality.",
    "releaseDate": "1999-03-31"
  }'
```

#### Create a Room
```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Theater 1",
    "rows": 10,
    "seatsPerRow": 12
  }'
```

#### Book Tickets
```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "showtimeId": 1,
    "seatIds": [1, 2, 3],
    "customerName": "John Doe"
  }'
```

**[Placeholder for Postman Collection link]**

## 🗄️ Database Schema

### Entity Relationship Diagram

<!-- TODO: Add ERD Diagram -->
```
[Placeholder for ER Diagram - Add using Excalidraw, Draw.io, or dbdiagram.io]

Relationships:
┌─────────┐         ┌──────────┐         ┌──────┐
│  Movie  │────1:N──│ Showtime │──N:1────│ Room │
└─────────┘         └────┬─────┘         └──┬───┘
                         │                  │
                         │1:N            1:N│
                         ▼                  ▼
                    ┌────────┐         ┌──────┐
                    │ Ticket │──N:1────│ Seat │
                    └────────┘         └──────┘
```

### Core Entities

**Movie**: Stores movie information (title, duration, genre, etc.)
**Room**: Cinema rooms with seat configuration
**Seat**: Individual seats within rooms
**Showtime**: Scheduled movie screenings
**Ticket**: Booking records linking showtimes and seats

See [docs/project.md](docs/project.md) for detailed schema documentation.

## 📁 Project Structure

```
cinema-rest-api/
├── src/
│   ├── main/
│   │   ├── java/dev/genesshoan/cinema_rest_api/
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── service/          # Business logic
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── dto/              # Data transfer objects
│   │   │   ├── mapper/           # DTO-Entity mappers
│   │   │   ├── exception/        # Custom exceptions
│   │   │   ├── error/            # Error handling
│   │   │   └── CinemaRestApiApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-test.properties
│   └── test/
│       └── java/                  # Unit and integration tests
├── docs/
│   └── project.md                 # Detailed project documentation
├── http-test/                     # HTTP request examples
├── docker-compose.yml             # PostgreSQL setup
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

## 🧪 Testing

### Run All Tests

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest=MovieServiceTest
```

### Test Structure

- **Unit Tests**: Service and mapper logic testing
- **Integration Tests**: Repository layer with H2 database
- **API Tests**: Controller endpoints with MockMvc

### Test Coverage Goals
- Service Layer: 80%+
- Repository Layer: 70%+
- Controller Layer: 75%+

## ⚙️ Configuration

### Application Profiles

**Development** (`application.properties`):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cinema_db
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**Testing** (`application-test.properties`):
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

**Production**:
```properties
spring.datasource.url=${DATABASE_URL}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_NAME` | Database name | cinema_db |
| `DB_USER` | Database user | cinema_user |
| `DB_PASSWORD` | Database password | - |
| `DB_PORT` | Database port | 5432 |
| `SERVER_PORT` | Application port | 8080 |

## 🚢 Deployment

### Docker Deployment

```bash
# Build Docker image
docker build -t cinema-rest-api:latest .

# Run container
docker run -p 8080:8080 \
  -e DB_NAME=cinema_db \
  -e DB_USER=cinema_user \
  -e DB_PASSWORD=secure_password \
  cinema-rest-api:latest
```


## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Java naming conventions
- Use Lombok annotations appropriately
- Write meaningful commit messages
- Add tests for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Genesshoan** - *Initial work* - [GitHub Profile](https://github.com/genesshoan)

---

**Project Status**: 🚧 In Development

**Current Version**: 0.0.1-SNAPSHOT

**Last Updated**: March 2026
