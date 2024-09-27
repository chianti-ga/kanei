FROM gradle:8.4.0-jdk20 AS builder
ARG REP_USR=foo
ARG REP_PASS=bar
WORKDIR /srv

COPY . .

RUN ["apt","install","git", "-y"]

RUN ["gradle", "--no-daemon", "shadowjar"]

RUN ["cp", "./build/libs/kanei-all.jar", "/srv/kanei-all.jar"]

FROM eclipse-temurin:20-jdk

WORKDIR /srv

COPY --from=builder /srv/kanei-all.jar /srv/


CMD ["java", "-XX:+UnlockExperimentalVMOptions","-XX:+OptimizeStringConcat","-XX:+UseZGC","-XX:+UseCompressedOops","-XX:+UseStringDeduplication","-Xms10M", "-Xmx500M", "-jar", "kanei-all.jar"]
