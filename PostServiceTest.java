public class PostServiceTest {
    public static void main(String[] args) {
        System.out.println("Starte Unit-Tests für PostService...");
        
        PostService service = new PostServiceImplements();
        
        // Test 1: Erstellen eines Posts
        Post p = service.create("Test-Beitrag", null, null, null);
        if (p != null && p.getCaption().equals("Test-Beitrag")) {
            System.out.println("✅ Test Erstellen: ERFOLGREICH");
        } else {
            System.out.println("❌ Test Erstellen: FEHLGESCHLAGEN");
            System.exit(1);
        }

        // Test 2: Like-Funktion
        int likesVorher = p.getLikes();
        service.addLike(p.getId());
        if (p.getLikes() == likesVorher + 1) {
            System.out.println("✅ Test Like: ERFOLGREICH");
        } else {
            System.out.println("❌ Test Like: FEHLGESCHLAGEN");
            System.exit(1);
        }

        System.out.println("Alle Tests erfolgreich abgeschlossen!");
    }
}