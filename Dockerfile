FROM maven:3.9-eclipse-temurin-21

RUN apt-get update && apt-get install -y --no-install-recommends git \
    && rm -rf /var/lib/apt/lists/*

RUN useradd -m -s /bin/bash amnotbot
RUN mkdir -p /home/amnotbot/.amnotbot

COPY src/main/resources/*.config /home/amnotbot/.amnotbot/
ADD . /home/amnotbot/app

RUN chown -R amnotbot:amnotbot /home/amnotbot

USER amnotbot
ENV HOME /home/amnotbot

WORKDIR /home/amnotbot/app

RUN mvn -e install -DskipTests

CMD ["java","-jar","target/amnotbot-core-0.0.1-SNAPSHOT.jar"]
