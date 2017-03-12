package validator;

import domain.User;
import domain.UserRequest;
import utils.exceptions.EntityValidatorException;

import java.util.ArrayList;

/**
 * Created by Sergiu on 1/20/2017.
 */
public class UserRequestValidator implements IValidator<UserRequest> {
    private ArrayList<String> errList;

    public UserRequestValidator() {
        errList = new ArrayList<>();
    }

    private void validateUserNewType(UserRequest u){
        if(! (u.getNewType().equals("Basic") || u.getNewType().equals("Power") || u.getNewType().equals("Admin"))){
            errList.add(String.format("Invalid type. Type must be: %s %s or %s.","Basic","Power","Admin"));
        }
    }

    private void validateUserOldType(UserRequest u){
        if(! (u.getOldType().equals("Basic") || u.getOldType().equals("Power") || u.getOldType().equals("Admin"))){
            errList.add(String.format("Invalid type. Type must be: %s %s or %s.","Basic","Power","Admin"));
        }
    }

    private void validateName(UserRequest u){
        if(u.getName().isEmpty()){
            errList.add("Username cannot be empty string.");
        }
    }

    @Override
    public void validate(UserRequest el) throws EntityValidatorException, EntityValidatorException {
        errList.clear();
        validateName(el);
        validateUserNewType(el);
        validateUserOldType(el);
        if(!errList.isEmpty()){
            throw  new EntityValidatorException(String.join("\n",errList));
        }
    }
}
