FROM openjdk:23-jdk-slim

WORKDIR /app

# Copiar el JAR ya construido
COPY applications/app-service/build/libs/*.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
