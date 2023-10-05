FROM eclipse-temurin:17-alpine

ARG JAR_FILE=build/libs/*.jar

WORKDIR /app

COPY ${JAR_FILE} sampobot.jar

ENV TZ Europe/Moscow

ENTRYPOINT ["java", "-jar", "sampobot.jar"]