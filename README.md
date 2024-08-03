# Backend - E-commerce Application

## Description
This is a Spring Boot application for the application. The application provides RESTful APIs for various operations. This document provides instructions on how to build and run the application using Docker.

## Prerequisites
- **Docker**: Ensure Docker is installed on your machine. You can download it from [Docker's official website](https://www.docker.com/get-started).
- **Maven**: Make sure Maven is installed. You can download it from [Maven's official website](https://maven.apache.org/download.cgi).

## Project Structure
The project directory structure should look like this:


## Dockerfile
Here is the Dockerfile used to build and run the application:

```dockerfile
# Use Maven to build the application
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Use OpenJDK to run the application
FROM openjdk:17.0.1-jdk-slim
COPY --from=build target/Backend-0.0.1-SNAPSHOT.jar Backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "Backend.jar"]
docker build -t backend:latest .
docker run -p 5050:5050 backend:latest
