package utils.params_loader;

import utils.exceptions.ControllerException;

/**
 * Created by Sergiu on 1/5/2017.
 */
public interface IParamsLoader<E,ID> {
    E getElement(String... obj) throws ControllerException;
    ID getId(String... obj) throws ControllerException;
}
