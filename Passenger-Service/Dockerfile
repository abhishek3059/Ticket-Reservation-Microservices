FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Build & install CommonDTO
COPY CommonDTO/pom.xml CommonDTO/
COPY CommonDTO/src CommonDTO/src
RUN mvn -f CommonDTO/pom.xml clean install -DskipTests -B

# Build Passenger-Service
COPY Passenger-Service/pom.xml Passenger-Service/
COPY Passenger-Service/src Passenger-Service/src
RUN mvn -f Passenger-Service/pom.xml clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/Passenger-Service/target/*.jar app.jar
EXPOSE 8070
ENTRYPOINT ["java", "-jar", "app.jar"]
