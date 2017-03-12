package repository;

import domain.HasID;
import validator.IValidator;

/**
 * Created by Sergiu on 1/4/2017.
 */
public class PageRepository<E extends HasID<ID>,ID> extends AbstractRepository<E,ID> {
    private Boolean isFirst;
    public PageRepository(IValidator validator,Boolean isFirst) {
        super(validator);
        this.isFirst = isFirst;
    }
}
