FROM clojure:temurin-17-tools-deps-bullseye

RUN apt-get update && apt-get install -y \
  curl default-jre \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/jar/app.jar .

EXPOSE 8080

ENV BIFF_PROFILE=prod
ENV HOST=0.0.0.0
ENV PORT=8080

CMD ["/usr/bin/java", "-XX:-OmitStackTraceInFastThrow", "-XX:+CrashOnOutOfMemoryError", "-jar", "app.jar"]
