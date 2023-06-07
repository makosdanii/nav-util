FROM maven:3.9.2-eclipse-temurin-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and source code
COPY ./server/pom.xml .
COPY ./server/src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use a Java image as the base image
FROM openjdk:17
EXPOSE 8081

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/server-0.0.1-SNAPSHOT.jar .
# Specify the command to run the application
CMD ["java", "-jar", "server-0.0.1-SNAPSHOT.jar"]