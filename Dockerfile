FROM openjdk:11-jdk-slim-stretch
LABEL email="tioman@gally.de"
CMD ["mkdir","app"]
COPY target/movit-0.0.1.jar /app/movit-0.0.1.jar
WORKDIR /app
CMD ["java","-jar","movit-0.0.1.jar"]
