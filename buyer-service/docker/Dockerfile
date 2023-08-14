FROM openjdk:18-alpine
ARG JAR_FILE=buyer-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]