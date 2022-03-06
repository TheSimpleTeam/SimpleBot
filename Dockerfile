FROM eclipse-temurin:17_35-jdk-focal
LABEL maintainer="Minemobs <minemobs.pro@gmail.com>"
WORKDIR /app
COPY lang lang/
COPY build/docker/libs libs/
COPY build/docker/classes classes/
ENTRYPOINT ["java", "-Xms256m", "-Xmx1024m", "-cp", "/app/resources:/app/classes:/app/libs/*", "fr.noalegeek.pepite_dor_bot.SimpleBot"]
EXPOSE 8080