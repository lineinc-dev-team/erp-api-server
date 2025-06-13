# 1단계: 빌드
FROM gradle:7.6-jdk17 as builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

# 2단계: 실행
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]