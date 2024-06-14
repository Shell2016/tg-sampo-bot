FROM eclipse-temurin:17-alpine

ENV TZ Europe/Moscow
WORKDIR /app
COPY build/libs/*.jar sampobot.jar
ENTRYPOINT ["java", "-jar", "sampobot.jar"]