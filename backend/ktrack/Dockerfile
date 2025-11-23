FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy everything
COPY . .

# Make mvnw executable
RUN ["chmod", "+x", "./mvnw"]

# Build the JAR
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
