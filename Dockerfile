FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace
COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
RUN addgroup -S finance && adduser -S finance -G finance
COPY --from=build --chown=finance:finance /workspace/build/libs/*.jar app.jar

USER finance
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
