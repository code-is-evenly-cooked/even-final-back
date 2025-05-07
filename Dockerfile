FROM gradle:8.4-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon -x test

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/ /app/
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar $(ls /app/*SNAPSHOT.jar | grep -v plain)"]
