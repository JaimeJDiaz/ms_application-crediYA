# Dockerfile para ms-application (Spring Boot, JDK 23)

# Etapa 1: Build
FROM eclipse-temurin:23-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

# Etapa 2: Imagen final
FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
# (Opcional) Copiar el application.yaml si no está embebido en el .jar
# COPY applications/app-service/src/main/resources/application.yaml ./application.yaml
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

