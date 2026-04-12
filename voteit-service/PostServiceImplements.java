import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class PostServiceImplements implements PostService {
    private List<Post> posts = new ArrayList<>();
    private final String CSV_FILE = "posts_data.csv";

    public PostServiceImplements() { 
        ensureCSVExists();
        loadFromCSV(); 
    }

    private void ensureCSVExists() {
        File f = new File(CSV_FILE);
        if (!f.exists()) {
            try { f.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    @Override
    public Post addLike(int id, String userName) {
        Post p = get(id);
        if (p != null && userName != null && !userName.isEmpty()) {
            // Toggle: schon geliked -> entfernen, sonst hinzufügen
            if (p.getLikedBy().contains(userName)) {
                p.getLikedBy().remove(userName);
            } else {
                p.getLikedBy().add(userName);
            }
            saveToCSV();
        }
        return p;
    }

    @Override
    public List<Post> list() { return posts; }
    @Override
    public Post get(int id) { return posts.stream().filter(p -> p.getId() == id).findFirst().orElse(null); }
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
        p.setAuthor(author);
        p.setImagePath("null");
        
        if (pictureStream != null && contentType != null) {
            try {
                String ext = contentType.contains("video") ? ".mp4" : ".png";
                String fileName = "upload_" + nextId + ext;
                File dir = new File("images");
                if (!dir.exists()) dir.mkdir();
                Files.copy(pictureStream, new File(dir, fileName).toPath());
                p.setImagePath("/images/" + fileName);
            } catch (IOException e) { p.setImagePath("null"); }
        }

        posts.add(p);
        saveToCSV();
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
                if (parts.length >= 6) {
                    Post p = new Post();
                    p.setId(Integer.parseInt(parts[0]));
                    p.setCaption(parts[1]);
                    p.setDate(LocalDate.parse(parts[2]));
                    p.setImagePath(parts[3]);
                    p.setAuthor(parts[5]);
                    
                    if (parts.length >= 7 && !parts[6].isEmpty()) {
                        String[] likers = parts[6].split(",");
                        p.setLikedBy(new HashSet<>(Arrays.asList(likers)));
                    }
                    posts.add(p);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            for (Post p : posts) {
                String likersCSV = String.join(",", p.getLikedBy());
                pw.println(p.getId() + ";" + p.getCaption() + ";" + p.getDate() + ";" + 
                           p.getImagePath() + ";" + p.getLikes() + ";" + p.getAuthor() + ";" + likersCSV);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public Post updateCaption(int id, String newCaption) {
        Post p = get(id);
        if (p != null) { p.setCaption(newCaption); saveToCSV(); }
        return p;
    }
}