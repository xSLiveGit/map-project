package Controller;

import domain.UserRequest;
import repository.IDataBaseRepository;
import utils.params_loader.IParamsLoader;
import utils.params_loader.UserRequestParamsLoader;

/**
 * Created by Sergiu on 1/20/2017.
 */
public class UserRequestController extends AbstractUserController<UserRequest> {
    private IDataBaseRepository<UserRequest, String> repository;
    private IParamsLoader<UserRequest,String> paramsLoader;


    public UserRequestController(IDataBaseRepository<UserRequest, String> repository) {
        super(repository, new UserRequestParamsLoader());
    }

    public UserRequestController() {
    }
}
