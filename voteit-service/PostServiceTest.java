import java.time.LocalDate;
import java.util.List;

public class PostServiceTest {
    public static void main(String[] args) {
        System.out.println("=== Starte automatisierte Unit-Tests für VoteIt ===");
        
        PostService service = new PostServiceImplements();
        
        // Test 1: Erstellen eines Beitrags
        // Wir übergeben null für Stream und ContentType, da dies ein Text-Test ist
        Post p = service.create("CI-Test Nachricht", LocalDate.now(), null, null, "Test-User");        
        if (p != null && "CI-Test Nachricht".equals(p.getCaption())) {
            System.out.println("✅ Test Erstellen: ERFOLGREICH");
        } else {
            System.out.println("❌ Test Erstellen: FEHLGESCHLAGEN");
            System.exit(1);
        }

        // Test 2: Like-Funktion (Zustandsprüfung)
        int likesVorher = p.getLikes();
        service.addLike(p.getId());
        if (p.getLikes() == likesVorher + 1) {
            System.out.println("✅ Test Like-System: ERFOLGREICH");
        } else {
            System.out.println("❌ Test Like-System: FEHLGESCHLAGEN");
            System.exit(1);
        }

        // Test 3: Lösch-Funktion
        int idZumLoeschen = p.getId();
        service.delete(idZumLoeschen);
        Post geloeschterPost = service.get(idZumLoeschen);
        if (geloeschterPost == null) {
            System.out.println("✅ Test Löschen: ERFOLGREICH");
        } else {
            System.out.println("❌ Test Löschen: FEHLGESCHLAGEN");
            System.exit(1);
        }

        // Test 4: Listen-Funktion (Integrität)
        List<Post> allePosts = service.list();
        if (allePosts != null) {
            System.out.println("✅ Test Daten-Liste: ERFOLGREICH (" + allePosts.size() + " Beiträge gefunden)");
        } else {
            System.out.println("❌ Test Daten-Liste: FEHLGESCHLAGEN");
            System.exit(1);
        }

        System.out.println("=== Alle Tests bestanden! Pipeline kann fortfahren. ===");
    }
}