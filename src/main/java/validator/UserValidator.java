package validator;

import domain.User;
import utils.exceptions.EntityValidatorException;

import java.util.ArrayList;

/**
 * Created by Sergiu on 1/19/2017.
 */
public class UserValidator implements IValidator<User> {
    private ArrayList<String> errList;
    private final Integer minUserLength = 6;
    private final Integer maxUserLength = 20;
    private final Integer minPassLength = 10;
    private final Integer maxPassLength = 20;
    public UserValidator() {
        this.errList = new ArrayList<>();
    }

    boolean isLetter(char c) {
        return ( c >= 'a' && c <= 'z') || (c>= 'A' && c<='Z');
    }

    boolean isDigit(char c){
        return c>='0' && c<='9';
    }

    boolean isAcceptedChar(char c){
        return c== '.' || c == '_';
    }

    public void validateUserName(User u){
        if(u.getId().length() < minUserLength) {
            errList.add(String.format("Username length is too small.You must add password with length between %s and %s",minUserLength,maxUserLength));

            return;
        }
        if(u.getId().length() > maxUserLength){
            errList.add(String.format("Username length is too high.You must add password with length between %s and %s",minUserLength,maxUserLength));
            return;
        }

        for(char c: u.getId().toCharArray()) {
            if(!isLetter(c) && !isDigit(c) && !isAcceptedChar(c)) {
                errList.add("Your username contain invalid characters. The user can contain letter, digits, \'-\' or \'_\'");
                return;
            }
        }

    }

    private void validateUserPassword(User u) {
        if(u.getPassword().length() < minPassLength) {
            errList.add(String.format("Password length is too small.You must add password with length between %s and %s",minPassLength,maxPassLength));
            return;
        }
        if(u.getPassword().length() > maxPassLength){
            errList.add(String.format("Password length is too high.You must add password with length between %s and %s",minPassLength,maxPassLength));
            return;
        }

        for(char c: u.getId().toCharArray()) {
            if(!isLetter(c) && !isDigit(c) && !isAcceptedChar(c)) {
                errList.add("Your password contain invalid characters. The password can contain letter, digits, \'-\' or \'_\'");
                return;
            }
        }
    }

    private void validateUserType(User u){
        if(! (u.getUserType().equals("Basic") || u.getUserType().equals("Power") || u.getUserType().equals("Admin"))){
            errList.add(String.format("Invalid type. Type must be: %s %s or %s","Basic","Power","Admin"));
        }
    }

    @Override
    public void validate(User el) throws EntityValidatorException, EntityValidatorException {
        errList.clear();
        validateUserName(el);
        validateUserPassword(el);
        validateUserType(el);
        if(!errList.isEmpty()){
            throw  new EntityValidatorException(String.join("\n",errList));
        }
    }


}
