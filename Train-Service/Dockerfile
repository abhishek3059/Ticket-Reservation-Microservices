FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Build & install CommonDTO
COPY CommonDTO/pom.xml CommonDTO/
COPY CommonDTO/src CommonDTO/src
RUN mvn -f CommonDTO/pom.xml clean install -DskipTests -B

# Build Train-Service
COPY Train-Service/pom.xml Train-Service/
COPY Train-Service/src Train-Service/src
RUN mvn -f Train-Service/pom.xml clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/Train-Service/target/*.jar app.jar
EXPOSE 8065
ENTRYPOINT ["java", "-jar", "app.jar"]
