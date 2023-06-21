FROM gradle:8 AS builder

WORKDIR /srv

COPY . .

RUN ["apt","install","git", "-y"]

RUN ["gradle", "--no-daemon", "shadowjar", "-PreposiliteRepositoryReleasesUsername=git", "-PreposiliteRepositoryReleasesPassword=5ox9JiyZRJbLkWfn+WwLdmDtFs43pYj5iHGHN8RC3YIEwpXulPwS7r2GPrReHY/P"]

RUN ["cp", "./build/libs/kanei-all.jar", "/srv/kanei-all.jar"]

FROM eclipse-temurin:20-jdk

WORKDIR /srv

COPY --from=builder /srv/kanei-all.jar /srv/


CMD ["java", "-XX:+UnlockExperimentalVMOptions","-XX:+OptimizeStringConcat","-XX:+UseZGC","-XX:+UseCompressedOops","-XX:+UseStringDeduplication","-Xms10M", "-Xmx500M", "-jar", "kanei-all.jar"]