FROM gradle:8.8-jdk21
WORKDIR /app

# 빌드 시점에 필요한 환경 변수를 설정 (Dockerfile의 ARG를 사용)
ARG REDIS_PASSWORD
ARG RESEND_API_KEY
ARG JJAVA_REFACTOR_API_KEY
ENV REDIS_PASSWORD=${REDIS_PASSWORD}
ENV RESEND_API_KEY=${RESEND_API_KEY}
ENV JJAVA_REFACTOR_API_KEY=${JJAVA_REFACTOR_API_KEY}

COPY . .
RUN chmod +x gradlew

# 테스트 + 문서 + jar 빌드
RUN ./gradlew clean test asciidoctor bootJar --no-daemon \
        -DREDIS_PASSWORD=${REDIS_PASSWORD} \
        -DRESEND_API_KEY=${RESEND_API_KEY} \
        -DJJAVA_REFACTOR_API_KEY=${JJAVA_REFACTOR_API_KEY} \
    && cp build/libs/*.jar build/libs/app.jar

EXPOSE 5000

# 실행 (local profile 적용)
ENTRYPOINT ["java","-jar","build/libs/app.jar","--spring.profiles.active=local"]