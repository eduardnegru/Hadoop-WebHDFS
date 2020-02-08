FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=./out/artifacts/hadoop_jar/hadoop.jar
COPY ${JAR_FILE} hadoop.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/hadoop.jar"]
