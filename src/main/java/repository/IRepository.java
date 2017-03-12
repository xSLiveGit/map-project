package repository;

import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sergiu on 1/4/2017.
 */
public interface IRepository<E,ID>{
    Collection<E> getAll() throws MapperException, RepositoryOpException, SQLException;
    E findById(ID id) throws RepositoryOpException, MapperException;
    E delete(ID id) throws MapperException, RepositoryOpException, SQLException;
    void update(E element, ID id) throws RepositoryOpException, MapperException, SQLException, DuplicateEnityException;
    void add(E element) throws RepositoryOpException, MapperException, SQLException, DuplicateEnityException;
    void populate(Collection<E> collection) throws MapperException, RepositoryOpException, SQLException;
    void clearAll() throws SQLException, RepositoryOpException, MapperException;
    Integer countElements();
    boolean exist(E element) throws SQLException, MapperException, RepositoryOpException;

}

