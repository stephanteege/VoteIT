import java.awt.Image;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class PostServiceImplements implements PostService {
    private int counter = 1;
    private Map<Integer, Post> posts = new HashMap<>();
    private final String FILE_NAME = "posts_data.csv";
    private final String IMAGE_DIR = "images";

    public PostServiceImplements() {
        File dir = new File(IMAGE_DIR);
        if (!dir.exists()) dir.mkdir();
        loadFromFile();
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return; 
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int maxId = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 5) {
                    Post post = new Post();
                    post.setId(Integer.parseInt(parts[0].trim()));
                    post.setCaption(parts[1]);
                    post.setDate(LocalDate.parse(parts[2].trim()));
                    post.setImagePath(parts[3].trim());
                    post.setLikes(Integer.parseInt(parts[4].trim()));
                    posts.put(post.getId(), post);
                    if (post.getId() > maxId) maxId = post.getId();
                }
            }
            counter = maxId + 1;
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Post p : posts.values()) {
                pw.println(p.getId() + ";" + p.getCaption() + ";" + p.getDate() + ";" + p.getImagePath() + ";" + p.getLikes());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public Post create(String caption, LocalDate date, InputStream pictureStream, String contentType) {
        Post post = new Post();
        post.setId(counter++);
        post.setCaption(caption);
        post.setDate(LocalDate.now());
        post.setLikes(0);

        if (pictureStream != null && contentType != null) {
            // Logik zur Erkennung der Endung verbessert
            String ext = "jpg";
            if (contentType.toLowerCase().contains("video") || contentType.toLowerCase().contains("mp4")) {
                ext = "mp4";
            } else if (contentType.toLowerCase().contains("png")) {
                ext = "png";
            }
            
            String fileName = "media_" + post.getId() + "." + ext;
            try (FileOutputStream out = new FileOutputStream(new File(IMAGE_DIR, fileName))) {
                pictureStream.transferTo(out);
                post.setImagePath("/images/" + fileName); 
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            post.setImagePath("null");
        }
        
        posts.put(post.getId(), post);
        saveToFile();
        return post;
    }

    @Override
    public Post updateCaption(int id, String newCaption) {
        Post p = posts.get(id);
        if (p != null) { p.setCaption(newCaption); saveToFile(); }
        return p;
    }

    public Post addLike(int id) {
        Post p = posts.get(id);
        if (p != null) { p.setLikes(p.getLikes() + 1); saveToFile(); }
        return p;
    }

    public List<Post> list() { return new ArrayList<>(posts.values()); }
    public Post get(int id) { return posts.get(id); }
    public Post delete(int id) { Post p = posts.remove(id); saveToFile(); return p; }
    public Post create(String c, LocalDate d, Image p) { return null; }
    public Post updateCaption(int i, String c, LocalDate d) { return null; }
    public Post updatePicture(int i, Image p) { return null; }
}