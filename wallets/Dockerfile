# BUILD
FROM maven:3.9.9-eclipse-temurin-17-noble AS build
WORKDIR /home/app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# RUN
FROM eclipse-temurin:17-jdk-noble
COPY --from=build /home/app/target/*.jar app.jar
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "app.jar"]