# 1단계: Gradle과 JDK 17이 포함된 이미지에서 애플리케이션 빌드
FROM gradle:7.6-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

# 2단계: 빌드된 JAR 파일만 경량 OpenJDK 이미지에 복사하여 실행 환경 구성
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# 컨테이너 시작 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]