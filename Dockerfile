FROM maven:3.9.3-eclipse-temurin-21-alpine

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

ENV JAVA_OPTS=""
ENV PORT 8080

EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar target/EasyBuy-0.0.1-SNAPSHOT.jar"]
