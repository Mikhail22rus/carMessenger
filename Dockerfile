FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
COPY target/CarMessenger-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080



ENTRYPOINT ["java", "-jar", "app.jar"]