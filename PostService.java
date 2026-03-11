import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

public interface PostService {
    Post get(int id);
    List<Post> list();
    Post delete(int id);
    Post addLike(int id);
    Post create(String caption, LocalDate date, InputStream pictureStream, String contentType);
    Post updateCaption(int id, String newCaption);
}