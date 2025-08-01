FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/calculator-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application-docker.yml application-docker.yml
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.additional-location=application-docker.yml"]
