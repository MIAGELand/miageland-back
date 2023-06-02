# Base image
FROM maven:3.8.3-openjdk-17 AS builder

# Set the working directory
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Build the project dependencies
RUN mvn dependency:go-offline -B

# Copy the application source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Create a new image with JRE only
FROM openjdk:17-jdk-oracle

# Set the working directory
WORKDIR /app

# Copy the JAR file from the builder image
COPY --from=builder /app/target/MIAGELand-0.0.1-SNAPSHOT.jar app.jar

# Specify the command to run the Spring Boot app
CMD ["java", "-jar", "app.jar"]
