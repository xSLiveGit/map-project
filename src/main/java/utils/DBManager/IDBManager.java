package utils.DBManager;

import domain.HasID;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by Sergiu on 1/5/2017.
 */
public interface IDBManager<E extends HasID<ID>,ID> {
    void doDBUpdate(E element,ID id) throws MapperException, RepositoryOpException, SQLException;
    E doDBDelete(ID id) throws MapperException, RepositoryOpException;
    void doDBAdd(E element) throws RepositoryOpException, MapperException, SQLException;
    E doDBFindById(ID id) throws MapperException, RepositoryOpException;
    Collection<E> doDBGetAll() throws MapperException, RepositoryOpException;
    Collection<E> getDBPartial(Integer offset,Integer quantity) throws MapperException, RepositoryOpException;
    void clearAll() throws SQLException;
    Integer getDBSize();
    void doDBAddOriginal(E element) throws MapperException, RepositoryOpException, SQLException;
    boolean exist(E element) throws MapperException, SQLException;
}
