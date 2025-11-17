# Build stage
# Gradle 8.5 버전과 JDK 17을 사용하는 빌드 환경을 설정합니다.
FROM gradle:8.5-jdk17 AS build
# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# Gradle 관련 파일들을 복사합니다.
# 빌드 스크립트, 설정 파일, Gradle Wrapper를 복사하여 빌드 환경을 준비합니다.
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# 의존성을 미리 다운로드합니다. (소스 코드 변경 시 캐시 활용 목적)
# '--no-daemon' 옵션은 CI 환경에서 권장됩니다.
# '|| true'는 의존성 다운로드 실패 시 빌드를 계속 진행하도록 합니다. (선택적)
RUN gradle dependencies --no-daemon || true

# 소스 코드를 복사합니다.
COPY src src

# 애플리케이션을 빌드합니다. (테스트는 제외)
# '--no-daemon' 옵션은 CI 환경에서 권장됩니다.
# '-x test'는 빌드 시간을 단축하기 위해 테스트를 건너뜁니다.
RUN gradle bootJar --no-daemon -x test

# Runtime stage
# Eclipse Temurin JRE 17 이미지를 기반으로 런타임 환경을 설정합니다.
FROM eclipse-temurin:17-jre
# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 보안 강화를 위해 non-root 사용자 생성 및 사용
# 'spring' 그룹과 사용자를 생성하고, 해당 사용자로 컨테이너를 실행합니다.
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# 빌드 스테이지에서 생성된 JAR 파일을 복사합니다.
# '--from=build' 옵션은 이전 빌드 스테이지를 참조합니다.
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션이 사용할 포트를 노출합니다. (기본 8080)
EXPOSE 8080

# JVM 옵션 설정 (메모리 제한 등)
# '-XX:+UseContainerSupport' 옵션은 컨테이너 환경에서 메모리 제한을 잘 인식하도록 돕습니다.
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport"

# <<<--- ENV SPRING_PROFILES_ACTIVE=dev 라인을 삭제했습니다. --->>>
# 프로필은 컨테이너 실행 시 외부에서 주입하는 것이 좋습니다.
# 예: docker run -e SPRING_PROFILES_ACTIVE=local ...
# 예: ECS Task Definition 환경 변수 설정

# 애플리케이션 실행 명령어
# 'sh -c'를 사용하여 환경 변수($JAVA_OPTS)를 적용합니다.
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
