FROM openjdk:8-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} bot.jar
ENTRYPOINT ["java", "-jar", "bot.jar"]