# Stage 1: Build the application
FROM maven:3.9.7-eclipse-temurin-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the built jar file from the first stage
COPY --from=build /app/target/streams.examples-0.1.jar ./app.jar

# Expose the port that your application will run on
EXPOSE 9092

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
