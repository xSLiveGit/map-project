package utils.params_loader;

import domain.User;
import utils.exceptions.ControllerException;

/**
 * Created by Sergiu on 1/19/2017.
 */
public class UserParamsLoader implements IParamsLoader<User,String> {
    @Override
    public User getElement(String... obj) throws ControllerException {
        if(obj.length != 3){
            throw new ControllerException("Invalid numbers of parameters. The method must have 3 parameters(userName:String,password:String,type:String");
        }
        else return new User(obj[0],obj[1],obj[2]);
    }

    @Override
    public String getId(String... obj) throws ControllerException {
        if(obj.length != 1){
            throw new ControllerException("Invalid numbers of parameters. The method must have 1 parameter(user:String");
        }
        return obj[0];
    }
}
