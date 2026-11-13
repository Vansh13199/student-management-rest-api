# Student Management REST API

A Spring Boot REST API for managing student records, built with Spring Data JPA and MySQL.

## Tech stack

- **Java 17**, **Spring Boot 3.3.5**
- **Spring Web** (REST endpoints), **Spring Data JPA** (persistence), **Bean Validation** (Jakarta)
- **MySQL** (production database), **H2** (in-memory, tests only)
- **Maven** build

## Features

- Create, read, update, and delete student records
- Look up students by course (case-insensitive)
- Bean Validation on input (name, email, course, age 1–120)
- Uniform JSON error responses with the correct HTTP status codes
  (201 on create, 400 on validation errors, 404 when a student is not found,
  409 on duplicate email, 204 on delete)

## Project structure

```
student-management-rest-api/
├── pom.xml
├── db/
│   └── init.sql                  # optional manual MySQL setup
└── src/
    ├── main/
    │   ├── java/com/vansh/studentmanagement/
    │   │   ├── StudentManagementApplication.java
    │   │   ├── controller/StudentController.java
    │   │   ├── service/StudentService.java
    │   │   ├── repository/StudentRepository.java
    │   │   ├── model/Student.java
    │   │   └── exception/         # global handler + error types
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/.../StudentControllerTest.java   # MockMvc integration tests (H2)
        └── resources/application.properties
```

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use the included wrapper if present)
- MySQL 8 running locally (skip this if you only want to run the tests)

## Database setup

The application creates the `student_db` database and `students` table itself
(`createDatabaseIfNotExist=true` + `ddl-auto=update`), so usually no manual
setup is needed. If you prefer to set things up by hand:

```bash
mysql -u root -p < db/init.sql
```

`db/init.sql` creates database `student_db` and table `students`
(`id`, `name`, `email` (unique), `course`, `age`).

## Configuration

Database settings in `src/main/resources/application.properties` can be
overridden with environment variables:

| Variable       | Default                                                                 | Meaning        |
| -------------- | ----------------------------------------------------------------------- | -------------- |
| `DB_URL`       | `jdbc:mysql://localhost:3306/student_db?...`                            | JDBC URL       |
| `DB_USERNAME`  | `root`                                                                  | DB user        |
| `DB_PASSWORD`  | *(empty)*                                                               | DB password    |

Example:

```bash
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
```

> Never commit real credentials. `.gitignore` already excludes `.env` files.

## Run

```bash
mvn spring-boot:run
```

The API starts on **http://localhost:8080**.

## API endpoints

Base URL: `http://localhost:8080`

| Method | Endpoint                        | Success | Description              |
| ------ | ------------------------------- | ------- | ------------------------ |
| POST   | `/api/students`                 | 201     | Add a new student        |
| GET    | `/api/students`                 | 200     | Retrieve all students    |
| GET    | `/api/students/{id}`            | 200     | Retrieve student by ID   |
| PUT    | `/api/students/{id}`            | 200     | Update student details   |
| DELETE | `/api/students/{id}`            | 204     | Delete a student         |
| GET    | `/api/students/course/{course}` | 200     | Retrieve students by course (case-insensitive) |

### Student object

```json
{
  "id": 1,
  "name": "Aarav Sharma",
  "email": "aarav@example.com",
  "course": "B.Tech CSE",
  "age": 20
}
```

Validation rules: `name` required (max 100 chars), `email` required and must be
a valid email (max 150 chars, unique), `course` required (max 100 chars),
`age` between 1 and 120.

### Examples (curl)

**Add a student**

```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"name":"Aarav Sharma","email":"aarav@example.com","course":"B.Tech CSE","age":20}'
```

**Get all students**

```bash
curl http://localhost:8080/api/students
```

**Get student by ID**

```bash
curl http://localhost:8080/api/students/1
```

**Update a student**

```bash
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Aarav S.","email":"aarav@example.com","course":"B.Tech IT","age":21}'
```

**Delete a student**

```bash
curl -X DELETE http://localhost:8080/api/students/1
```

**Get students by course**

```bash
curl http://localhost:8080/api/students/course/B.Tech%20CSE
```

### Error format

All errors return the same JSON shape:

```json
{
  "timestamp": "2026-11-13T17:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Student not found with id: 99",
  "path": "/api/students/99",
  "fieldErrors": null
}
```

`fieldErrors` is only populated on validation failures (HTTP 400), mapping each
invalid field to its message.

## Testing

### Automated tests

```bash
mvn test
```

Tests run against an in-memory H2 database (no MySQL needed) and cover all six
endpoints, including duplicate-email (409), validation errors (400),
not-found (404), and delete (204) cases.

### Postman

A ready-to-import collection lives at
[`postman/Student_Management_API.postman_collection.json`](postman/Student_Management_API.postman_collection.json).
Import it into Postman, start the app, and run the requests to exercise every
endpoint (including a 404 check after delete). The collection uses `base_url`
(default `http://localhost:8080`) and `student_id` variables — edit them if
your server runs elsewhere or after creating a student.

## Notes

- `spring.jpa.hibernate.ddl-auto=update` is used for development convenience;
  use `validate` or `none` in production.
- CORS is open (`@CrossOrigin(origins = "*")`) for local development/testing.
