FROM eclipse-temurin:17-jdk AS builder
WORKDIR /build
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
USER app
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
