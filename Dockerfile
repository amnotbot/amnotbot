FROM maven:latest

RUN useradd -m -s /bin/bash amnotbot
RUN mkdir -p /home/amnotbot/.amnotbot

COPY src/main/resources/*.config /home/amnotbot/.amnotbot/
COPY src/main/resources/log4j.properties /home/amnotbot/.amnotbot/
ADD . /home/amnotbot/app

RUN chown -R amnotbot:amnotbot /home/amnotbot

USER amnotbot
ENV HOME /home/amnotbot

WORKDIR /home/amnotbot/app

RUN mvn -e install -DskipTests

CMD ["java","-jar","target/amnotbot-core-0.0.1-SNAPSHOT.jar"]
