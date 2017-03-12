package validator;


import utils.exceptions.EntityValidatorException;

/**
 * Created by Sergiu on 10/17/2016.
 */
public interface IValidator<E> {
    public void validate(E el) throws EntityValidatorException, EntityValidatorException;
}
