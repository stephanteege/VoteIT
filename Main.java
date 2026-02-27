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
        
        server.createContext("/logo.png", exchange -> {
            File file = new File("logo.png");
            if (file.exists()) {
                exchange.getResponseHeaders().add("Content-Type", "image/png");
                byte[] b = Files.readAllBytes(file.toPath());
                exchange.sendResponseHeaders(200, b.length);
                exchange.getResponseBody().write(b);
            } else { exchange.sendResponseHeaders(404, -1); }
            exchange.close();
        });

        server.createContext("/images/", exchange -> {
            String path = exchange.getRequestURI().getPath().substring(8); 
            File file = new File("images", path);
            if (file.exists()) {
                // Header-Erkennung für Videos
                String contentType = path.toLowerCase().endsWith(".mp4") ? "video/mp4" : "image/jpeg";
                exchange.getResponseHeaders().add("Content-Type", contentType);
                byte[] b = Files.readAllBytes(file.toPath());
                exchange.sendResponseHeaders(200, b.length);
                exchange.getResponseBody().write(b);
            } else { exchange.sendResponseHeaders(404, -1); }
            exchange.close();
        });

        server.createContext("/like", exchange -> {
            int id = Integer.parseInt(exchange.getRequestURI().getQuery().split("=")[1]);
            service.addLike(id);
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });

        server.createContext("/delete", exchange -> {
            int id = Integer.parseInt(exchange.getRequestURI().getQuery().split("=")[1]);
            service.delete(id);
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });

        server.createContext("/update", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                int id = Integer.parseInt(query.split("=")[1]);
                String newCaption = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                service.updateCaption(id, newCaption);
                exchange.sendResponseHeaders(200, -1);
            }
            exchange.close();
        });

        server.createContext("/", exchange -> {
            byte[] b = Files.readAllBytes(new File("index.html").toPath());
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, b.length);
            exchange.getResponseBody().write(b);
            exchange.close();
        });

        server.start();
        System.out.println("Server läuft auf http://localhost:8089");
    }

    static class Handler implements HttpHandler {
        private PostService service;
        Handler(PostService s) { this.service = s; }

        public void handle(HttpExchange request) throws IOException {
            request.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            StringBuilder out = new StringBuilder();
            
            if ("GET".equals(request.getRequestMethod())) {
                out.append("[");
                List<Post> list = service.list();
                for (int i = 0; i < list.size(); i++) {
                    Post p = list.get(i);
                    out.append("{\"Bild_ID\":"+p.getId()+",\"Beschreibung\":\""+p.getCaption()+"\",\"Likes\":"+p.getLikes()+",\"BildPfad\":\""+p.getImagePath()+"\",\"Erstelldatum\":\""+p.getDate()+"\"}");
                    if (i < list.size() - 1) out.append(",");
                }
                out.append("]");
            } else if ("POST".equals(request.getRequestMethod())) {
                byte[] bytes = request.getRequestBody().readAllBytes();
                String body = new String(bytes, "ISO-8859-1");
                
                String caption = "Kein Text";
                int cIdx = body.indexOf("name=\"caption\"");
                if (cIdx != -1) {
                    int s = body.indexOf("\r\n\r\n", cIdx) + 4;
                    int e = body.indexOf("\r\n", s);
                    caption = new String(body.substring(s, e).getBytes("ISO-8859-1"), "UTF-8");
                }

                InputStream is = null;
                String ct = null;
                int fIdx = body.indexOf("name=\"file\"");
                if (fIdx != -1 && body.contains("filename=\"")) {
                    int ctS = body.indexOf("Content-Type: ", fIdx) + 14;
                    int ctE = body.indexOf("\r\n", ctS);
                    ct = body.substring(ctS, ctE).trim();
                    
                    int dS = body.indexOf("\r\n\r\n", ctE) + 4;
                    int dE = body.lastIndexOf("\r\n---");
                    if (dE > dS) {
                        byte[] mediaData = new byte[dE - dS];
                        System.arraycopy(bytes, dS, mediaData, 0, mediaData.length);
                        is = new ByteArrayInputStream(mediaData);
                    }
                }
                service.create(caption, LocalDate.now(), is, ct);
                out.append("{\"status\":\"ok\"}");
            }
            byte[] resp = out.toString().getBytes("UTF-8");
            request.sendResponseHeaders(200, resp.length);
            request.getResponseBody().write(resp);
            request.close();
        }
    }
}