import java.util.List;

public interface UserDAO {
    List<User> findAll();

    User findByEmail(String email);

    User findByUsername(String username);

    void insert(User user);

    void update(User user);

    void delete(User user);
}
