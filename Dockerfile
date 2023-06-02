# Base image
FROM openjdk:17-oracle

# Set the working directory
WORKDIR /app

# Copy the compiled Spring Boot JAR file
COPY target/MIAGELand-0.0.1-SNAPSHOT.jar app.jar

# Expose the necessary port
EXPOSE 8080

# Specify the command to run the Spring Boot app
CMD ["java", "-jar", "app.jar"]
