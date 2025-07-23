# Use official JDK image
FROM docker.io/library/openjdk:21-jdk

# Set working directory
WORKDIR /app

# Copy built jar (replace with your jar name if needed)
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=default \
    SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/test2?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC" \
    SPRING_DATASOURCE_USERNAME=root \
    SPRING_DATASOURCE_PASSWORD=1234 \
    SPRING_REDIS_HOST=redis \
    SPRING_REDIS_PORT=6379 \
    SPRING_CACHE_TYPE=redis

# Expose port (replace with your Spring Boot port if different)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
