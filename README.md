# API Note Vault

A RESTful API for managing note storage built with Spring Boot 3.3.4 and SQLite. Designed as a foundational data vault system for ingesting, storing, and retrieving structured note data.

## System Overview & Architecture

The API follows a classic three-tier Spring Boot architecture:

```
Controller Layer (REST Endpoints)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
SQLite Database
```

### Core Components

- **NoteController** - REST endpoints for CRUD operations on notes, returns `ResponseEntity<NoteResponse>` for type-safe responses
- **NoteService** - Business logic: content validation, note creation, retrieval, deletion (works with Note entities)
- **NoteRepository** - Spring Data JPA interface extending JpaRepository
- **Note Entity** - JPA entity mapped to `notes` table (database representation)
- **NoteResponse** - DTO for API responses, separates database entity from API contract
- **NoteRequest** - DTO for incoming POST requests
- **Database** - SQLite with Hibernate ORM (file-based persistence)

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.3.4 |
| Build | Maven | 3.8+ |
| Database | SQLite | Latest |
| ORM | Hibernate | 6.5.3 |
| Testing | JUnit 5 (Jupiter) + Mockito | Latest |
| Java | OpenJDK | 17+ |

## Architecture: Data Flow

```
Client Request (JSON)
    ↓
NoteController (REST Endpoint)
    ├─ Validates input (NoteRequest DTO)
    ├─ Calls NoteService
    └─ Converts Note entity to NoteResponse DTO
    ↓
NoteService (Business Logic)
    ├─ Validates business rules (empty content, not found)
    ├─ Calls NoteRepository
    └─ Throws IllegalArgumentException on validation failure
    ↓
NoteRepository (Data Access)
    └─ Calls Hibernate/SQLite
    ↓
SQLite Database
    ↓
Response (NoteResponse DTO in JSON format)
```

**Key Separation:**
- **Service layer** works with `Note` entities (database representation)
- **Controller layer** converts `Note` → `NoteResponse` DTOs for API responses
- **Client** only sees `NoteResponse` fields — never the raw database entity

## How to Build & Run

### Prerequisites
- Java 17 or later
- Maven 3.8+

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`

### Run Tests
```bash
mvn clean test
```

Runs 17 tests:
- 1 Application Context Test
- 7 Controller Integration Tests
- 6 Service Unit Tests
- 3 Edge Case Tests

## API Endpoints

All endpoints return JSON. Base path: `/notes`

### 1. Create Note
**POST** `/notes`

Request:
```bash
curl -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -d '{"content":"Buy groceries"}'
```

Response (201 Created):
```json
{
  "id": 1,
  "content": "Buy groceries",
  "createdAt": "2026-06-04T17:42:58.123456"
}
```

**Status Codes:**
- `201 Created` - Note successfully created
- `400 Bad Request` - Content missing, empty, or null

### 2. Get All Notes
**GET** `/notes`

Response (200 OK):
```json
[
  {
    "id": 1,
    "content": "Buy groceries",
    "createdAt": "2026-06-04T17:42:58.123456"
  }
]
```

**Status Codes:**
- `200 OK` - Returns array of all notes (empty array if none exist)

### 3. Get Note by ID
**GET** `/notes/{id}`

Response (200 OK):
```json
{
  "id": 1,
  "content": "Buy groceries",
  "createdAt": "2026-06-04T17:42:58.123456"
}
```

**Status Codes:**
- `200 OK` - Note found
- `404 Not Found` - Note with given ID does not exist

### 4. Delete Note
**DELETE** `/notes/{id}`

Response: (204 No Content)

**Status Codes:**
- `204 No Content` - Note successfully deleted
- `404 Not Found` - Note with given ID does not exist

## Database Schema

```sql
CREATE TABLE notes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  content VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## ResponseEntity: Controlling HTTP Responses

All endpoints return `ResponseEntity<T>` which lets you control:
- **HTTP Status Code** - What status to return (200, 201, 204, 400, 404, etc.)
- **Response Body** - What data to include (or none)
- **Headers** - Custom headers (not used here but available)

Examples from this API:
```java
// Returns 201 Created with NoteResponse body
ResponseEntity.status(HttpStatus.CREATED).body(new NoteResponse(note))

// Returns 200 OK with NoteResponse body
ResponseEntity.ok(new NoteResponse(note))

// Returns 204 No Content with no body
ResponseEntity.noContent().build()

// Returns 400 Bad Request with no body
ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

// Returns 404 Not Found with no body
ResponseEntity.status(HttpStatus.NOT_FOUND).build()
```

This gives the controller full control over the HTTP response for different scenarios.

## Test Coverage

### Controller Tests
- Create note with valid content (201)
- Create note with empty content (400)
- Create note with null content (400)
- Create note with whitespace only (400)
- Create note with empty JSON (400)
- Get all notes (200)
- Get note by ID - found (200)
- Get note by ID - not found (404)
- Delete note - success (204)
- Delete note - not found (404)

### Service Tests
- Create note successfully
- Create note with empty content (throws exception)
- Create note with null content (throws exception)
- Get all notes
- Get note by ID - found
- Get note by ID - not found
- Delete note successfully
- Delete note - not found (throws exception)

## Error Handling

| Scenario | Status | Behavior |
|----------|--------|----------|
| POST with valid content | 201 | Note created |
| POST with empty/null content | 400 | Bad Request |
| GET all notes | 200 | Returns array (empty if none) |
| GET note by valid ID | 200 | Returns note |
| GET note by invalid ID | 404 | Not Found |
| DELETE note by valid ID | 204 | No Content |
| DELETE note by invalid ID | 404 | Not Found |

## Design Decisions

### 1. SQLite Database
File-based database stored as `notes.db`. No separate database server needed and zero configuration needed in order to run this. Sqlite is perfect for prototyping and demos. This is not suitable for high concurrency production. Future migration to PostgreSQL/MySQL would be straightforward down the line. 

---

### 2. Three-Tier Architecture
Strict separation: Controller (HTTP) → Service (Business Logic) → Repository (Data Access).

Each layer has one responsibility. Service logic is reusable if gRPC endpoints are added later. Testing is easier. It does add more files and classes, however, the testing payoff is worth it. Service tests pass regardless of HTTP Framework. Controller tests can verify HTTP behavior independently. 

---

### 3. DTO Pattern (NoteResponse)
API returns NoteResponse DTOs, not raw Note entities. Service works with entities; controller converts them. This allows for safe and secure database evolution independent of the API response. It adds an extra class and conversion logic, but is worth the security and evolutionary database benefits. 

---

### 4. ResponseEntity for HTTP Control
All endpoints return `ResponseEntity<T>` to explicitly set status codes (201, 204, 400, 404). Spring's ResponseEntity is the standard tool for this making it the obvious choice.

---

### 5. Validation at Service Layer
Empty content check happens in service layer, not controller. This allows business logic to be centralized and reusable across any interface. All callers of `createNote()` get validation, regardless of how they invoke it. It is not the standard for Spring, but was simple enough for this scope.

---

### 6. MockMvc for Testing
Tests use MockMvc to test HTTP layer without starting an actual server. This allows testing in milliseconds. It does not test the full HTTP stack, however, for this use case it is good enough for verifying requests/response handling.

---

## Future Improvements

- [ ] Migrate to PostgreSQL/MySQL for production
- [ ] Add authentication and user-scoped notes
- [ ] Add pagination/sorting for GET /notes
- [ ] Add filtering query (search by content and/ or time)
- [ ] Add request/response logging
- [ ] Formalized API documentation
- [ ] Enable CORS for browser clients
- [ ] Add actuator endpoints for health/metrics
