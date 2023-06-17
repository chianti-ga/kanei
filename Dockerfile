FROM gradle:7-jdk-jammy AS builder

WORKDIR /srv

COPY . .

RUN ["gradle", "--no-daemon", "shadowjar"]

RUN ["cp", "./build/libs/kanei-all.jar", "/srv/kanei-all.jar"]

FROM eclipse-temurin:17.0.7_7-jre-jammy

WORKDIR /srv

COPY --from=builder /srv/kanei-all.jar /srv/

VOLUME /data
WORKDIR /data

CMD ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseG1GC", "-XX:+UseStringDeduplication", "-Xms10M", "-Xmx500M", "-jar", "kanei-all.jar"]