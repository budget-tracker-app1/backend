# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the local JAR file to the container
COPY target/BudgetApp-0.0.1-SNAPSHOT.jar app.jar

# Copy the SSL certificate into the container
COPY src/main/resources/certs/ca.pem /app/certs/ca.pem

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose port 8080 to access the application
EXPOSE 8080
