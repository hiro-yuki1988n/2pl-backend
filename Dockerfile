FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/Mkoba-Management-System-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 7080

CMD [ "java", "-jar", "app.jar" ]