# Task Management System

Spring Boot application with REST API and web interface for managing users and tasks.

## Technology Stack

- Spring Boot 3.5.6
- Java 21
- Spring Data JPA
- H2 Database
- Thymeleaf
- Bootstrap 5
- Gradle

## Features

- User management (CRUD)
- Task management (CRUD)
- Task assignment to users
- Task status tracking (Pending, In Progress, Completed, Cancelled)
- Task priorities (Low, Medium, High, Urgent)
- Due date tracking
- Overdue task detection
- Search functionality
- REST API endpoints

## Running the Application

```bash
./gradlew bootRun
```

Access the application at http://localhost:8080

## Database Access

H2 Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:taskdb
- Username: sa
- Password: password

## API Endpoints

### Users
- GET /api/users
- GET /api/users/{id}
- POST /api/users
- PUT /api/users/{id}
- DELETE /api/users/{id}

### Tasks
- GET /api/tasks
- GET /api/tasks/{id}
- POST /api/tasks
- PUT /api/tasks/{id}
- DELETE /api/tasks/{id}
- GET /api/tasks/overdue
- PATCH /api/tasks/{id}/complete

## Project Structure

```
src/main/java/com/example/demo/
├── controller/
│   ├── api/     # REST controllers
│   └── web/     # Web controllers
├── model/       # JPA entities
├── repository/  # Data repositories
├── service/     # Business logic
└── config/      # Configuration
```
