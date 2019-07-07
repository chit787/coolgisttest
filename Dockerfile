FROM maven:3.6.0-jdk-8-slim
COPY src /home/app/src
COPY pom.xml /home/app
ENTRYPOINT ["mvn", "-f", "/home/app/pom.xml", "clean", "test"]