import java.time.LocalDate;

public class Post {
    private int id;
    private String caption;
    private LocalDate date;
    private String imagePath;
    private int likes;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
}