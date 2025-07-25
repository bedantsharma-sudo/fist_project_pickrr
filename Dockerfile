FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/*.jar dockerApp.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "dockerApp.jar"]