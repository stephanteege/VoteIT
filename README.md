# VoteIt - Multimedia Microservice (Docker & CI/CD)

Dieses Projekt ist eine Java-basierte Microservice-Anwendung zur Verwaltung von Text-, Bild- und Video-Beiträgen. Es demonstriert die Umsetzung einer modernen DevOps-Infrastruktur inklusive Containerisierung und automatisierter Qualitätssicherung gemäß den Anforderungen von Opgave 2 und Opgave 3.

---

## Technologie-Stack
* **Backend:** Java 17 (Amazon Corretto / OpenJDK)
* **Frontend:** HTML5, CSS3 (Bootstrap 5), Vanilla JavaScript (Fetch API)
* **Datenhaltung:** CSV-Persistenz (`posts_data.csv`)
* **Infrastruktur:** Docker (Containerisierung)
* **CI/CD:** GitHub Actions

---

## Start-Möglichkeiten

### Option 1: Start mit Docker
Dies ist der effizienteste Weg, da die gesamte Ausführungsumgebung im Image gekapselt ist.

* **Image lokal bauen:**
  `docker build -t voteit-app .`
* **Container starten:**
  `docker run -p 8089:8089 voteit-app`
* **App aufrufen:** Navigiere im Browser zu `http://localhost:8089`.

### Option 2: Start aus dem Quellcode (Lokal)
Voraussetzung: Java JDK 17 ist lokal installiert.

* **Kompilieren:** `javac *.java`
* **Starten:** `java Main`

### Option 3: Nutzung des CD-Artefakts (ZIP)
Für Nutzer ohne installierte Java-Compiler oder Docker. Das fertige Programm kann direkt von GitHub bezogen werden:

1. Klicke oben im Repository auf den Reiter **Actions**.
2. Wähle den obersten (neuesten) Workflow-Lauf in der Liste aus (erkennbar am grünen Haken).
3. Scrolle nach unten zum Bereich **Artifacts**.
4. Klicke auf `VoteIT-Lauffaehiges-Programm`, um die Zip-Datei herunterzuladen.
5. Entpacke die Datei und starte die Anwendung im Terminal mit: `java Main`.

---

## CI/CD & Qualitätssicherung
Die Pipeline (`.github/workflows/pipeline.yml`) automatisiert den gesamten Software-Lebenszyklus bei jedem Push:

1. **Build-Check:** Automatisierte Kompilierung zur Syntax-Prüfung bei jedem Push auf den Main-Branch.
2. **Unit-Tests:** Die Klasse `PostServiceTest.java` verifiziert Kernfunktionen (z. B. Like-System, Post-Erstellung).
3. **Continuous Delivery (Artefakt):** Bereitstellung eines ZIP-Pakets mit allen `.class`-Dateien für den schnellen Einsatz ohne Neukompilierung.
4. **Containerisierung (Opgave 3):** Automatisierter Build eines Docker-Images als moderne, portable Ausführungsumgebung (Infrastructure-as-Code).

---

## Projektstruktur
* **`Dockerfile`**: Definition der Ausführungsumgebung (Aufgabe 1, Opgave 3).
* **`Main.java`**: HTTP-Server und Definition der REST-Endpunkte.
* **`PostService.java` & `PostServiceImplements.java`**: Saubere Trennung von Interface und Geschäftslogik.
* **`Post.java`**: Datenmodell für Beiträge.
* **`PostServiceTest.java`**: Testfälle für die automatisierte Qualitätssicherung.
* **`index.html`**: Responsive Web-Oberfläche der Anwendung.
* **`.gitignore`**: Schließt Kompilate (`.class`), lokale Datenbanken (`.csv`) und Bilder von der Versionsverwaltung aus.

---

## REST-Schnittstellen (API)
| Endpunkt | Methode | Beschreibung |
| :--- | :--- | :--- |
| `/main` | GET | Gibt alle Beiträge als JSON zurück. |
| `/main` | POST | Erstellt einen neuen Multimedia-Beitrag. |
| `/like?id=X` | POST | Inkrementiert den Like-Zähler für Beitrag X. |
| `/update?id=X` | POST | Aktualisiert die Bildunterschrift von Beitrag X. |
| `/delete?id=X` | POST | Löscht den Beitrag X permanent. |