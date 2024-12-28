# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy Maven wrapper files and the project source code into the container
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/

# Build the application using Maven inside the container
RUN ./mvnw clean package -DskipTests

# (Optional) Copy the SSL certificate into the container if needed
# If you don't want the certificate file, just comment this line
COPY src/main/resources/certs/ca.pem /app/certs/ca.pem

# Expose port 8080 to access the application
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "target/BudgetApp-0.0.1-SNAPSHOT.jar"]
