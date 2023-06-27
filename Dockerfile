FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=/target/ewallet.jar
COPY ./target/ewallet.jar ewallet.jar
ENTRYPOINT ["java", "-Duser.timezone=UTC -Duser.language=en -Duser.country=US", "-jar","/ewallet.jar"]