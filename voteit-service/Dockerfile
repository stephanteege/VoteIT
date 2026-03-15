# Nutzt das schlanke Amazon Corretto Image (Java 17)
FROM amazoncorretto:17-alpine-jdk

# Arbeitsverzeichnis im Container
WORKDIR /app

# Kopiere alle Java-Dateien und Frontend-Ressourcen
# (Main.java, Post.java, PostService.java, PostServiceImplements.java, 
# index.html, member_area.html, style.css, logo.png)
COPY . .

# Erstelle den Ordner für die Bilder-Uploads, falls er im Image noch fehlt
RUN mkdir -p images

# Kompiliere alle Java-Klassen
RUN javac *.java

# Der Haupt-Service läuft auf Port 8089
EXPOSE 8089

# Startbefehl
CMD ["java", "Main"]