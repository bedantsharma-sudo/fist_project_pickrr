# 📚 Quotes API

A Spring Boot application that provides a RESTful API for managing and retrieving quotes with user authentication and rate limiting.

---

## 🚀 Features

- Get a random quote
- Add a new quote (requires authentication)
- View all quotes (requires authentication)
- Rate limiting using Redis (e.g., 5 requests/min/user)
- Basic Auth and optional OAuth
- HTML-based UI

---

## 🔧 Tech Stack

- Java 17 / Java 21
- Spring Boot 3.x
- MySQL
- Redis (for rate limiting)
- Spring Security (Basic Auth)
- Thymeleaf (HTML templates)
- Maven

---

## ⚙️ Setup Instructions

### 1. 🛠️ Clone the Repository

```bash
git clone <your-repo-url>
cd fist_project_pickrr
```

---

### 2. 🧩 Install Dependencies

Ensure you have the following installed:

- Java 17 or 21
- Maven
- MySQL
- Redis

#### 🔴 To install Redis (Ubuntu):

```bash
sudo apt update
sudo apt install redis
```

Verify Redis is running:

```bash
redis-cli ping
# Should return: PONG
```

---

### 3. 🗃️ MySQL Setup

1. Start your MySQL server.
2. Create a new database:

```sql
CREATE DATABASE quotes_db;
```

3. Update your database credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quotes_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

---

### 4. 📦 Build the Project

Run the following command to build and install dependencies:

```bash
./mvnw clean install
```

On Windows:

```bash
mvnw.cmd clean install
```

---

### 5. ▶️ Run the Application

```bash
./mvnw spring-boot:run
```

Once running, the app will be available at:  
📍 `http://localhost:8080`

---

## 🔐 Default Auth Users

| Username | Password | Role   |
|----------|----------|--------|
| user1    | pass1    | USER   |
| admin    | admin    | ADMIN  |

---

## 🧪 API Endpoints

| Method | Endpoint         | Auth Required | Description            |
|--------|------------------|----------------|------------------------|
| GET    | `/quotes/random` | ❌             | Get a random quote     |
| POST   | `/quotes`        | ✅             | Add a new quote        |
| GET    | `/quotes/all`    | ✅             | Get all quotes         |

### Example Request with Basic Auth:

```bash
curl -u user1:pass1 http://localhost:8080/quotes/all
```

---

## 🐳 Docker Support (Optional)

To run MySQL and Redis using Docker:

```bash
docker compose up
```

Make sure your `compose.yaml` includes services for:

- Redis (port 6379)
- MySQL (port 3306)

---

## 🧑‍💻 Developer Notes

- Redis is used for rate limiting through `RateLimiterService`.
- HTML templates are located in `src/main/resources/templates/`.
- MySQL entities are managed via JPA and auto-created if `ddl-auto=update`.
- OAuth2 login (if used) is configured via `SecurityConfig.java`.

---

## 🔄 Known Issues / Improvements

- Add pagination to `/quotes/all`
- Replace Basic Auth with JWT
- Improve error handling and validation
- Add Swagger documentation
- Add user registration feature

---

## 🧪 Sample Test Users and cURL Examples

Add a new quote (requires auth):

```bash
curl -u user1:pass1 -X POST http://localhost:8080/quotes \
-H "Content-Type: application/json" \
-d '{"content": "Keep pushing forward", "author": "Anonymous"}'
```

---

