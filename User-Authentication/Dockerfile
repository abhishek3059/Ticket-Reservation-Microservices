FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Build & install CommonDTO
COPY CommonDTO/pom.xml CommonDTO/
COPY CommonDTO/src CommonDTO/src
RUN mvn -f CommonDTO/pom.xml clean install -DskipTests -B

# Build User-Authentication
COPY User-Authentication/pom.xml User-Authentication/
COPY User-Authentication/src User-Authentication/src
RUN mvn -f User-Authentication/pom.xml clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/User-Authentication/target/*.jar app.jar
EXPOSE 8041
ENTRYPOINT ["java", "-jar", "app.jar"]
