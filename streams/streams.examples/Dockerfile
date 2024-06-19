# Stage 1: Build the application
FROM maven:3.9.7-eclipse-temurin-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
COPY . .

# Define build arguments
ARG KAFKA_BROKER
ARG KAFKA_TOPIC

# Set environment variables from build arguments
ENV KAFKA_BROKER=${KAFKA_BROKER}
ENV KAFKA_TOPIC=${KAFKA_TOPIC}

#RUN mvn dependency:go-offline -B
RUN mvn clean package

# Stage 2: Create the final image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the built jar file from the first stage
COPY --from=build /app/target/streams.examples-0.1.jar ./app.jar

# Expose the port that your application will run on
EXPOSE 9092

# Define environment variables again
ARG KAFKA_BROKER
ARG KAFKA_TOPIC

# Set environment variables
ENV KAFKA_BROKER=${KAFKA_BROKER}
ENV KAFKA_TOPIC=${KAFKA_TOPIC}

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
