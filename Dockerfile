FROM eclipse-temurin:17-jdk-focal
LABEL maintainer="Minemobs <minemobs.pro@gmail.com>"
WORKDIR /app
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY src/main/resources/config .
COPY build/libs/simplebot-1.0-all.jar simplebot.jar
EXPOSE 8080 10909
ENTRYPOINT exec java $JAVA_OPTS -jar simplebot.jar