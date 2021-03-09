package modelv2;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class User implements Serializable {

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

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            return this.id.equals((String) obj);
        } catch (ClassCastException e) {
            try {
                return this.id.equals(((User) obj).getId());
            } catch (ClassCastException f) {
                return false;
            }
        }
    }
}
