# 1. Basis-Image mit Java 17 (schlanke Version)
FROM amazoncorretto:17-alpine-jdk

# 2. Arbeitsverzeichnis im Container erstellen
WORKDIR /app

# 3. Alle notwendigen Dateien in den Container kopieren
COPY *.java ./
COPY index.html ./
COPY logo.png ./

# 4. Den Microservice im Container kompilieren
RUN javac *.java

# 5. Port 8089 nach außen hin öffnen
EXPOSE 8089

# 6. Befehl zum Starten der Anwendung
CMD ["java", "Main"]