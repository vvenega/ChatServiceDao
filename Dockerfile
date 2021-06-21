FROM adoptopenjdk/openjdk8
WORKDIR /
ARG ChatServiceDao-0.0.1-SNAPSHOT.jar
ADD ChatServiceDao-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8007
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]