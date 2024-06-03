FROM openjdk:17 as builder
COPY . .

RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=builder target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]