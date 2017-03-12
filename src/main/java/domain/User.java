package domain;

import java.util.SimpleTimeZone;

/**
 * Created by Sergiu on 1/19/2017.
 */
public class User implements HasID<String>,IUser<String> {
    private String name;
    private String password;

    public void setType(String type) {
        this.type = type;
    }

    private String type;
    private final static Integer masterId = 4;
    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public void setId(String s) {

    }

    public void incrementPrivileges(){
        if(this.type.equals("Basic")){
            this.type = "Power";
        }
        else if(this.type.equals("Power")){
            this.type.equals("Admin");
        }
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.type = "Basic";
    }

    public User(String name, String password, String type) {
        this.name = name;
        this.password = password;
        this.type = type;
    }

    public String getPassword(){
        return this.password;
    }

    public String getUserType(){
        return this.type;
    }

    public String getNextUserType(){
        String res = "";
        switch (this.type){
            case "Basic":
                res = "Power";
                break;
            case "Power":
                res = "Admin";
                break;
            case "Admin":
                res = "Admin";
            case "Master":
                res = "Master";
                break;
            default:
                res = "";
                break;
        }
        return res;
    }

    public boolean hasAddEntityPrivileges(){
        return this.type.equals("Power") || this.type.equals("Admin") || this.type.equals("Master") || this.type.equals("Basic");
    }

    public boolean hasUpdateEntityPrivileges(){
        return this.type.equals("Power") || this.type.equals("Admin") || this.type.equals("Master");
    }

    public boolean hasDeleteEntityPrivileges(){
        return this.type.equals("Power") || this.type.equals("Admin") || this.type.equals("Master");
    }

    public boolean hasAddRelationPrivileges(){
        return this.type.equals("Admin") || this.type.equals("Master");
    }

    public boolean hasDeleteRelationPrivileges(){
        return this.type.equals("Admin") || this.type.equals("Master");
    }

    public boolean hasFilterByPrefixPrivileges(){
        return this.type.equals("Power") || this.type.equals("Admin") || this.type.equals("Master") || this.type.equals("Basic");
    }

    public boolean hasReportsPrivileges(){
        return this.type.equals("Power") || this.type.equals("Admin") || this.type.equals("Master") || this.type.equals("Basic");
    }

    public boolean isMasterUser(){
        return this.type.equals("Master");
    }

    public boolean isEndUser(){
        return this.type.equals("Admin");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }
}
