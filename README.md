# 📚 Quotes API

A Spring Boot application that provides a RESTful API for managing and retrieving quotes with user authentication and rate limiting.

---

## 🚀 Features

- Add a new quote (requires authentication)
- View all quotes (requires authentication)
- Rate limiting using Redis
- Basic Auth and OAuth
- HTML-based UI
- Cashing using Redis

---

## 🔧 Tech Stack

- Java 21
- Spring Boot 3.5.3
- MySQL
- Redis (for rate limiting and cashing)
- Spring Security (Basic Auth)
- Oauth 2.0 (Google)
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

- Java 21
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

---

## 🧪 API Endpoints

| Method | Endpoint         | Auth Required | Description            |
|--------|------------------|---------------|------------------------|
| | HTTP Method | Endpoint                 | Auth Required | Description                                          |
| ----------- | ------------------------- | ------------- | ---------------------------------------------------- |
| `GET`       | `/Log`                    | ❌ No         | Show login page (form)                               |
| `GET`       | `/loginWithGoogle`        | ❌ No         | Redirect to Google OAuth                             |
| `GET`       | `/welcome?token={token}`  | ✅ Yes (OAuth) | Show welcome message with JWT                        |
| `GET`       | `/register`               | ❌ No         | Show user registration form                          |
| `POST`      | `/register`               | ❌ No         | Register a new user                                  |
| `GET`       | `/quotes`                 | ❌ No         | Paginated, sorted list of quotes                     |
| `GET`       | `/user/quotes/{id}`       | ✅ Yes        | View details of a single quote                       |
| `POST`      | `/user/quotes`            | ✅ Yes        | Add a new quote                                      |
| `PUT`       | `/user/quotes/{id}`       | ✅ Yes        | Update an existing quote                             |
| `DELETE`    | `/user/quotes/{id}`       | ✅ Yes        | Delete a quote                                       |





---

## 🧑‍💻 Developer Notes

- Redis is used for rate limiting through `RateLimiterService`.
- HTML templates are located in `src/main/resources/templates/`.
- MySQL entities are managed via JPA and auto-created if `ddl-auto=update`.
- OAuth2 login (if used) is configured via `SecurityConfig.java`.

---

## 🔄 Known Issues / Improvements

- Replace Basic Auth with JWT
- Improve error handling and validation
- Add Swagger documentation

---

## 🧪 Sample Test Users and cURL Examples

Add a new quote (requires auth):

```bash
curl -u user1:pass1 -X POST http://localhost:8080/user/quotes \
-H "Content-Type: application/json" \
-d '{"content": "Keep pushing forward", "author": "Anonymous"}'
```

---

