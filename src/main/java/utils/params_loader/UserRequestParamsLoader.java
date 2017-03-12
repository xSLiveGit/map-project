package utils.params_loader;

import domain.User;
import domain.UserRequest;
import utils.exceptions.ControllerException;

/**
 * Created by Sergiu on 1/20/2017.
 */
public class UserRequestParamsLoader implements IParamsLoader<UserRequest,String> {
    @Override
    public UserRequest getElement(String... obj) throws ControllerException {
        if(obj.length != 3){
            throw new ControllerException("Invalid numbers of parameters. The method must have 3 parameters(userName:String,oldType:String,newType:String");
        }
        else return new UserRequest(obj[0],obj[1],obj[2]);
    }

    @Override
    public String getId(String... obj) throws ControllerException {
        if(obj.length != 1){
            throw new ControllerException("Invalid numbers of parameters. The method must have 1 parameter(user:String");
        }
        return obj[0];
    }
}
