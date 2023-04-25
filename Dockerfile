FROM amazoncorretto:17-alpine-jdk

ARG JAR_FILE=build/libs/*.jar

WORKDIR /app

COPY ${JAR_FILE} sampobot.jar

ENV TZ Europe/Moscow

ENTRYPOINT ["java", "-jar", "/app/sampobot.jar"]