package domain;

/**
 * Created by Sergiu on 1/20/2017.
 */
public interface IUser<ID> extends HasID<ID> {
    ID getName();
    void setName(ID id);
}
