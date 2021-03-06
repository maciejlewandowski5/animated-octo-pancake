package modelv2;

public class User {

    private String name;
    private String id;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public User(User user) {
        this.name = user.name;
        this.id = user.id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
