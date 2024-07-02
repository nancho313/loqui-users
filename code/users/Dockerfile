LABEL org.opencontainers.image.source="https://github.com/nancho313/loqui-users"
LABEL org.opencontainers.image.description="Java Microservice that handles all the business logic regarding users."
FROM openjdk:21-slim
COPY boot/target/users*.jar app.jar
CMD ["java", "-jar", "/app.jar"]