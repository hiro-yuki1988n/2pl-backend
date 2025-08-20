# Stage 1: Build the JAR
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the app
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 7080
ENTRYPOINT ["java", "-jar", "app.jar"]



#FROM openjdk:21-jdk-slim
#
#WORKDIR /app
#
#COPY target/Mkoba-Management-System-0.0.1-SNAPSHOT.jar app.jar
#
#EXPOSE 7080
#
#CMD [ "java", "-jar", "app.jar" ]