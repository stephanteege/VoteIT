# VoteIT - Bachelor in Garden

VoteIT ist eine webbasierte Plattform zum Teilen und Bewerten von multimedialen Beiträgen (Texte, Bilder, Videos). Die Anwendung wurde als Microservice-Architektur entworfen und stellt eine geschlossene Umgebung für autorisierte Nutzer bereit. Die Plattform dient als digitales Begleitsystem für Events und ermöglicht Interaktionen in Echtzeit.

## Architektur und Infrastruktur

Das System basiert auf einer Service-Orientierten Architektur (SOA) und teilt die Zuständigkeiten in zwei unabhängige, containerisierte Microservices auf:

1. **User-Service (Port 8090):** Fungiert als Identity Provider für die Authentifizierung und Session-Verwaltung (Cookie-basiert).
2. **VoteIT-Service (Port 8089):** Der primäre Applikationsserver für die Geschäftslogik (Feed, Uploads, Likes). Er ist zustandslos bezüglich der Nutzerdaten und validiert Anfragen über das Session-Cookie des User-Services.

## Projektstruktur (Layered Architecture)

Innerhalb des Haupt-Services (VoteIT) folgt die Codebasis einer strikten Schichtentrennung, um Wartbarkeit und Testbarkeit zu gewährleisten:

* **API-Layer (`Main.java` & Handler)**: Verwaltet die REST-Endpunkte, HTTP-Requests und das Routing.
* **Service-Layer (`PostService.java` / `PostServiceImplements.java`)**: Enthält die eigentliche Geschäftslogik. Die Entkopplung durch ein Interface ermöglicht einfaches Mocking in Unit-Tests.
* **Domain-Layer (`Post.java`)**: Definiert das interne Datenmodell der Anwendung.
* **Persistence-Layer**: Dateibasierte Speicherung via CSV (`posts_data.csv`) für leichtgewichtige Portabilität und lokales Dateisystem für Medien-Uploads.
* **Infrastructure (`Dockerfile` & `docker-compose.yml`)**: Definiert die isolierten Laufzeitumgebungen und die Orchestrierung.

## Technologie-Stack

* **Backend:** Java 17 (Vanilla HTTP-Server, ohne externe Frameworks)
* **Frontend:** HTML5, CSS3, Vanilla JavaScript (Fetch API)
* **Datenhaltung:** Dateibasiert (CSV und lokales Filesystem)
* **Infrastruktur:** Docker und Docker Compose 
* **CI/CD:** GitHub Actions (Automatisierte Kompilierung, Unit-Tests und Artefakt-Erstellung)

## REST-Schnittstellen (API)

Der Service unterstützt die Verarbeitung von Multimedia-Daten via `multipart/form-data`.

| Endpunkt | Methode | Beschreibung |
| :--- | :--- | :--- |
| `/main` | GET | Gibt alle Beiträge als JSON zurück. |
| `/main` | POST | Erstellt einen neuen Multimedia-Beitrag (Text + Bild/Video). |
| `/like?id=X` | POST | Inkrementiert Likes für Beitrag X (ID via Query-Parameter). |
| `/update?id=X` | POST | Aktualisiert die Beschreibung von Beitrag X. |
| `/delete?id=X` | POST | Löscht den Beitrag X permanent aus der CSV und dem Filesystem. |

## Lokales Setup und Ausführung

Die Anwendung kann auf drei Arten gestartet werden, je nach gewünschtem Automatisierungsgrad und ist anschließend erreichbar unter: `http://localhost:8089`

### Option 1: Start via Docker Compose (Empfohlen)
Die Nutzung von Docker Compose konfiguriert das Netzwerk-Routing sowie die persistenten Volumes automatisch. Voraussetzung ist eine lokale Installation von Docker.

1. Das Repository klonen und ein Terminal im Stammverzeichnis öffnen.
2. Build und Start der Container: `docker-compose up --build`

### Option 2: Manueller Start via Java (Ohne Docker)
Voraussetzung ist ein installiertes Java JDK 17. Da die Architektur aus zwei getrennten Services besteht, müssen diese in zwei separaten Terminals gestartet werden.

**Terminal 1: User-Service**
1. `cd user-service`
2. `javac UserService.java`
3. `java UserService`

**Terminal 2: VoteIT-Service**
1. Neues Terminal öffnen und `cd voteit-service`
2. `javac *.java`
3. `java Main`

### Option 3: Ausführung des CI/CD-Artefakts (Continuous Delivery)
Die GitHub Actions Pipeline erstellt bei jedem erfolgreichen Build automatisch ein lauffähiges Release-Paket (ZIP).
1. Das Artefakt (ZIP-Paket) aus dem "Actions"-Reiter in GitHub herunterladen und entpacken.
2. In den entpackten Ordnern verfahren wie unter **Option 2** beschrieben, um die Anwendung direkt und ohne vorheriges Kompilieren auf jedem Java-fähigen System zu starten.

## Autorisierte Test-Zugänge (Login)
Das System nutzt derzeit eine geschlossene Benutzergruppe.
* **Caro:** finkca.vi23@stud.gera.dhge.de (Passwort: password123)
* **Stephan:** teegst.vi23@stud.gera.dhge.de (Passwort: password456)
* **Joanna:** gramjo.vi23@stud.gera.dhge.de (Passwort: password123)
* **Irene:** haerir.vi23@stud.gera.dhge.de (Passwort: password456)

---

### Grundlagen & Applikation
* **Backend:** Implementierung eines robusten HTTP-Servers in purem Java (ohne Spring).
* **REST-API:** Bereitstellung von Endpunkten (`GET /main`, `POST /main`, `POST /like`, `POST /delete`) zur asynchronen Kommunikation mit dem Frontend (Vanilla JavaScript / Fetch API).
* **Multimedia-Support:** Verarbeitung von `multipart/form-data` für den Upload von Bildern und MP4-Videos.
* **Persistenz:** Dateibasierte Speicherung der Beitrags-Metadaten (`posts_data.csv`).

### Qualitätssicherung & CI/CD
* **GitHub Actions:** Eine vollständige CI/CD-Pipeline (`pipeline.yml`) ist integriert. Automatisierung des gesamten Software-Lebenszyklus bei jedem Push.
* **Automatisierte Tests:** Bei jedem Push in das Repository durchläuft der Code Unit-Tests (z. B. `PostServiceTest.java`), um die Kernlogik (Post-Erstellung, Likes, Löschen) abzusichern.
* **Continuous Delivery:** Fehlerfreier Code wird als herunterladbares Artefakt (ZIP-Paket) in der Pipeline zur Verfügung gestellt (siehe Option 3).

### Infrastruktur & Architektur (Microservices)
* **Service-Orientierte Architektur (SOA):** Die Monolith-Struktur wurde in zwei logisch getrennte Microservices (User-Management und Beitrags-Logik) entkoppelt.
* **Separation of Concerns:** Der VoteIT-Service delegiert die Autorisierung an den User-Service und nutzt Session-Cookies.
* **Containerisierung:** Jeder Service besitzt ein eigenes `Dockerfile`. Automatisierter Build eines Docker-Images als portable Ausführungsumgebung.
* **Orchestrierung (IaC):** Eine `docker-compose.yml` verknüpft die Microservices, mappt die Ports und sichert die Datenpersistenz über Docker-Volumes.