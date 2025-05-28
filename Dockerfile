FROM gradle:8.4-jdk21 AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

COPY . .
RUN gradle build --no-daemon -x test --build-cache

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/ /app/
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar $(ls /app/*SNAPSHOT.jar | grep -v plain)"]
