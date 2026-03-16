import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Post {
    private int id;
    private String caption;
    private LocalDate date;
    private String imagePath;
    private String author;
    
    // Speichert die Usernamen der Personen, die gelikt haben
    private Set<String> likedBy = new HashSet<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Set<String> getLikedBy() { return likedBy; }
    public void setLikedBy(Set<String> likedBy) { this.likedBy = likedBy; }

    // Die Anzahl der Likes ist die Größe der Liste der Liker
    public int getLikes() { return likedBy.size(); }
}