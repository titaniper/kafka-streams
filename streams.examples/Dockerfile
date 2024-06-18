# Step 1: Use Maven to build the application
FROM maven:3.9.7-eclipse-temurin-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml file and download the dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package

# Step 2: Use Eclipse Temurin to run the application
FROM eclipse-temurin:17-jre

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the Maven image
COPY --from=build /app/target/*.jar app.jar

# Specify the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
