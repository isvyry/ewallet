FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} ewallet.jar
ENTRYPOINT ["java","-jar","/ewallet.jar"]