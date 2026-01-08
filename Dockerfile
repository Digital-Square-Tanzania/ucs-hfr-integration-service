# Use an official OpenJDK runtime as a parent image
FROM amazoncorretto:17

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle wrapper files to the container
COPY gradlew /app/
COPY gradle /app/gradle

# Copy the build configuration files to the container
COPY build.gradle /app/
COPY settings.gradle /app/

# Copy the source code to the container
COPY src /app/src

# Build the project
RUN ./gradlew clean shadowJar

# Specify the default command to run on boot
CMD ["java", "-jar", "/app/build/libs/ucs-lab-module-integration-service-1.0.0.jar"]