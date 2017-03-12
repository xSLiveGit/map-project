package repository;


import domain.ConnectionWrapper;
import domain.HasID;
import utils.DBManager.IDBManager;
import utils.exceptions.*;
import utils.mapper.IMapper;
import validator.IValidator;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Sergiu on 1/4/2017.
 */
public class DBRepository<E extends HasID<ID>,ID> implements IDataBaseRepository<E,ID>,IRepository<E,ID>{
    private Connection connection;
    private IMapper<E,ID> mapper;
    private IValidator<E> validator;
    private IDBManager<E,ID> manager;

    private PageRepository<E,ID> page;
    private Integer currentPageNumber;//indexed from 0
    private Integer size;

    private Map<ID,E> toUpdate;
    private Map<ID,E> toDelete;
    private Map<ID,E> toAdd;
    private Map<ID,E> toAddOriginal;
    private Map<ID,E> Updated;
    private Integer pageSize;

    public DBRepository() throws FileNotFoundException, RepositoryOpException, MapperException {
        page = new PageRepository<E, ID>(validator,true);
        toDelete = new HashMap<ID, E>();
        toUpdate = new HashMap<ID,E>();
        toAdd = new HashMap<ID,E>();
        toAddOriginal = new HashMap<ID, E>();
        Updated = new HashMap<ID, E>();


    }

    public DBRepository(ConnectionWrapper connection, IValidator<E> validator, IMapper<E,ID> mapper, IDBManager<E,ID> manager, Integer pageSize) throws RepositoryOpException, MapperException, FileNotFoundException {
        this.connection = connection.getConnection();
        this.mapper = mapper;
        this.validator = validator;
        this.manager = manager;
        this.pageSize = pageSize;

        page = new PageRepository<E, ID>(validator,true);
        toDelete = new HashMap<ID, E>();
        toUpdate = new HashMap<ID,E>();
        toAdd = new HashMap<ID,E>();
        toAddOriginal = new HashMap<ID, E>();
        Updated = new HashMap<ID, E>();

        this.currentPageNumber = 0;
        this.size = manager.getDBSize();
        loadData();
    }

    public void setConnection(ConnectionWrapper cw){
        this.connection = cw.getConnection();
    }

    public void setMapper(IMapper<E,ID> mapper){
        this.mapper = mapper;
    }

    public void setValidator(IValidator<E> validator){
        this.validator = validator;
    }

    public void setManager(IDBManager<E,ID> manager) throws FileNotFoundException, RepositoryOpException, MapperException {
        this.manager = manager;
        this.currentPageNumber = 0;
        this.size = manager.getDBSize();
        loadData();
    }

    public void setSize(Integer size){
        this.size = size;
    }
    @Override
    public void loadData() throws FileErrorException, RepositoryOpException, FileNotFoundException, MapperException {
        loadCurrentPage();
    }

    @Override
    public void saveData() throws FileErrorException, FileNotFoundException, MapperException, RepositoryOpException, SQLException {
        doDBAddOriginal();
        doDBAdd();
        doDBUpdate();
        doDBDelete();
        loadCurrentPage();
    }

    @Override
    public Collection<E> getAll() throws MapperException, RepositoryOpException, SQLException {
        doDBAddOriginal();
        doDBAdd();
        doDBUpdate();
        doDBDelete();
        loadCurrentPage();
        return manager.doDBGetAll();
    }

    @Override
    public E findById(ID id) throws RepositoryOpException, MapperException {
        E el = page.findById(id);
        if(null == el){
            el = manager.doDBFindById(id);
        }
        return el;
    }

    @Override
    public E delete(ID id) throws MapperException, RepositoryOpException, SQLException {
        E el = page.delete(id);
        if(el == null) {
            el = this.findById(id);
            if(null != el){
                el = manager.doDBDelete(id);
                this.size--;
            }

            return el;
        }
        else{
            toDelete.put(id,el);//we prepare delete
            if(toUpdate.containsKey(id)){
                toUpdate.remove(id);
            }
            if(page.countElements() == 0){
                doDBAddOriginal();
                doDBAdd();
                doDBUpdate();
                doDBDelete();
                loadCurrentPage();
            }
        }
        this.size--;
        return el;
    }

    @Override
    public void update(E element, ID id) throws RepositoryOpException, MapperException, SQLException, DuplicateEnityException {
        this.validator.validate(element);
        if(exist(element)){
            throw new DuplicateEnityException("There exist this entity");
        }
        E el = page.findById(id);
        if(null != el){
            page.update(element,id);
            toUpdate.put(id,element);
            Updated.put(el.getId(),el);
        }
        else{
            manager.doDBUpdate(element,id);
        }
    }

    @Override
    public void add(E element) throws MapperException, SQLException, DuplicateEnityException, RepositoryOpException {
        this.validator.validate(element);
        if(exist(element)){
            throw new DuplicateEnityException("There exist this entity");
        }
//        for(E el : page.getAll()){
//            if(el.equals(element)){
//                throw new RepositoryOpException("Duplicate entity error");
//            }
//        }
        if(page.countElements() < this.pageSize){
            this.page.add(element);
            toAdd.put(element.getId(),element);
            this.size++;
        }
        else {
            manager.doDBAdd(element);
            this.size++;
        }

    }

    @Override
    public void populate(Collection<E> collection) throws MapperException, RepositoryOpException, SQLException {
        for(E el : collection){
            try {
                this.validator.validate(el);
                manager.doDBAdd(el);
                this.size++;
            } catch (EntityValidatorException e) {
                e.printStackTrace();
            }
        }
        loadCurrentPage();
    }

    @Override
    public void clearAll() throws SQLException, RepositoryOpException, MapperException {
        manager.clearAll();
        page.clearAll();
        toAdd.clear();
        toDelete.clear();
        toUpdate.clear();
        toAddOriginal.clear();
        this.size = 0;
        loadCurrentPage();
    }

    @Override
    public Integer countElements() {
        return this.size;
    }

    @Override
    public boolean exist(E element) throws SQLException, MapperException, RepositoryOpException {
//        return false;/*
//        Daca decomentam, nu se permit duplicatele
        boolean ex = page.exist(element);
        if(ex){
            return true;
        }
        ex = manager.exist(element);
        if(ex) {
            doDBAddOriginal();
            doDBAdd();
            doDBUpdate();
            doDBDelete();
            loadCurrentPage();
            return manager.exist(element);
        }
        return ex;//*/
    }

    @Override
    public boolean existInDB(E element) throws SQLException, MapperException {

       return manager.exist(element);
    }

    @Override
    public Collection<E> getAllFromPage(){
        return page.getAll();
    }

    @Override
    public void setCurrentPageNumber(Integer pageNr) throws RepositoryOpException, MapperException, SQLException {
        if(pageNr < 0 || pageNr+1 > ((this.countElements() - 1)/this.pageSize + 1)){
            throw new RepositoryOpException(String.format("Invalid page number.Page must be between 1 and %s",((this.countElements() - 1)/this.pageSize + 1)));
        }
        this.currentPageNumber = pageNr;
        doDBAddOriginal();
        doDBAdd();
        doDBUpdate();
        doDBDelete();
        loadCurrentPage();
    }

    @Override
    public Integer getCurretnPageNumber() {
        return this.currentPageNumber;
    }

    @Override
    public void addOriginal(E el) throws RepositoryOpException, SQLException, MapperException {
        this.validator.validate(el);

        for(E element : page.getAll()){
            if(el.equals(element)){
                throw new RepositoryOpException("Duplicate entity error");
            }
        }

        if(page.countElements() < this.pageSize){
            this.page.add(el);
            toAddOriginal.put(el.getId(),el);
            this.size++;
        }
        else {
            manager.doDBAddOriginal(el);
            this.size++;
        }

    }

    private void doDBDelete() throws RepositoryOpException, MapperException {
        for(ID id : toDelete.keySet()){
            manager.doDBDelete(id);
        }
        toDelete.clear();
    }

    private void doDBAdd() throws RepositoryOpException, MapperException, SQLException {
        for(E el : toAdd.values()){
            manager.doDBAdd(el);
        }
        toAdd.clear();
    }

    private void doDBAddOriginal() throws RepositoryOpException, MapperException, SQLException {
        for(E el : toAddOriginal.values()){
            manager.doDBAddOriginal(el);
        }
        toAddOriginal.clear();
    }

    private void doDBUpdate() throws MapperException, RepositoryOpException, SQLException {
        for(E el : toUpdate.values()){
            manager.doDBUpdate(el,el.getId());
        }
        toUpdate.clear();
        Updated.clear();
    }

    @Override
    //if current page have max dimension, there're doing next page, else actualize current page with max dimension                                                  ---page---
    public void nextPage() throws RepositoryOpException, MapperException, SQLException {
        if(hasNextPage() && (0 == toDelete.size())) {
            this.currentPageNumber++;

            doDBAddOriginal();
            doDBAdd();
            doDBUpdate();
            doDBDelete();
            loadCurrentPage();

        }
        else{
            doDBAddOriginal();
            doDBAdd();
            doDBUpdate();
            doDBDelete();
            loadCurrentPage();
        }
    }

    @Override
    //if current page have max dimension, there're doing previous page, else actualize current page with max dimension                                                  ---page---
    public void previousPage() throws RepositoryOpException, MapperException, SQLException {
        if((toDelete.size() == 0) && hasPreviousPage()) {
            this.currentPageNumber--;
            doDBAddOriginal();
            doDBAdd();
            doDBUpdate();
            doDBDelete();

            loadCurrentPage();
        }
        else{
            doDBAddOriginal();
            doDBAdd();
            doDBUpdate();
            doDBDelete();
            loadCurrentPage();
        }
    }

    @Override
    public boolean hasNextPage(){
        if(this.size > this.pageSize * (this.currentPageNumber + 1)){
            return true;
        }
        return false;
    }

    @Override
    public boolean hasPreviousPage(){
        return (this.currentPageNumber > 0);
    }

    private void loadCurrentPage() throws MapperException, RepositoryOpException {
        Integer offset = this.currentPageNumber * this.pageSize;
        Collection<E> result = manager.getDBPartial(offset,this.pageSize);
        page.populate(result);
    }

}
