package model;


import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User implements Serializable {

    private String id;
    private String name;
    private ArrayList<Group> groups;

    public User(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> nested = new HashMap<>();
        nested.put(this.getCurrentGroup().getId(), this.getCurrentGroup().getName());
        result.put("currentGroup", nested);
        Map<String, Object> nested2 = new HashMap<>();
        for (Group group : groups) {
            if (!group.equals(getCurrentGroup())) {
                nested2.put(group.getId(), getName());
            }
        }
        result.put("groups",nested2);
        return result;
    }

    //TODO::
    private Group getCurrentGroup() {
        return new Group("as", "ASd", new User("wrt"));
    }

    public String getId() {
        return id;
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

    public String getName() {
        return name;
    }

}
