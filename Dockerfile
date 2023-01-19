FROM amazoncorretto:17

ARG JAR_FILE=build/libs/*.jar
RUN mkdir /opt/bots
COPY ${JAR_FILE} /opt/bots/sampobot.jar

ENV TZ Europe/Moscow

ENTRYPOINT ["java", "-jar", "/opt/bots/sampobot.jar"]