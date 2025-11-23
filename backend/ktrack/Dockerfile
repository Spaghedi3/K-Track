# 1. Build stage (JDK, includes Maven)
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw
# Build and run tests
RUN ./mvnw clean package

# 2. Runtime stage (JRE, smaller, no Maven)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
