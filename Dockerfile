# 🛠 Build Stage
FROM gradle:8.5-jdk21 AS build

WORKDIR /home/gradle/project

COPY --chown=gradle:gradle . .

RUN gradle build --no-daemon

# 🚀 Runtime Stage
FROM openjdk:21-jdk-slim

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]