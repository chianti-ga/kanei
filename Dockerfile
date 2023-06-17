FROM eclipse-temurin:17.0.4_8-jre

WORKDIR /srv/kanei

COPY . .

CMD ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseG1GC", "-XX:+UseStringDeduplication", "-Xms10M", "-Xmx500M", "-jar", "JMusicBot.jar"]