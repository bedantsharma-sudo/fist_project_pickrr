# 📚 Quotes API

A full-stack Spring Boot application that offers a RESTful and web-based platform to manage inspirational quotes. Includes secure user authentication, rate limiting along with Throttling, pagination, OAuth login, Redis caching, and JWT and Docker support.

---

## 🚀 Features

✅ User registration and login (Basic Auth + Google OAuth2)  
✅ Add, update, view, and delete quotes
✅ Fetch a Random Quote from the Database
✅ Index-based pagination  
✅ Rate limiting and Throtting  
✅ Redis caching for improved performance  
✅ JWT token generation and validation  
✅ HTML-based frontend using Thymeleaf  
✅ Docker support for MySQL and Redis

---

## 🧰 Tech Stack

| Layer         | Tools                                      |
|---------------|--------------------------------------------|
| Language      | Java 21                                    |
| Backend       | Spring Boot 3.5.3                          |
| Database      | MySQL *(or Dockerized MySQL)*             |
| Cache & Rate Limiting | Redis *(or Dockerized Redis)*     |
| Security      | Spring Security (Basic Auth), OAuth2 (Google) |
| Frontend      | Thymeleaf Templates                        |
| Build Tool    | Maven                                      |
| Containerization | Docker *(optional but recommended)*     |

---

## ⚙️ Setup Instructions

### 1. 🛠️ Clone the Repository

```bash
git clone <your-repo-url>
cd fist_project_pickrr
```

---

### 2. 📦 Install Dependencies

Ensure you have:

- ✅ Java 21  
- ✅ Maven  
- ✅ Docker *(recommended)*

---

### 3. 🐳 Option 1: Dockerized MySQL & Redis Setup (Recommended)

### 3. 🐳 Option 1: Dockerized MySQL & Redis Setup (Recommended)

This option uses Docker to quickly set up the MySQL and Redis dependencies.

#### 🔧 Step 1: Run Services Using Docker Compose

Use the provided `docker-compose.yml` file:

```bash
docker compose up -d
```

This will spin up:

- MySQL on port `3306`
- Redis on port `6379`

#### 📁 Sample `docker-compose.yml`

```yaml
services:
  mysql:
    image: 'mysql:latest'
    environment:
      - 'MYSQL_DATABASE=mydatabase'
      - 'MYSQL_PASSWORD=secret'
      - 'MYSQL_ROOT_PASSWORD=verysecret'
      - 'MYSQL_USER=myuser'
    ports:
      - '3306:3306'
    networks:
      - backend
    restart: unless-stopped

  redis:
    image: 'redis:latest'
    ports:
      - '6379:6379'
    networks:
      - backend
    restart: unless-stopped

networks:
  backend:
    driver: bridge
```

#### ⚙️ Step 2: Update `application.properties`

Make sure your `src/main/resources/application.properties` looks like this (with DB name, user, and password matching your Docker config):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase
spring.datasource.username=myuser
spring.datasource.password=secret

spring.redis.host=localhost
spring.redis.port=6379

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT & OAuth settings...
jwt.secret=your_very_secret_jwt_key
```

#### ✅ Step 3: Run the Application

After the containers are up, build and run your Spring Boot app:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

Your app will be available at: [http://localhost:8080](http://localhost:8080)



### 4. 🗃️ Option 2: Manual MySQL & Redis Installation

#### ➕ MySQL

1. Install MySQL:
   ```bash
   sudo apt update
   sudo apt install mysql-server
   ```
2. Create database:
   ```sql
   CREATE DATABASE quotes_db;
   ```

#### ➕ Redis

```bash
sudo apt update
sudo apt install redis
sudo systemctl enable redis
sudo systemctl start redis
```

Check Redis is running:

```bash
redis-cli ping
# Should return: PONG
```

---

### 5. 🧱 Build the Project

```bash
./mvnw clean install
# or on Windows:
mvnw.cmd clean install
```

---

### 6. ▶️ Run the Application

```bash
./mvnw spring-boot:run
```

App will run at:

📍 `http://localhost:8080`

---

## 👥 Default Auth Users

> You can create new users via `/register` page

| Username | Password | Role |
|----------|----------|------|
| user1    | pass1    | USER |

---

## 🌐 API & Web Endpoints

### 🔑 Authentication & Registration

| Method | Endpoint                    | Auth         | Description                       |
|--------|-----------------------------|--------------|-----------------------------------|
| GET    | `/Log`                      | ❌           | Login form (HTML)                 |
| GET    | `/loginWithGoogle`         | ❌           | Redirect to Google OAuth          |
| GET    | `/welcome?token={token}`   | ✅ (OAuth)   | Welcome page with JWT token       |
| GET    | `/register`                | ❌           | Registration form                 |
| POST   | `/register`                | ❌           | Register new user                 |

---

### 📝 Quote Management

| Method | Endpoint                | Auth | Description                  |
|--------|-------------------------|------|------------------------------|
| GET    | `/quotes`              | ❌   | List all quotes (paginated) |
| GET    | `/user/quotes/{id}`    | ✅   | View single quote           |
| POST   | `/user/quotes`         | ✅   | Create a new quote          |
| PUT    | `/user/quotes/{id}`    | ✅   | Update existing quote       |
| DELETE | `/user/quotes/{id}`    | ✅   | Delete quote                |

---

## 🧑‍💻 Developer Notes

- 🔒 **Rate Limiting**:  
  Implemented using Redis. Each user is throttled to 2 requests per millisecond.  


- ⚙️ **Authentication**:  
  - Basic Auth uses Spring Security with a custom `UserDetailsService`.  
  - Google OAuth2 login is configured via Spring Security OAuth client.  

- 🔑 **JWT Tokens**:  
  - Generated with `JwtUtil`.  


- 💾 **Redis Caching**:  

    
- 🛠️ **Database Setup (JPA + Hibernate)**:  
  - MySQL schema is auto-created.  
  - Entities: `User`, `Quote`
  -  `writer` Table

| Column   | Type         | Constraints         |
|----------|--------------|---------------------|
| username | VARCHAR(255) | UNIQUE              |
| password | VARCHAR(255) | NOT NULL            |
| role     | VARCHAR(20)  | DEFAULT `'USER'`    |

- `quote` Table

| Column  | Type         | Constraints              |
|---------|--------------|--------------------------|
| id      | VARCHAR(255) | PRIMARY KEY, UNIQUE      |
| content | TEXT         | NOT NULL                 |
| author  | VARCHAR(255) |                          |


- 🔐 **Security Notes**:  
  - Passwords stored using `BCryptPasswordEncoder`.  
    
- 🌐 **Web Frontend (Thymeleaf)**:  
  - Pages: Login, Dashboard, All Quotes  
  - Uses Spring MVC to pass data into templates.
  
---

## 🔄 Improvements / TODOs

- [ ] Replace Basic Auth with full JWT-based security
- [ ] Swagger/OpenAPI documentation for APIs
- [ ] Better error handling (404 pages, custom messages)

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
