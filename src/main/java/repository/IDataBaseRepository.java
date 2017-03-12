package repository;

import utils.exceptions.FileErrorException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by Sergiu on 1/4/2017.
 */
public interface IDataBaseRepository<E,ID> extends IRepository<E,ID> {
    void loadData() throws FileErrorException, RepositoryOpException,FileNotFoundException,MapperException;
    void saveData() throws FileErrorException, FileNotFoundException, MapperException, RepositoryOpException, SQLException;

    boolean hasNextPage();
    boolean hasPreviousPage();

    void previousPage() throws RepositoryOpException, MapperException, SQLException;
    void nextPage() throws RepositoryOpException, MapperException, SQLException;

    Collection<E> getAllFromPage();
    void setCurrentPageNumber(Integer pageNr) throws RepositoryOpException, MapperException, SQLException;
    Integer getCurretnPageNumber();
    void addOriginal(E el) throws RepositoryOpException, SQLException, MapperException;
    boolean existInDB(E element) throws SQLException, MapperException;
}
