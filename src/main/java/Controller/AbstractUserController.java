package Controller;

import domain.IUser;
import domain.User;
import repository.IDataBaseRepository;
import utils.exceptions.ControllerException;
import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;
import utils.params_loader.IParamsLoader;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by Sergiu on 1/20/2017.
 */
public abstract class AbstractUserController<E extends IUser<String>> {
    private IDataBaseRepository<E, String> repository;
    private IParamsLoader<E,String> paramsLoader;

    public AbstractUserController(IDataBaseRepository<E,String> repository,IParamsLoader<E,String> paramsLoader){
        this.repository = repository;
        this.paramsLoader = paramsLoader;
    }

    public AbstractUserController(){

    }

    public void setParamsLoader(IParamsLoader<E,String> paramsLoader){
        this.paramsLoader = paramsLoader;
    }

    public void setRepository(IDataBaseRepository<E, String> repository){
        this.repository = repository;
    }

    public void add(String... args) throws ControllerException, MapperException, SQLException, RepositoryOpException, DuplicateEnityException {
        E element = paramsLoader.getElement(args);
        repository.addOriginal(element);
    }

    public void update(String... args) throws MapperException, SQLException, RepositoryOpException, DuplicateEnityException, ControllerException {
        E element = paramsLoader.getElement(args);
        repository.update(element,element.getName());
    }

    public E findById(String... args) throws MapperException, RepositoryOpException, ControllerException {
        String userName = paramsLoader.getId(args);
        return repository.findById(userName);
    }

    public void saveAll() throws MapperException, FileNotFoundException, RepositoryOpException, SQLException {
        this.repository.saveData();
    }

    public Collection<E> getAll() throws MapperException, RepositoryOpException, SQLException {
        return this.repository.getAll();
    }

    public void delete(String... obj) throws RepositoryOpException, MapperException, ControllerException, SQLException {
        String key = paramsLoader.getId(obj);
        repository.delete(key);
    }
}
