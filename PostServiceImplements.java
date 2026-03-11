import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PostServiceImplements implements PostService {
    private List<Post> posts = new ArrayList<>();
    private final String CSV_FILE = "posts_data.csv";

    public PostServiceImplements() {
        loadFromCSV();
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
    public Post create(String caption, LocalDate date, InputStream pictureStream, String contentType) {
        Post p = new Post();
        int nextId = posts.isEmpty() ? 1 : posts.stream().mapToInt(Post::getId).max().getAsInt() + 1;
        p.setId(nextId);
        p.setCaption(caption);
        p.setDate(date != null ? date : LocalDate.now());
        p.setLikes(0);
        
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
                if (parts.length >= 5) {
                    Post p = new Post();
                    p.setId(Integer.parseInt(parts[0]));
                    p.setCaption(parts[1]);
                    p.setDate(LocalDate.parse(parts[2]));
                    p.setImagePath(parts[3]);
                    p.setLikes(Integer.parseInt(parts[4]));
                    posts.add(p);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            for (Post p : posts) {
                pw.println(p.getId() + ";" + p.getCaption() + ";" + p.getDate() + ";" + p.getImagePath() + ";" + p.getLikes());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}