import java.awt.Image;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

public interface PostService {
    public Post get(int id);
    public List<Post> list();
    public Post delete(int id);
    public Post addLike(int id);
    public Post create(String caption, LocalDate date, InputStream pictureStream, String contentType);
    public Post updateCaption(int id, String newCaption);
    
    public Post create(String caption, LocalDate date, Image picture);
    public Post updateCaption(int id, String caption, LocalDate date);
    public Post updatePicture(int id, Image picture);
}