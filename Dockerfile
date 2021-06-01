# Alpine Linux with OpenJDK JRE
FROM adoptopenjdk:8-jdk-hotspot
COPY build/libs/pepite_dor_bot-1.0-SNAPSHOT-all.jar /bot.jar
CMD ["usr/bin/java", "-jar", "/bot.jar"]