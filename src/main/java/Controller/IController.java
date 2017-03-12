package Controller;

import utils.exceptions.ControllerException;
import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * Created by Sergiu on 1/5/2017.
 */
public interface IController<E> {
    void add(String... obj) throws MapperException, RepositoryOpException, ControllerException, SQLException, DuplicateEnityException;
    void delete(String... obj) throws RepositoryOpException, MapperException, ControllerException, SQLException;
    void update(String... obj) throws MapperException, RepositoryOpException, ControllerException, SQLException, DuplicateEnityException;
    Collection<E> getAll() throws RepositoryOpException, MapperException, SQLException;
    Collection<E> getAllFromCurrentPage();
    E findById(String... obj) throws MapperException, RepositoryOpException, ControllerException;
    boolean hasNextPage() throws MapperException, RepositoryOpException;
    boolean hasPreviousPage() throws MapperException, RepositoryOpException;
    void nextPage() throws MapperException, RepositoryOpException, SQLException;
    void previousPage() throws MapperException, RepositoryOpException, SQLException;
    Collection<E> filterCollection(Collection<E> col, Predicate<E> predicate);
    Collection<E> sortCollection(Collection<E> col, Comparator<E> comparator);
    void setCurrentPageNumber(String pageNr) throws RepositoryOpException, ControllerException, MapperException, SQLException;
    void clearAll() throws SQLException, RepositoryOpException, MapperException;
    Integer getCurrentPageNumber();
    void saveAll() throws MapperException, FileNotFoundException, RepositoryOpException, SQLException;
    void addOriginal(String... obj) throws SQLException, MapperException, ControllerException, RepositoryOpException;
    Integer size();
}
