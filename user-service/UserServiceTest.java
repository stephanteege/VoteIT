import java.util.Map;

public class UserServiceTest {

    private static final Map<String, String[]> USERS = Map.of(
        "finkca.vi23@stud.gera.dhge.de", new String[]{"password123", "Caro"},
        "teegst.vi23@stud.gera.dhge.de", new String[]{"password456", "Stephan"},
        "gramjo.vi23@stud.gera.dhge.de", new String[]{"password123", "Joanna"},
        "haerir.vi23@stud.gera.dhge.de", new String[]{"password456", "Irene"}
    );

    private static boolean login(String mail, String pass) {
        return USERS.containsKey(mail) && USERS.get(mail)[0].equals(pass);
    }

    private static String resolveUsername(String mail) {
        String[] entry = USERS.get(mail);
        return entry != null ? entry[1] : null;
    }

    public static void main(String[] args) {
        System.out.println("=== Starte automatisierte Unit-Tests für User-Service ===");

        String body1 = "mail=test%40example.com&password=geheim";

        String extracted = UserService.extractParam(body1, "password");
        if ("geheim".equals(extracted)) {
            System.out.println("✅ Test extractParam (Passwort): ERFOLGREICH");
        } else {
            System.out.println("❌ Test extractParam (Passwort): FEHLGESCHLAGEN");
            System.exit(1);
        }

        String missing = UserService.extractParam(body1, "nichtVorhanden");
        if ("".equals(missing)) {
            System.out.println("✅ Test extractParam (fehlender Parameter): ERFOLGREICH");
        } else {
            System.out.println("❌ Test extractParam (fehlender Parameter): FEHLGESCHLAGEN");
            System.exit(1);
        }

        // %40 muss als @ dekodiert werden damit E-Mail-Adressen funktionieren
        String rawMail = UserService.extractParam(body1, "mail").replace("%40", "@");
        if ("test@example.com".equals(rawMail)) {
            System.out.println("✅ Test extractParam (%40-Dekodierung): ERFOLGREICH");
        } else {
            System.out.println("❌ Test extractParam (%40-Dekodierung): FEHLGESCHLAGEN");
            System.exit(1);
        }

        if (login("teegst.vi23@stud.gera.dhge.de", "password456")) {
            System.out.println("✅ Test Login (korrekte Daten): ERFOLGREICH");
        } else {
            System.out.println("❌ Test Login (korrekte Daten): FEHLGESCHLAGEN");
            System.exit(1);
        }

        if (!login("teegst.vi23@stud.gera.dhge.de", "falsch")) {
            System.out.println("✅ Test Login (falsches Passwort): ERFOLGREICH");
        } else {
            System.out.println("❌ Test Login (falsches Passwort): FEHLGESCHLAGEN");
            System.exit(1);
        }

        if (!login("unbekannt@example.com", "password123")) {
            System.out.println("✅ Test Login (unbekannte E-Mail): ERFOLGREICH");
        } else {
            System.out.println("❌ Test Login (unbekannte E-Mail): FEHLGESCHLAGEN");
            System.exit(1);
        }

        String name = resolveUsername("gramjo.vi23@stud.gera.dhge.de");
        if ("Joanna".equals(name)) {
            System.out.println("✅ Test Nutzernamen-Auflösung: ERFOLGREICH");
        } else {
            System.out.println("❌ Test Nutzernamen-Auflösung: FEHLGESCHLAGEN");
            System.exit(1);
        }

        System.out.println("=== Alle User-Service Tests bestanden! ===");
    }
}
