FROM alpine:latest

USER root

RUN mkdir plugins

RUN wget -P /plugins https://github.com/neo4j-contrib/neo4j-apoc-procedures/releases/download/4.4.0.6/apoc-4.4.0.6-all.jar

CMD ["/bin/sh", "-c", "ls -la /plugins"]