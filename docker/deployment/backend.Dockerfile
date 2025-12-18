# this file is used to build the final docker image used to deploy the project on Cloud Run
# 1- Build
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY ./backend .
RUN mvn clean package -DskipTests -Pprod
RUN ls -l infrastructure/target

# 2 - Run
FROM eclipse-temurin:21-jre
COPY --from=builder /app/infrastructure/target/myjobs-prod.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "/app.jar"]