FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY pom.xml .
RUN ./mvnw dependency:go-offline || true

COPY src ./src

RUN ./mvnw clean package -DskipTests

ENV JAVA_OPTS=""

ENV PORT 8080

EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar target/EasyBuy-0.0.1-SNAPSHOT.jar"]