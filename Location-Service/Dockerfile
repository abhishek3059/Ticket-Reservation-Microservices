FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Build & install CommonDTO
COPY CommonDTO/pom.xml CommonDTO/
COPY CommonDTO/src CommonDTO/src
RUN mvn -f CommonDTO/pom.xml clean install -DskipTests -B

# Build Location-Service
COPY Location-Service/pom.xml Location-Service/
COPY Location-Service/src Location-Service/src
RUN mvn -f Location-Service/pom.xml clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/Location-Service/target/*.jar app.jar
EXPOSE 8066
ENTRYPOINT ["java", "-jar", "app.jar"]
