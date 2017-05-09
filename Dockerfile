FROM maven:alpine

EXPOSE 8080

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

ADD . /usr/src/app

RUN mvn -s /usr/share/maven/ref/settings-docker.xml install -DskipTests=true

CMD ["java", "-Xmx600m", "-jar", "/usr/src/app/target/swagger-1.0-SNAPSHOT.jar"]
