FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Build & install CommonDTO
COPY CommonDTO/pom.xml CommonDTO/
COPY CommonDTO/src CommonDTO/src
RUN mvn -f CommonDTO/pom.xml clean install -DskipTests -B

# Build Payment-Service
COPY Payment-Service/pom.xml Payment-Service/
COPY Payment-Service/src Payment-Service/src
RUN mvn -f Payment-Service/pom.xml clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/Payment-Service/target/*.jar app.jar
EXPOSE 8067
ENTRYPOINT ["java", "-jar", "app.jar"]
