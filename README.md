# VoteIt - Multimedia Microservice

Dieses Projekt ist eine Java-basierte Microservice-Anwendung zur Verwaltung von Text-, Bild- und Video-Beiträgen. Es wurde im Rahmen der **Opgave 2** entwickelt, um die Prinzipien von REST-Schnittstellen, CRUD-Operationen und CI/CD-Infrastrukturen zu demonstrieren.

---

## 🛠️ Technologie-Stack
* **Backend:** Java JDK 17 (com.sun.net.httpserver)
* **Frontend:** HTML5, Bootstrap 5, Vanilla JavaScript (Fetch API)
* **Datenhaltung:** Dateibasierte Persistenz via CSV (`posts_data.csv`)
* **DevOps:** GitHub Actions für CI (Continuous Integration) und CD (Continuous Delivery)

---

## 🏃 Schnellstart-Anleitung

### Option A: Aus dem Quellcode starten (Lokal)
1. **Kompilieren:**

   javac *.java

2. **Server starten:**

   java Main

3. **App aufrufen:**
Öffne `http://localhost:8089` in deinem Browser.

### Option B: Nutzung des CD-Artefakts (Dritte)

1. Lade die `VoteIT-Lauffaehiges-Programm.zip` aus den GitHub Actions herunter.
2. Entpacke die Datei und öffne ein Terminal im Ordner.
3. Starte direkt mit: `java Main`

---

## ⚙️ CI/CD & Qualitätssicherung

In diesem Projekt ist ein automatisierter Workflow implementiert, der unter `.github/workflows/pipeline.yml` eingesehen werden kann:

1. **Build-Check:** Bei jedem Push wird geprüft, ob der Code fehlerfrei kompiliert.
2. **Automatisierte Tests:** Die Datei `PostServiceTest.java` führt fachliche Prüfungen (z. B. Like-Funktion) durch.
3. **Artifact Deployment:** Nur wenn alle Tests grün sind, erstellt die Pipeline ein fertiges ZIP-Paket (Continuous Delivery).

---

## 📁 Projektstruktur & Dateien

* **`Main.java`**: Der HTTP-Server. Er verwaltet die REST-Endpunkte (`/main`, `/like`, `/update`, `/delete`).
* **`PostService.java` & `PostServiceImplements.java**`: Trennung von Interface und Logik (Service-Layer).
* **`Post.java`**: Das Datenmodell für die Beiträge.
* **`PostServiceTest.java`**: Die Testklasse für die automatische Qualitätssicherung.
* **`index.html`**: Das Frontend, das via JavaScript mit der API kommuniziert.
* **`.gitignore`**: Ver
