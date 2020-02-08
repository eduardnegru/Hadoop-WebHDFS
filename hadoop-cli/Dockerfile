FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY out/artifacts/hadoop_cli_jar/hadoop-cli.jar  cli.jar
ENTRYPOINT ["java","-jar","cli.jar"]
