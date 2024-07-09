# Stage 1: Build the application
FROM gradle:8.8.0-jdk17-jammy AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and settings file
COPY gradlew gradlew.bat ./
COPY gradle gradle
COPY settings.gradle.kts ./

# Copy the application build files and source code
COPY app/build.gradle.kts app/
COPY app/src app/src

# Make the Gradle wrapper executable and build the project
RUN chmod +x gradlew
RUN ./gradlew build

ARG COMMIT_HASH
ARG NODE_ENV
ENV COMMIT_HASH=$COMMIT_HASH
ENV NODE_ENV=$NODE_ENV

# Stage 2: Create the final image
# alpine은 no matching manifest for linux/arm64/v8 in the manifest list entries
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the built jar file from the first stage
COPY --from=build /app/app/build/libs/*.jar ./app.jar

ENV COMMIT_HASH=$COMMIT_HASH
ENV NODE_ENV=$NODE_ENV

# Expose the port that your application will run on
EXPOSE 9092

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
