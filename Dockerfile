#
# Build stage
#
FROM gradle:jdk8 as build

COPY src /home/app/src
COPY build.gradle /home/app
COPY settings.gradle /home/app

RUN gradle build -p /home/app

#
# Package stage
#
FROM openjdk:8-jdk

ARG JAR_FILE=build/libs/*SNAPSHOT.jar
COPY --from=build /home/app/${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]