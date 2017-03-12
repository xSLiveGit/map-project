package domain;

/**
 * Created by Sergiu on 1/20/2017.
 */
public class UserRequest implements HasID<String>,IUser<String> {
    private String name;
    private String oldType;
    private String newType;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setOldType(String oldType) {
        this.oldType = oldType;
    }

    public void setNewType(String newType) {
        this.newType = newType;
    }

    @Override
    public String getName() {

        return name;
    }

    public String getOldType() {
        return oldType;
    }

    public String getNewType() {
        return newType;
    }

    public UserRequest(String name, String oldType, String newType) {

        this.name = name;
        this.oldType = oldType;
        this.newType = newType;
    }

    @Override
    public String getId() {
        return getName();
    }

    @Override
    public void setId(String s) {
        setName(s);
    }
}
