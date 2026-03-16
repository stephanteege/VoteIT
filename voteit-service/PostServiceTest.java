import java.time.LocalDate;
import java.util.List;

public class PostServiceTest {
    public static void main(String[] args) {
        System.out.println("=== Starte automatisierte Unit-Tests für VoteIt ===");
        
        PostService service = new PostServiceImplements();
        
        Post p = service.create("CI-Test Nachricht", LocalDate.now(), null, null, "TestUser");
        
        if (p != null && "CI-Test Nachricht".equals(p.getCaption())) {
            System.out.println("✅ Test Erstellen: ERFOLGREICH");
        } else {
            System.out.println("❌ Test Erstellen: FEHLGESCHLAGEN");
            System.exit(1);
        }

        // Test 2: Like & Unlike-Funktion (Toggle-Prüfung)
        int likesVorher = p.getLikes();
        
        // 1. Klick -> Like (+1)
        service.addLike(p.getId(), "TestUser");
        if (p.getLikes() == likesVorher + 1) {
            System.out.println("✅ Test Like-System (Hinzufügen): ERFOLGREICH");
        } else {
            System.out.println("❌ Test Like-System (Hinzufügen): FEHLGESCHLAGEN");
            System.exit(1);
        }

        // 2. Klick -> Unlike (-1)
        service.addLike(p.getId(), "TestUser"); 
        if (p.getLikes() == likesVorher) {
            System.out.println("✅ Test Like-System (Entfernen): ERFOLGREICH");
        } else {
            System.out.println("❌ Test Like-System (Entfernen): FEHLGESCHLAGEN");
            System.exit(1);
        }

        int idZumLoeschen = p.getId();
        service.delete(idZumLoeschen);
        if (service.get(idZumLoeschen) == null) {
            System.out.println("✅ Test Löschen: ERFOLGREICH");
        } else {
            System.out.println("❌ Test Löschen: FEHLGESCHLAGEN");
            System.exit(1);
        }

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