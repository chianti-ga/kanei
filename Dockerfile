FROM gradle:8 AS builder

WORKDIR /srv

COPY . .

RUN ["gradle", "--no-daemon", "shadowjar", "-PreposiliteRepositoryReleasesUsername=skitou", "-PreposiliteRepositoryReleasesPassword=s3r0bvRZRyqSo6wmG+UQwhVJRjsx5UIAVSFwH7q1ZnFEr2MZWhKJ7iIDpk6u1Fb2"]

RUN ["cp", "./build/libs/kanei-all.jar", "/srv/kanei-all.jar"]

FROM eclipse-temurin:17-jre

WORKDIR /srv

COPY --from=builder /srv/kanei-all.jar /srv/


CMD ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseParallelGC", "-XX:+UseStringDeduplication","-XX:+OptimizeStringConcat", "-XX:+UseCompressedOops", "-Xms10M", "-Xmx500M", "-jar", "kanei-all.jar"]