FROM openjdk:8u111-jdk
MAINTAINER Roberto Perez<roberto@perezalcolea.info>

VOLUME /tmp

RUN groupadd -r user && useradd -r -g user user

COPY github-api.jar /app/github-api.jar
RUN bash -c 'touch /app/github-api.jar'
RUN chown -R user:user /app

USER user
WORKDIR /app

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/github-api.jar"]