package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupManager implements Serializable {

    private static GroupManager singleInstance = null;
    private Group currentGroup;
    private List<Group> groups;

    private GroupManager() {
        this.groups = new ArrayList<>();
    }

    public void addGroup(String code, String name, User user){
        groups.add(new Group(code, name, user));
    }

    public static GroupManager getInstance(){
        if (singleInstance == null){
            singleInstance = new GroupManager();
        }
        return singleInstance;
    }

    public List<Group> getGroups(){
        return this.groups;
    }

    public Group getCurrentGroup(){

        if(currentGroup == null){
            return groups.get(0);
        }
        return currentGroup;
    }

    public void setCurrentGroup(Group group){
        currentGroup = group;
    }


}
