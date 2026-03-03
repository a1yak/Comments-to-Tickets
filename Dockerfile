# Stage 1: build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Stage 2: run
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/comments-to-tickets-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
CMD ["java", "-jar", "comments-to-tickets-0.0.1-SNAPSHOT.jar"]