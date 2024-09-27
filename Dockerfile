FROM gradle:8.8-jdk-21-and-22 AS builder
WORKDIR /srv

COPY . .

RUN ["apt","install","git", "-y"]

RUN ["gradle", "--no-daemon", "shadowjar"]

RUN ["cp", "./build/libs/kanei-all.jar", "/srv/kanei-all.jar"]

FROM eclipse-temurin:20-jdk

WORKDIR /srv

COPY --from=builder /srv/kanei-all.jar /srv/


CMD ["java", "-XX:+UnlockExperimentalVMOptions","-XX:+OptimizeStringConcat","-XX:+UseZGC","-XX:+UseCompressedOops","-XX:+UseStringDeduplication","-Xms10M", "-Xmx1G", "-jar", "kanei-all.jar"]
