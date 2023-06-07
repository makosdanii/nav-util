FROM maven:3.9.2-eclipse-temurin-17 AS build
RUN mvn clean package

FROM openjdk:17
WORKDIR /app
COPY ./target/server-0.0.1-SNAPSHOT.jar /app
EXPOSE 8081
ENTRYPOINT ["java","-jar","server-0.0.1-SNAPSHOT.jar"]