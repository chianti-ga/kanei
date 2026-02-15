FROM gradle:9.3.1-jdk-25-and-25 AS builder
WORKDIR /srv

COPY . .

RUN ["apt", "install", "git", "-y"]

RUN ["gradle", "--no-daemon", "shadowJar"]

RUN ["cp", "./build/libs/kanei-all.jar", "/srv/kanei-all.jar"]

FROM eclipse-temurin:25-jre

WORKDIR /srv

COPY --from=builder /srv/kanei-all.jar /srv/

CMD ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+OptimizeStringConcat", "-XX:+UseZGC", "-XX:+UseCompressedOops", "-XX:+UseStringDeduplication", "-Xms${INITIAL_HEAP_SIZE:-10M}", "-Xmx${MAX_HEAP_SIZE:-1G}", "-jar", "kanei-all.jar"]
