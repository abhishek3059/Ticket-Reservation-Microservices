FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Build & install CommonDTO
COPY CommonDTO/pom.xml CommonDTO/
COPY CommonDTO/src CommonDTO/src
RUN mvn -f CommonDTO/pom.xml clean install -DskipTests -B

# Build Booking-Service
COPY Booking-Service/pom.xml Booking-Service/
COPY Booking-Service/src Booking-Service/src
RUN mvn -f Booking-Service/pom.xml clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/Booking-Service/target/*.jar app.jar
EXPOSE 8069
ENTRYPOINT ["java", "-jar", "app.jar"]
