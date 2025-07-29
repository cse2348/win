# 1. Java 21이 설치된 공식 OpenJDK 이미지를 베이스로 사용
# -> 컨테이너에서 Java 애플리케이션을 실행할 수 있도록 해줌
FROM openjdk:17

# 2. build/libs 폴더 아래 있는 .jar 파일을 JAR_FILE이라는 변수로 정의
# -> Gradle 빌드 시 JAR이 이 경로에 생성됨
ARG JAR_FILE=build/libs/*.jar

# 3. 위에서 정의한 JAR 파일을 app.jar 라는 이름으로 컨테이너에 복사
# -> 이미지 내부에 jar 파일을 포함시켜야 실행 가능함
COPY ${JAR_FILE} app.jar

# 4. 컨테이너 실행 시 'java -jar /app.jar' 명령을 자동으로 실행하도록 지정
# -> ENTRYPOINT는 Docker 컨테이너 시작 시 기본으로 실행되는 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]