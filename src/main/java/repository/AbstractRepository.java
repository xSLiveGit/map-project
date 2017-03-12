package repository;

import domain.HasID;
import utils.exceptions.RepositoryOpException;
import validator.IValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sergiu on 1/4/2017.
 */
public abstract class AbstractRepository<E extends HasID<ID>,ID> implements IRepository<E,ID> {
    protected Map<ID,E> items;
    protected IValidator validator;

    public AbstractRepository(IValidator validator) {
        this.items = new HashMap<ID,E>();
        this.validator = validator;
    }

    /**
     * Description:
     * @return Iterable<E> for iterate it
     */
    @Override
    public Collection<E> getAll() {
        return items.values();
    }

    /**
     * Description: return elements with id searched, if item exist in items, return null else
     * @param id: Integer
     * @return
     */
    @Override
    public E findById(ID id) {
        if(items.containsKey(id))
            return items.get(id);
        return null;
    }

    /**
     * Description: delete 1 element with searched id, if it exist and return deleted element, return null if it doesn't exist 1 element with searched id
     * @param id: Integer
     * @return deleted items
     */

    @Override
    public E delete(ID id) {
        E el = this.findById(id);
        if(el!=null){
            items.remove(el.getId());
        }
        return el;
    }

    /**
     * Description: replace 1 object with searched id from items with element if eixst 1 object in items with searched id
     * @param element: E, E.id is fictive, E.id will be id from params
     * @param id: Integer
     * @throws Exception if id(parm) do not exist in items/ exist 1 item with element.id in items
     */
    @Override
    public void update(E element, ID id) throws RepositoryOpException {
        this.validator.validate(element);
        E el = this.findById(id);//search for item with searched id
        if(el == null){//if i have not what to update
            throw new RepositoryOpException("Nu exista o entitate cu acest id");
        }
        items.put(id,element);
    }


    /**
     * Description: add element in items, if it doesn't exist 1 obkect with same id in items collection
     * @param element
     * @throws Exception
     */
    @Override
    public void add(E element) throws RepositoryOpException{
        this.validator.validate(element);
        if(findById(element.getId()) != null){
            throw new RepositoryOpException("Exista o entitate in lista ce are acest id");
        }
        items.put(element.getId(),element);
    }

    @Override
    public void populate(Collection<E> collection) {
        this.items.clear();
        for(E e : collection){
            this.items.put(e.getId(),e);
        }
    }
    @Override
    public Integer countElements(){
        return items.values().size();
    }

    @Override
    public boolean exist(E element) {
        for(E el : items.values()){
            if(el.equals(element))
                return true;
        }
        return false;
    }

    @Override
    public void clearAll(){
        this.items.clear();
    }
}
