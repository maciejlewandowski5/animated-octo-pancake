package model;

import java.io.Serializable;

public class User implements Serializable {
    public String name = "user";

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
