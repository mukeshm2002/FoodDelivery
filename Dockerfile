# Stage 1: Build stage using Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run stage (openjdk-க்கு பதிலாக eclipse-temurin பயன்படுத்தப்பட்டுள்ளது)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Stage 1-ல இருந்து பில்ட் ஆன .jar ஃபைலை காப்பி பண்றோம்
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]