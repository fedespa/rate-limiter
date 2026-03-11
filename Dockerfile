FROM eclipse-temurin:21-jdk-alpine
ARG JAR_FILE=target/rate-limiter-0.0.1.jar
COPY ${JAR_FILE} app_ratelimiter.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app_ratelimiter.jar"]