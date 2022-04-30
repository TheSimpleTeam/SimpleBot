FROM eclipse-temurin:18-jdk-focal
LABEL maintainer="Minemobs <minemobs.pro@gmail.com>"
WORKDIR /app
COPY src/main/resources/lang lang/
COPY build/docker/libs libs/
COPY build/docker/classes classes/
ENTRYPOINT ["java", "-Xms256m", "-Xmx1024m", "-cp", "/app/resources:/app/classes:/app/libs/*", "net.thesimpleteam.simplebot.SimpleBot"]
EXPOSE 8080
