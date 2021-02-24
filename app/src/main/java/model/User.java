package model;

import java.util.UUID;

public class User {
    private String id;
    private String name;

    public User(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof User) {
            return this.id.equals(((User) object).id);
        }
        return false;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
