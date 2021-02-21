package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupManager implements Serializable {

    private List<Group> groups;

    public GroupManager() {
        this.groups = new ArrayList<>();
    }

    public void addGroup(String code, String name){
        groups.add(new Group(code, name));
    }

    public List<Group> getGroups(){

        return this.groups;
    }
}
