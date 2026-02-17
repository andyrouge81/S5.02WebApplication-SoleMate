# SoleMate Backend

## 📄 Description - Exercise Statement
SoleMate Backend is an academic IT Academy project.  
The exercise consists of building a secure REST API for a feet-focused social platform, including authentication, role-based authorization, feet management, reviews, swipe actions, and admin tools.

Main features in this repository:
- JWT authentication (register, login, current user).
- Role-based access control (`ROLE_USER`, `ROLE_ADMIN`).
- Feet CRUD operations.
- Reviews CRUD operations with ownership rules.
- Swipe actions (like/dislike) per user.
- Admin user management with pagination and search.
- OpenAPI/Swagger documentation.

## 💻 Technologies Used
- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (`jjwt`)
- PostgreSQL (runtime database)
- H2 (tests)
- Maven Wrapper (`mvnw`)
- Springdoc OpenAPI (Swagger UI)
- JUnit + Spring Boot Test

## 📋 Requirements
- Java 17 installed
- Maven (optional if you use `./mvnw`)
- PostgreSQL running locally (or remote DB for deployment)
- Database created (default local config expects `solemate_db`)
- Environment/config values:
  - Local (`application.yml`):
    - DB URL: `jdbc:postgresql://localhost:5432/solemate_db`
    - DB user: `user`
    - DB password: `user1234`
    - JWT secret and expiration configured
  - Deployment (`application-render.yml`):
    - `SPRING_DATASOURCE_URL`
    - `SPRING_DATASOURCE_USERNAME`
    - `SPRING_DATASOURCE_PASSWORD`
    - `JWT_SECRET`
    - `JWT_EXPIRATION_MS` (optional, default 86400000)

## 🛠️ Installation
1. Clone this repository.
2. Configure PostgreSQL and create database `solemate_db` (or adapt datasource values).
3. Review and update `src/main/resources/application.yml` if needed.
4. Build dependencies:

```bash
./mvnw clean install
```

## ▶️ Execution
Run in local development:

```bash
./mvnw spring-boot:run
```

Default local URL:
- `http://localhost:8080`

Swagger/OpenAPI:
- `http://localhost:8080/swagger-ui.html`
- API docs JSON: `http://localhost:8080/v3/api-docs`

Run tests:

```bash
./mvnw test
```

Main API groups:
- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`
- `GET /feet`, `POST /feet`, `PUT /feet/{footId}`, `DELETE /feet/{footId}`
- `GET /feet/{footId}/reviews`, `POST /feet/{footId}/reviews`
- `PUT /feet/reviews/{reviewId}`, `DELETE /feet/reviews/{reviewId}`
- `POST /feet/{footId}/swipe`, `GET /feet/swipes/me`
- `GET /admin/users`, `PUT /admin/users/{userId}`, `DELETE /admin/users/{userId}`

## 🌐 Deployment
Production deployment checklist:
1. Set `server.port` (Render uses `PORT` env variable, already supported).
2. Set production environment variables:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `JWT_SECRET`
   - `JWT_EXPIRATION_MS` (optional)
3. Build artifact:

```bash
./mvnw clean package
```

4. Run JAR:

```bash
java -jar target/WebAppSoleMate-0.0.1-SNAPSHOT.jar
```

5. Ensure CORS allowed origins include your frontend domain in `SecurityConfig`.

## 🤝 Contributions
Contributions are welcome.

Recommended workflow:
1. Create a feature branch.
2. Keep code style and architecture consistent.
3. Add/update tests for changed behavior.
4. Run `./mvnw test` before submitting.
5. Open a Pull Request with a clear technical summary.
