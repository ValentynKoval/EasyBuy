# Multi-stage build
FROM maven:3.9.3-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/EasyBuy-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS=""
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]