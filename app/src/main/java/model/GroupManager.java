package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupManager implements Serializable {

    public Group getCurrentGroup(){

        return new Group();
    }
    public ArrayList<Group> getGroups(){

        return new ArrayList<>();
    }
}
