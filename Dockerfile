FROM eclipse-temurin:17-alpine

ENV TZ Europe/Moscow
WORKDIR /app
COPY build/libs/*.jar sampobotservice.jar
ENTRYPOINT ["java", "-jar", "sampobotservice.jar"]