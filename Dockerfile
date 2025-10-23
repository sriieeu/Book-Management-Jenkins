# Multi-stage Dockerfile for building and running the Spring Boot application

# ---------------------------
# Build stage -- compile app
# ---------------------------
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the files needed to download dependencies first (cache layer)
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Download all project dependencies to leverage Docker layer caching
RUN mvn -B -f pom.xml dependency:go-offline

# Copy the application source and build the jar (skip tests for faster image builds)
COPY src ./src
RUN mvn -B -DskipTests package

# ---------------------------
# Runtime stage -- run app
# ---------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the packaged jar from the build stage
COPY --from=build /app/target/*.jar ./app.jar

# Expose the application port (matches application.properties server.port)
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "./app.jar"]
