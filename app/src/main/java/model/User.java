package model;


import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class User implements Serializable {

    private String id;
    private String name;
    private ArrayList<Group> groups;
    private String currentGroupId;
    private String currentGroupName;

    public User(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        groups = new ArrayList<>();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> nested = new HashMap<>();
        nested.put(this.getCurrentGroupId(), this.getCurrentGroupName());
        result.put("currentGroup", nested);
        Map<String, Object> nested2 = new HashMap<>();
        for (Group group : groups) {
            if (!group.getId().equals(getCurrentGroupId())) {
                nested2.put(group.getId(), getName());
            }
        }
        result.put("groups",nested2);
        result.put("name",name);
        return result;
    }


    public String getCurrentGroupName(){
    return currentGroupName;
    }

    public String getCurrentGroupId(){
        return currentGroupId;
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

    public void setCurrentGroupData(Map.Entry<String, Object> string) {
        currentGroupId = string.getKey();
        currentGroupName = (String)string.getValue();
    }

    public void addGroupData(Map.Entry<String, Object> gn) {
    }

    public static User createUser(DocumentSnapshot documentSnapshot) {
        User user1 = new User(documentSnapshot.getString("name"));
        Set<Map.Entry<String, Object>> currentGroup = ((Map<String,Object>)documentSnapshot.getData().get("currentGroup")).entrySet();
        for(Map.Entry<String,Object> gn : currentGroup){
            user1.setCurrentGroupData(gn);
        }
        Set<Map.Entry<String, Object>> groups = ((Map<String,Object>)documentSnapshot.getData().get("groups")).entrySet();
        for(Map.Entry<String,Object> gn : groups){
            user1.addGroupData(gn);
        }
        return user1;
    }

    public void setId(String uid) {
        id = uid;
    }
}
