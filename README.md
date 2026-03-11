# VoteIt - Multimedia Microservice (Docker & CI/CD)

Dieses Projekt ist eine Java-basierte Microservice-Anwendung zur Verwaltung von Text-, Bild- und Video-Beiträgen. Es demonstriert die Umsetzung einer modernen DevOps-Infrastruktur inklusive Containerisierung und automatisierter Qualitätssicherung gemäß den Anforderungen von Opgave 2 und Opgave 3.


## Technologie-Stack
* **Backend:** Java 17 (Amazon Corretto / OpenJDK)
* **Frontend:** HTML5, CSS3 (Bootstrap 5), Vanilla JavaScript (Fetch API)
* **Datenhaltung:** CSV-Persistenz (`posts_data.csv`)
* **Infrastruktur:** Docker (Containerisierung)
* **CI/CD:** GitHub Actions


## Start-Möglichkeiten

### Option 1: Start mit Docker (Empfohlen)
Dies ist der effizienteste Weg, da die gesamte Ausführungsumgebung im Image gekapselt ist.

* **Image lokal bauen:**
  `docker build -t voteit-app .`
* **Container mit Persistenz starten (Live-Sync):**
  Um sicherzustellen, dass hochgeladene Medien und CSV-Änderungen direkt auf dem Host-System gespeichert werden, wird ein Volume-Mount empfohlen:
  `docker run -p 8089:8089 -v "$(pwd)/posts_data.csv:/app/posts_data.csv" -v "$(pwd)/images:/app/images" voteit-app`
  
  Die Anwendung speichert hochgeladene Dateien physisch im Verzeichnis `/app/images` innerhalb des Containers. Durch die Verwendung von Docker-Volumes (siehe Start-Möglichkeiten) wird eine zustandsbehaftete Datenhaltung (Persistence) über Lebenszyklen des Containers hinweg sichergestellt.

* **App aufrufen:** Navigiere im Browser zu `http://localhost:8089`.

### Option 2: Start aus dem Quellcode (Lokal)
Voraussetzung: Java JDK 17 ist lokal installiert.

* **Kompilieren:** `javac *.java`
* **Starten:** `java Main`

### Option 3: Nutzung des CD-Artefakts (ZIP)
1. Klicke oben im Repository auf den Reiter **Actions**.
2. Wähle den neuesten Workflow-Lauf (grüner Haken) aus.
3. Scrolle nach unten zu **Artifacts** und lade `VoteIT-Lauffaehiges-Programm` herunter.
4. Entpacke die Datei und starte mit: `java Main`.


## CI/CD & Qualitätssicherung
Die Pipeline (`.github/workflows/pipeline.yml`) automatisiert den gesamten Software-Lebenszyklus bei jedem Push:

1. **Build-Check:** Automatisierte Kompilierung zur Syntax-Prüfung.
2. **Unit-Tests:** Die Klasse `PostServiceTest.java` verifiziert Kernfunktionen (z. B. Like-System, Post-Erstellung, Lösch-Logik).
3. **Continuous Delivery:** Bereitstellung eines lauffähigen ZIP-Pakets.
4. **Containerisierung (Opgave 3):** Automatisierter Build eines Docker-Images als portable Ausführungsumgebung (Infrastructure-as-Code).


## Projektstruktur & Architektur
Die Anwendung folgt einer **Layered Architecture** (Schichtentrennung), um Wartbarkeit und Testbarkeit zu gewährleisten:

* **API-Layer (`Main.java`)**: Verwaltet die REST-Endpunkte und das Routing.
* **Service-Layer (`PostService.java` / `PostServiceImplements.java`)**: Enthält die Geschäftslogik. Die Entkopplung durch ein Interface ermöglicht einfaches Mocking in Tests.
* **Domain-Layer (`Post.java`)**: Definiert das Datenmodell.
* **Persistence-Layer**: Dateibasierte Speicherung via CSV für leichtgewichtige Portabilität.
* **Infrastructure (`Dockerfile`)**: Definiert die isolierte Laufzeitumgebung.


## REST-Schnittstellen (API)
Der Service unterstützt die Verarbeitung von Multimedia-Daten via `multipart/form-data`.

| Endpunkt | Methode | Beschreibung |
| :--- | :--- | :--- |
| `/main` | GET | Gibt alle Beiträge als JSON zurück. |
| `/main` | POST | Erstellt einen neuen Multimedia-Beitrag (Text + Bild/Video). |
| `/like?id=X` | POST | Inkrementiert Likes für Beitrag X (ID via Query-Parameter). |
| `/update?id=X` | POST | Aktualisiert die Beschreibung von Beitrag X. |
| `/delete?id=X` | POST | Löscht den Beitrag X permanent aus der CSV und dem Filesystem. |