import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PostServiceImplements implements PostService {
    private List<Post> posts = new ArrayList<>();
    private final String CSV_FILE = "posts_data.csv";

    public PostServiceImplements() { 
        // Selbstheilung: Prüfen ob Datei existiert, bevor geladen wird
        ensureCSVExists();
        loadFromCSV(); 
    }

    private void ensureCSVExists() {
        File f = new File(CSV_FILE);
        if (!f.exists()) {
            try {
                f.createNewFile();
                System.out.println("ℹ️ posts_data.csv wurde automatisch erstellt.");
            } catch (IOException e) {
                System.err.println("❌ Konnte posts_data.csv nicht erstellen: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Post> list() { return posts; }

    @Override
    public Post get(int id) {
        return posts.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Post addLike(int id) {
        Post p = get(id);
        if (p != null) { p.setLikes(p.getLikes() + 1); saveToCSV(); }
        return p;
    }

    @Override
    public Post delete(int id) {
        Post p = get(id);
        if (p != null) { posts.remove(p); saveToCSV(); }
        return p;
    }

    @Override
    public Post create(String caption, LocalDate date, InputStream pictureStream, String contentType, String author) {
        Post p = new Post();
        int nextId = posts.isEmpty() ? 1 : posts.stream().mapToInt(Post::getId).max().getAsInt() + 1;
        p.setId(nextId);
        p.setCaption(caption);
        p.setDate(date != null ? date : LocalDate.now());
        p.setLikes(0);
        p.setAuthor(author); 
        
        if (pictureStream != null && contentType != null) {
            try {
                String ext = contentType.contains("video") ? ".mp4" : ".png";
                String fileName = "upload_" + nextId + ext;
                File dir = new File("images");
                if (!dir.exists()) dir.mkdir();
                Files.copy(pictureStream, new File(dir, fileName).toPath());
                p.setImagePath("/images/" + fileName);
            } catch (IOException e) { p.setImagePath("null"); }
        } else { p.setImagePath("null"); }

        posts.add(p);
        saveToCSV();
        return p;
    }

    @Override
    public Post updateCaption(int id, String newCaption) {
        Post p = get(id);
        if (p != null) { p.setCaption(newCaption); saveToCSV(); }
        return p;
    }

    private void loadFromCSV() {
        File f = new File(CSV_FILE);
        if (!f.exists()) return;
        posts.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                Post p = new Post();
                if (parts.length >= 6) { 
                    p.setId(Integer.parseInt(parts[0]));
                    p.setCaption(parts[1]);
                    p.setDate(LocalDate.parse(parts[2]));
                    p.setImagePath(parts[3]);
                    p.setLikes(Integer.parseInt(parts[4]));
                    p.setAuthor(parts[5]);
                    posts.add(p);
                } else if (parts.length == 5) { 
                    p.setId(Integer.parseInt(parts[0]));
                    p.setCaption(parts[1]);
                    p.setDate(LocalDate.parse(parts[2]));
                    p.setImagePath(parts[3]);
                    p.setLikes(Integer.parseInt(parts[4]));
                    p.setAuthor("Anonym");
                    posts.add(p);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            for (Post p : posts) {
                pw.println(p.getId() + ";" + p.getCaption() + ";" + p.getDate() + ";" + p.getImagePath() + ";" + p.getLikes() + ";" + p.getAuthor());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}