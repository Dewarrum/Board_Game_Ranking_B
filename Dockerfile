# 1단계: 빌드 환경 (자바 21 버전)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradle 래퍼와 설정 파일들을 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 윈도우에서 작성된 경우를 대비해 gradlew에 실행 권한 부여
RUN chmod +x ./gradlew

# 실제 소스 코드 복사
COPY src src

# 프로젝트 빌드 (테스트는 제외해서 속도 높임)
RUN ./gradlew clean build -x test

# 2단계: 실행 환경 (가벼운 자바 21 런타임만 포함)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 1단계에서 빌드된 .jar 파일만 가져오기
COPY --from=build /app/build/libs/*.jar app.jar

# Render에게 8080 포트를 쓴다고 알려줌 (스프링 부트 기본 포트)
EXPOSE 8080

# 스프링 부트 실행
ENTRYPOINT ["java", "-jar", "app.jar"]