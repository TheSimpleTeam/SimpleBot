FROM eclipse-temurin:17-jdk-focal
LABEL maintainer="Minemobs <minemobs.pro@gmail.com>"
WORKDIR /app
COPY src/main/resources/lang lang/
COPY build/docker/libs libs/
COPY build/docker/classes classes/
ENTRYPOINT ["java", "-Xms256m", "-Xmx1024m", "-cp", "/app/resources:/app/classes:/app/libs/*", "SimpleBot"]
EXPOSE 8080
