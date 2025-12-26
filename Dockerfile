FROM eclipse-temurin:17-jre

RUN apt-get update && \
    apt-get install -y --no-install-recommends postgresql-client tzdata curl && \
    rm -rf /var/lib/apt/lists/* && \
    ln -snf /usr/share/zoneinfo/UTC /etc/localtime

WORKDIR /app

COPY /build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "/app/app.jar"]