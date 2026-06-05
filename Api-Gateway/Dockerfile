FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY Api-Gateway/pom.xml Api-Gateway/
COPY Api-Gateway/src Api-Gateway/src
RUN mvn -f Api-Gateway/pom.xml clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/Api-Gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
