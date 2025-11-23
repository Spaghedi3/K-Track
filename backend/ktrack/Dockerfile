# 1. Build Stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

# 2. Runtime Stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
