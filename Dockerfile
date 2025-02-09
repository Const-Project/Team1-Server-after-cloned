# Docker file

# jdk17 Image Start
From --platform=linux/arm64/v8 openjdk:17-jdk

# 인자 정리 - Jar
ARG JAR_FILE=build/libs/*.jar

# jar File Copy
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker","-jar","app.jar"]
