package model;


import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class User implements Serializable {

    private String id;
    private String name;
    private Map<String,String> groups;
    private String currentGroupId;
    private String currentGroupName;

    public User(String name,String id) {
        this.id = id;
        this.name = name;
        groups = new HashMap<>();
    }

    public User(DocumentSnapshot documentSnapshot){
        name = documentSnapshot.getString("name");
        id = documentSnapshot.getId();
        groups = (Map<String, String>) documentSnapshot.getData().get("groups");
        Set<Map.Entry<String, Object>> currentGroup = ((Map<String,Object>)documentSnapshot.getData().get("currentGroup")).entrySet();
        for(Map.Entry<String,Object> gn : currentGroup){
            groups.put(gn.getKey(),(String) gn.getValue());
            setCurrentGroupData(gn);
        }


    }

    public void addGroup(String id, String name) {
        groups.put(id,name);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> nested = new HashMap<>();
        nested.put(this.getCurrentGroupId(), this.getCurrentGroupName());
        result.put("currentGroup", nested);
        Map<String, Object> nested2 = new HashMap<>();
        for (Map.Entry<String,String> group : groups.entrySet()) {
            if (!group.getKey().equals(getCurrentGroupId())) {
                nested2.put(group.getKey(), group.getValue());
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
    public void setCurrentGroupData1(Map.Entry<String, String> string) {
        currentGroupId = string.getKey();
        currentGroupName = string.getValue();
    }

    public void addGroupData(Map.Entry<String, Object> gn) {
        groups.put(gn.getKey(),(String) gn.getValue());
    }



    public void setId(String uid) {
        id = uid;
    }

    public Map<String,String> getGroups() {
        return groups;
    }
}
