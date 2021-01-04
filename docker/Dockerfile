FROM alpine/git as clone
WORKDIR /app
RUN git clone https://github.com/AndriiBraslavskyi/replication-client.git

FROM maven:3.6.0-jdk-8-slim AS build
WORKDIR /app
COPY --from=clone /app/replication-client /app
RUN mvn -Dmaven.test.skip=true clean package

FROM openjdk:8u252-jre-slim
WORKDIR /app
RUN mkdir conf
COPY --from=build /app/target/replication-client-0.0.1-SNAPSHOT.jar /app

EXPOSE 8080
EXPOSE 8070
EXPOSE 8060
EXPOSE 8050

ARG CLIENT_PORT
ARG CLIENT_DELAY

CMD ["java", "-jar", "replication-client-0.0.1-SNAPSHOT.jar", "--server.port=${CLIENT_PORT}", "--replication-client.persist-delay=${CLIENT_DELAY}", "--replication-client.save-on-cancel=${SAVE_ON_CANCEL}", "--replication-client.max-random-timeout=${MAX_RANDOM_TIMEOUT}"]