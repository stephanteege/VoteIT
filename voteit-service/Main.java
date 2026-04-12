import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String args[]) throws IOException {
        PostService service = new PostServiceImplements();
        HttpServer server = HttpServer.create(new InetSocketAddress(8089), 0);
        
        server.createContext("/main", new Handler(service));
        server.createContext("/like", new ActionHandler(service, "LIKE"));
        server.createContext("/delete", new ActionHandler(service, "DELETE"));
        server.createContext("/update", new ActionHandler(service, "UPDATE"));

        serveFile(server, "/style.css", "style.css", "text/css");
        serveFile(server, "/index.html", "index.html", "text/html");
        serveFile(server, "/member_area.html", "member_area.html", "text/html"); // FEHLTE!
        serveFile(server, "/ICON.png", "logo.png", "image/png");

        server.createContext("/images/", exchange -> {
            String path = exchange.getRequestURI().getPath().substring(8); 
            File file = new File("images", path);
            if (file.exists()) {
                String contentType = path.toLowerCase().endsWith(".mp4") ? "video/mp4" : "image/jpeg";
                exchange.getResponseHeaders().add("Content-Type", contentType);
                byte[] b = Files.readAllBytes(file.toPath());
                exchange.sendResponseHeaders(200, b.length);
                exchange.getResponseBody().write(b);
            } else { exchange.sendResponseHeaders(404, -1); }
            exchange.close();
        });

        server.createContext("/health", exchange -> {
            byte[] resp = "{\"status\":\"UP\",\"service\":\"voteit-service\"}".getBytes("UTF-8");
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
            exchange.close();
        });

        server.createContext("/", exchange -> {
            File file = new File("index.html");
            if (file.exists()) {
                byte[] b = Files.readAllBytes(file.toPath());
                exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, b.length);
                exchange.getResponseBody().write(b);
            } else { exchange.sendResponseHeaders(404, -1); }
            exchange.close();
        });

        server.start();
        System.out.println("VoteIT-Service läuft auf Port 8089");
    }

    private static void serveFile(HttpServer server, String path, String file, String type) {
        server.createContext(path, e -> {
            File f = new File(file);
            if (f.exists()) {
                byte[] b = Files.readAllBytes(f.toPath());
                e.getResponseHeaders().add("Content-Type", type + "; charset=UTF-8");
                e.sendResponseHeaders(200, b.length);
                e.getResponseBody().write(b);
            } else { e.sendResponseHeaders(404, -1); }
            e.close();
        });
    }

    static class Handler implements HttpHandler {
        private PostService service;
        public Handler(PostService service) { this.service = service; }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                List<Post> posts = service.list();
                StringBuilder out = new StringBuilder("[");
                for (int i = 0; i < posts.size(); i++) {
                    Post p = posts.get(i);
                    out.append("{")
                       .append("\"Bild_ID\":").append(p.getId())
                       .append(",\"Beschreibung\":\"").append(p.getCaption()).append("\"")
                       .append(",\"Likes\":").append(p.getLikes())
                       .append(",\"BildPfad\":\"").append(p.getImagePath()).append("\"")
                       .append(",\"Erstelldatum\":\"").append(p.getDate()).append("\"")
                       .append(",\"Ersteller\":\"").append(p.getAuthor()).append("\"")
                       .append("}");
                    if (i < posts.size() - 1) out.append(",");
                }
                out.append("]");
                byte[] resp = out.toString().getBytes("UTF-8");
                exchange.sendResponseHeaders(200, resp.length);
                exchange.getResponseBody().write(resp);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                String cookie = exchange.getRequestHeaders().getFirst("Cookie");
                String user = (cookie != null && cookie.contains("user=")) ? cookie.split("user=")[1].split(";")[0] : "Anonym";
                
                byte[] bytes = exchange.getRequestBody().readAllBytes();
                String body = new String(bytes, "ISO-8859-1");
                String caption = "Kein Text";
                InputStream is = null;
                String ct = null;

                int cIdx = body.indexOf("name=\"caption\"");
                if (cIdx != -1) {
                    int s = body.indexOf("\r\n\r\n", cIdx) + 4;
                    int e = body.indexOf("\r\n", s);
                    caption = new String(body.substring(s, e).getBytes("ISO-8859-1"), "UTF-8");
                }

                int fIdx = body.indexOf("name=\"file\"");
                if (fIdx != -1 && body.contains("filename=\"")) {
                    int ctS = body.indexOf("Content-Type: ", fIdx) + 14;
                    int ctE = body.indexOf("\r\n", ctS);
                    ct = body.substring(ctS, ctE).trim();
                    int dS = body.indexOf("\r\n\r\n", ctE) + 4;
                    int dE = body.lastIndexOf("\r\n--");
                    if (dE > dS) {
                        byte[] data = new byte[dE - dS];
                        System.arraycopy(bytes, dS, data, 0, data.length);
                        is = new ByteArrayInputStream(data);
                    }
                }
                
                service.create(caption, LocalDate.now(), is, ct, user);
                exchange.sendResponseHeaders(200, 0);
            }
            exchange.close();
        }
    }

    static class ActionHandler implements HttpHandler {
        private PostService service;
        private String action;
        public ActionHandler(PostService s, String a) { this.service = s; this.action = a; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("id=")) {
                int id = Integer.parseInt(query.split("=")[1]);
                
                // NEU: Den Usernamen aus dem Cookie holen, genau wie beim Posten!
                String cookie = exchange.getRequestHeaders().getFirst("Cookie");
                String currentUser = (cookie != null && cookie.contains("user=")) ? cookie.split("user=")[1].split(";")[0] : "Anonym";

                if ("LIKE".equals(action)) {
                    // NEU: Jetzt geben wir den Namen an die Like-Methode weiter
                    service.addLike(id, currentUser); 
                }
                if ("DELETE".equals(action)) service.delete(id);
                if ("UPDATE".equals(action)) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
                    String newCap = r.readLine();
                    if (newCap != null) service.updateCaption(id, newCap);
                }
            }
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        }
    }
}