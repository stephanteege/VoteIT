# Amazon Corretto für Stabilität (Mac M-Chip kompatibel)
FROM amazoncorretto:17-alpine-jdk

# Arbeitsverzeichnis
WORKDIR /app

# Kopiere den Quellcode
COPY *.java ./
COPY index.html ./
# Wichtig: Falls du das Logo oder andere Bilder im Root hast, hier mit kopieren
COPY *.png ./ 2>/dev/null || true

# Kompilieren der Java-Dateien
RUN javac *.java

# Port für den Webserver freigeben
EXPOSE 8089

# Startbefehl
CMD ["java", "Main"]