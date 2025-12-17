# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos primero lo mínimo para aprovechar cache
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -q -DskipTests dependency:go-offline

# Copiamos el código
COPY src/ src/

# Build (por ahora sin tests)
RUN ./mvnw -q -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el jar generado (ajusta si tu artefacto cambia)
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
