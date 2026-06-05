FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY Service-Registry/pom.xml Service-Registry/
COPY Service-Registry/src Service-Registry/src
RUN mvn -f Service-Registry/pom.xml clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/Service-Registry/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
