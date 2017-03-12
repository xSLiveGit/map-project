package Controller;

import domain.HasID;
import repository.IDataBaseRepository;
import repository.IRepository;
import utils.exceptions.ControllerException;
import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;
import utils.params_loader.IParamsLoader;
import validator.StaticValidator;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Sergiu on 1/5/2017.
 */
public class AbstractController<E extends HasID<Integer>> implements IController<E> {
    protected IParamsLoader<E,Integer> paramsLoader;
    protected IDataBaseRepository<E, Integer> repository;

    public AbstractController(IDataBaseRepository<E, Integer> repository, IParamsLoader<E,Integer> paramsLoader)
    {
        this.repository = repository;
        this.paramsLoader = paramsLoader;
    }

    public AbstractController(){

    }

    public void setParamsLoader(IParamsLoader<E,Integer> paramsLoader){
        this.paramsLoader = paramsLoader;
    }

    public void setRepository(IDataBaseRepository<E, Integer> repository){
        this.repository = repository;
    }

    @Override
    public void add(String... obj) throws MapperException, RepositoryOpException, ControllerException, SQLException, DuplicateEnityException {
        E element = paramsLoader.getElement(obj);
        Random rn = new Random();
        Integer nr = 0;
        boolean gata = false;
        while (!gata){
            try {
                nr = Math.abs(rn.nextInt());
                element.setId(nr);
                repository.add (element);
                gata = true;
            }
            catch(RepositoryOpException e){
                System.out.println(nr);
            }
        }
    }

    @Override
    public void delete(String... obj) throws RepositoryOpException, MapperException, ControllerException, SQLException {
        Integer key = paramsLoader.getId(obj);
        repository.delete(key);
    }

    @Override
    public void update(String... obj) throws MapperException, RepositoryOpException, ControllerException, SQLException, DuplicateEnityException {
        E element = paramsLoader.getElement(obj);
        Integer key = element.getId();
        repository.update(element, key);
    }

    @Override
    public Collection<E> getAll() throws RepositoryOpException, MapperException, SQLException {
        return repository.getAll();
    }

    @Override
    public Collection<E> getAllFromCurrentPage() {
        return repository.getAllFromPage();
    }

    @Override
    public E findById(String... obj) throws MapperException, RepositoryOpException, ControllerException {
        int id = paramsLoader.getId(obj);
        return this.repository.findById(id);
    }

    @Override
    public boolean hasNextPage() {
        return repository.hasNextPage();
    }

    @Override
    public boolean hasPreviousPage() {
        return repository.hasPreviousPage();
    }

    @Override
    public void nextPage() throws MapperException, RepositoryOpException, SQLException {
        repository.nextPage();
    }

    @Override
    public void previousPage() throws MapperException, RepositoryOpException, SQLException {
        repository.previousPage();
    }

    @Override
    public Collection<E> filterCollection(Collection<E> col, Predicate<E> predicate) {
        return col.parallelStream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public Collection<E> sortCollection(Collection<E> col, Comparator<E> comparator) {
        return col.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public void setCurrentPageNumber(String pageNr) throws RepositoryOpException, ControllerException, MapperException, SQLException {
        if(pageNr.equals("")){
            return;
        }
        if(StaticValidator.isIntegerNumber(pageNr)) {
            this.repository.setCurrentPageNumber(Integer.parseInt(pageNr) - 1);
        }
        else{
            throw  new ControllerException("Given nr is not a positive integer number.");
        }
    }

    @Override
    public void clearAll() throws SQLException, RepositoryOpException, MapperException {
        this.repository.clearAll();
    }

    @Override
    public Integer getCurrentPageNumber() {
        return repository.getCurretnPageNumber();
    }

    @Override
    public void saveAll() throws MapperException, FileNotFoundException, RepositoryOpException, SQLException {
        this.repository.saveData();
    }

    @Override
    public void addOriginal(String... obj) throws SQLException, MapperException, ControllerException, RepositoryOpException {
        E element = paramsLoader.getElement(obj);
        repository.addOriginal(element);
    }
    @Override
    public Integer size(){
        return this.repository.countElements();
    }
}
