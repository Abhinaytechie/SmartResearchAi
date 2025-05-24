# Use an OpenJDK base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the image
COPY target/research-assisant-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (same as your Spring Boot app)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
