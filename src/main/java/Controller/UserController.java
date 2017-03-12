package Controller;

import domain.User;
import repository.IDataBaseRepository;
import utils.exceptions.ControllerException;
import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;
import utils.params_loader.IParamsLoader;
import utils.params_loader.UserParamsLoader;

import java.io.FileNotFoundException;
import java.sql.SQLException;

/**
 * Created by Sergiu on 1/19/2017.
 */
public class UserController extends AbstractUserController<User> {
    private IDataBaseRepository<User, String> repository;
    private IParamsLoader<User,String> paramsLoader;

    public UserController() {
    }

    public UserController(IDataBaseRepository<User, String> repository) {
        super(repository, new UserParamsLoader());
    }
}
