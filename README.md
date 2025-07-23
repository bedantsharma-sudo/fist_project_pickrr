# 📚 Quotes API

A full-stack Spring Boot application that offers a RESTful and web-based platform to manage inspirational quotes. Includes secure user authentication, rate limiting, pagination, OAuth login, and Redis caching.

---

## 🚀 Features

✅ User registration and login (Basic Auth + Google OAuth2)  
✅ Add, update, view, and delete quotes  
✅ Pagination and sorting of quotes  
✅ Rate limiting with Redis  
✅ Caching for performance optimization  
✅ JWT token support  
✅ HTML-based frontend using Thymeleaf

---

## 🧰 Tech Stack

| Layer | Tools |
|-------|-------|
| Language | Java 21 |
| Backend | Spring Boot 3.5.3 |
| Database | MySQL |
| Cache & Rate Limiting | Redis |
| Security | Spring Security (Basic Auth), OAuth2 (Google) |
| Frontend | Thymeleaf Templates |
| Build Tool | Maven |

---

## ⚙️ Setup Instructions

### 1. 🛠️ Clone the Repository

```bash
git clone <your-repo-url>
cd fist_project_pickrr
```

---

### 2. 📦 Install Dependencies

Ensure the following are installed:

- ✅ Java 21
- ✅ Maven
- ✅ MySQL
- ✅ Redis

#### ➕ Installing Redis (Ubuntu):

```bash
sudo apt update
sudo apt install redis
```

✅ Check if Redis is running:

```bash
redis-cli ping
# Should return: PONG
```

---

### 3. 🗃️ MySQL Database Setup

1. Start MySQL server
2. Create database:

```sql
```

3. Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quotes_db
spring.datasource.username=root
spring.datasource.password=<your_mysql_password>
spring.jpa.hibernate.ddl-auto=update
jwt.secret=your_very_secret_jwt_key
```

---

### 4. 🧱 Build the Project

```bash
./mvnw clean install
# or on Windows:
mvnw.cmd clean install
```

---

### 5. ▶️ Run the Application

```bash
./mvnw spring-boot:run
```

App will run at:

📍 `http://localhost:8080`

---

## 👥 Default Auth Users

> You can create new users via `/register` page

| Username | Password | Role   |
|----------|----------|--------|
| user1    | pass1    | USER   |

---

## 🌐 API & Web Endpoints

### 🔑 Authentication & Registration

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/Log` | ❌ | Login form (HTML) |
| GET | `/loginWithGoogle` | ❌ | Redirect to Google OAuth |
| GET | `/welcome?token={token}` | ✅ (OAuth) | Welcome page with JWT token |
| GET | `/register` | ❌ | Registration form |
| POST | `/register` | ❌ | Register new user |

---

### 📝 Quote Management

| Method | Endpoint | Auth | Description |
|--------|----------|-----|-------------|
| GET | `/quotes` | ❌ | List of all quotes (paginated, sortable) |
| GET | `/user/quotes/{id}` | ✅ | View single quote |
| POST | `/user/quotes` | ✅ | Create a new quote |
| PUT | `/user/quotes/{id}` | ✅ | Update existing quote |
| DELETE | `/user/quotes/{id}` | ✅ | Delete quote |

---


## 🧑‍💻 Developer Notes

- JWTs are generated using `JwtUtil` and validated by a custom filter
- OAuth is configured with Google via Spring Security
- HTML templates: `src/main/resources/templates/`
- Redis is used for both rate limiting and caching quote data
- MySQL schema is auto-generated via JPA
- Initial test quotes and users are optionally loaded into the DB on startup using a CommandLineRunner.
- Passwords are securely hashed using BCryptPasswordEncoder. 
- Quotes are paginated and sorted by ID for consistency.


---

## 🔄 Improvements / TODOs

- [ ] Switch fully to JWT instead of Basic Auth
- [ ] Add Swagger/OpenAPI docs
- [ ] Better error handling (404, 401, etc.)
- [ ] Dockerize app and Redis setup

---

## 📬 Example cURL Commands

### ➕ Add a Quote (Basic Auth):

```bash
curl -u user1:pass1 -X POST http://localhost:8080/user/quotes \
-H "Content-Type: application/json" \
-d '{"content": "Keep pushing forward", "author": "Anonymous"}'
```

### 🔍 Get Auth Info (JWT):

```bash
curl -H "Authorization: Bearer <your_token>" \
http://localhost:8080/check
```

---
