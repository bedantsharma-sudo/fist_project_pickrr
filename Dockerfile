# Use official JDK image
FROM openjdk:21-jdk

# Set working directory
WORKDIR /app

# Copy built jar (replace with your jar name if needed)
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port (replace with your Spring Boot port if different)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
