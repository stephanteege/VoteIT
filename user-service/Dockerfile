# Nutzt das schlanke Amazon Corretto Image (Java 17)
FROM amazoncorretto:17-alpine-jdk

# Arbeitsverzeichnis im Container
WORKDIR /app

# Kopiere nur die Java-Datei des User-Services
COPY UserService.java .

# Kompiliere den Service
RUN javac UserService.java

# Der User-Service läuft auf Port 8090
EXPOSE 8090

# Startbefehl
CMD ["java", "UserService"]