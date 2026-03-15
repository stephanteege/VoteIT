import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Map;

public class UserService {
    private static final Map<String, String[]> USERS = Map.of(
        "finkca.vi23@stud.gera.dhge.de", new String[]{"password123", "Caro"},
        "teegst.vi23@stud.gera.dhge.de", new String[]{"password456", "Stephan"},
        "gramjo.vi23@stud.gera.dhge.de", new String[]{"password123", "Joanna"},
        "haerir.vi23@stud.gera.dhge.de", new String[]{"password456", "Irene"}
    );

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8090), 0);
        
        // Login-Endpunkt
        server.createContext("/login", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                String mail = extractParam(body, "mail").replace("%40", "@");
                String pass = extractParam(body, "password");

                if (USERS.containsKey(mail) && USERS.get(mail)[0].equals(pass)) {
                    String name = USERS.get(mail)[1];
                    // Cookie setzen für beide Services
                    exchange.getResponseHeaders().add("Set-Cookie", "user=" + name + "; Path=/; HttpOnly");
                    exchange.getResponseHeaders().add("Location", "http://localhost:8089/member_area.html");
                    exchange.sendResponseHeaders(302, -1);
                } else {
                    exchange.getResponseHeaders().add("Location", "http://localhost:8089/index.html?error=1");
                    exchange.sendResponseHeaders(302, -1);
                }
            }
            exchange.close();
        });

        // Logout
        server.createContext("/logout", exchange -> {
            exchange.getResponseHeaders().add("Set-Cookie", "user=; Path=/; Max-Age=0");
            exchange.getResponseHeaders().add("Location", "http://localhost:8089/index.html");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        });

        server.start();
        System.out.println("User-Service läuft auf Port 8090");
    }

    private static String extractParam(String body, String name) {
        try {
            for (String pair : body.split("&")) {
                String[] kv = pair.split("=");
                if (kv[0].equals(name)) return kv[1];
            }
        } catch (Exception e) {}
        return "";
    }
}