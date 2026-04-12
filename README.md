# VoteIT

VoteIT ist eine einfache Web-App zum Teilen von Fotos und Videos innerhalb einer Gruppe. Nutzer können Beiträge erstellen, liken und wieder löschen. Das Projekt wurde im Rahmen des DevOps-Kurses als Microservice-Anwendung umgesetzt.

## Wie das Ganze aufgebaut ist

Die App besteht aus zwei Services die getrennt laufen:

- **User-Service (Port 8090)** – kümmert sich nur ums Login und setzt ein Cookie wenn die Credentials stimmen
- **VoteIT-Service (Port 8089)** – der eigentliche Hauptservice, macht alles mit den Posts (anzeigen, erstellen, liken, löschen)

Die zwei Services reden nicht direkt miteinander, der VoteIT-Service liest einfach das Cookie das der User-Service gesetzt hat.

### Komponentendiagramm

```mermaid
graph TD
    Browser["Browser (Client)"]

    subgraph Docker Compose
        US["User-Service\n:8090\n(UserService.java)"]
        VS["VoteIT-Service\n:8089\n(Main.java)"]
    end

    FS[("CSV + Filesystem\n(posts_data.csv, images/)")]

    Browser -- "POST /login\nGET /logout" --> US
    Browser -- "GET/POST /main\nPOST /like, /update, /delete" --> VS
    VS -- "Session-Cookie validieren" --> US
    VS -- "lesen / schreiben" --> FS
```

### Klassendiagramm (VoteIT-Service)

Der VoteIT-Service ist in Schichten aufgeteilt: `Main.java` übernimmt das HTTP-Routing, `PostService` definiert die Schnittstelle zur Geschäftslogik und `PostServiceImplements` setzt sie um. `Post` ist das Datenmodell.

```mermaid
classDiagram
    class Post {
        -int id
        -String caption
        -LocalDate date
        -String imagePath
        -String author
        -Set~String~ likedBy
        +getId() int
        +getCaption() String
        +getDate() LocalDate
        +getImagePath() String
        +getAuthor() String
        +getLikedBy() Set~String~
        +getLikes() int
    }

    class PostService {
        <<interface>>
        +get(int id) Post
        +list() List~Post~
        +delete(int id) Post
        +addLike(int id, String userName) Post
        +create(String, LocalDate, InputStream, String, String) Post
        +updateCaption(int id, String newCaption) Post
    }

    class PostServiceImplements {
        -List~Post~ posts
        -String CSV_FILE
        +get(int id) Post
        +list() List~Post~
        +delete(int id) Post
        +addLike(int id, String userName) Post
        +create(String, LocalDate, InputStream, String, String) Post
        +updateCaption(int id, String newCaption) Post
        -loadFromCSV() void
        -saveToCSV() void
    }

    class Main {
        +main(String[] args) void
    }

    PostService <|.. PostServiceImplements : implements
    PostServiceImplements *-- Post
    Main --> PostService
```

### Sequenzdiagramm – Login und Post erstellen

```mermaid
sequenceDiagram
    actor User as Nutzer
    participant B as Browser
    participant US as User-Service :8090
    participant VS as VoteIT-Service :8089

    User->>B: Login (E-Mail + Passwort)
    B->>US: POST /login
    US-->>B: 200 OK + Set-Cookie: session=...

    User->>B: Feed aufrufen
    B->>VS: GET /main (Cookie)
    VS->>US: Cookie validieren
    US-->>VS: Nutzername
    VS-->>B: JSON-Array (alle Posts)

    User->>B: Beitrag erstellen
    B->>VS: POST /main (multipart: Caption + Bild)
    VS-->>B: 201 Created (neuer Post als JSON)

    User->>B: Beitrag liken
    B->>VS: POST /like?id=3 (Cookie)
    VS-->>B: 200 OK (aktualisierter Post)
```

## Technologie

- Java 17 ohne externe Frameworks (nur `com.sun.net.httpserver`)
- HTML/CSS/JavaScript im Frontend
- Daten werden in einer CSV-Datei gespeichert, Bilder/Videos im lokalen Ordner
- Docker + Docker Compose für die Container
- GitHub Actions für CI/CD

## API-Endpunkte

| Endpunkt | Methode | Beschreibung |
|---|---|---|
| `/main` | GET | Alle Posts als JSON |
| `/main` | POST | Neuen Post anlegen (multipart mit Bild/Video) |
| `/like?id=X` | POST | Like togglen (einmal = like, nochmal = unlike) |
| `/update?id=X` | POST | Beschreibung ändern |
| `/delete?id=X` | POST | Post löschen |

## Starten

### Mit Docker Compose (Lokale Installation von Docker vorausgesetzt)

```bash
docker-compose up --build
```

Danach läuft die App unter `http://localhost:8089`.

### Manuell mit Java

Zwei Terminals öffnen:

**Terminal 1:**
```bash
cd user-service
javac UserService.java
java UserService
```

**Terminal 2:**
```bash
cd voteit-service
javac *.java
java Main
```

## CI/CD Pipeline

Bei jedem Push auf `main` läuft automatisch:
1. Kompilieren beider Services
2. Unit-Tests für User-Service und VoteIT-Service
3. Docker Images bauen und in die GitHub Container Registry pushen

## Test-Zugänge

| Name | E-Mail | Passwort |
|---|---|---|
| Caro | finkca.vi23@stud.gera.dhge.de | password123 |
| Stephan | teegst.vi23@stud.gera.dhge.de | password456 |
| Joanna | gramjo.vi23@stud.gera.dhge.de | password123 |
| Irene | haerir.vi23@stud.gera.dhge.de | password456 |
