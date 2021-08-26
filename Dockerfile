FROM openjdk:8-jdk-alpine

ARG JAR_FILE=build/libs/Team-PandaN-Back-0.0.1-SNAPSHOT.jar
ARG PROPERTIES_FILE=src/main/resources/application.yml

COPY ${JAR_FILE} app.jar
COPY ${PROPERTIES_FILE} application-set1.yml
COPY ${PROPERTIES_FILE} application-set2.yml

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/app.jar"]