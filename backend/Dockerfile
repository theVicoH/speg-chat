FROM maven:3.8.4-openjdk-17-slim AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src src

CMD ["mvn", "spring-boot:run"]