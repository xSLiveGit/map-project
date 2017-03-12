package validator;

import domain.Post;
import utils.exceptions.EntityValidatorException;

import java.util.ArrayList;

/**
 * Created by Sergiu on 10/17/2016.
 */
public class PostValidator implements IValidator<Post> {
    private ArrayList<String> errList;
    public PostValidator(){
        errList = new ArrayList<>();
    }

    private void validateId(Integer id){
//        if(id <=0){
//            errList.add("Id invalid.Id-ul trebuie sa fie un numar intreg pozitiv.");
//        }
    }

    private void validateName(String name){
        if(!(name.toLowerCase().equals("manager") || name.toLowerCase().equals("director") || name.toLowerCase().equals("angajat") || name.toLowerCase().equals("sef"))){
            errList.add("Nu ai introdus un post valid. Variante corect: \"Manager\"; \"Director\"; \"Angajat\"; \"Sef\".");
        }
    }

    private void validateType(String post){
        if(post.equals("")){
            errList.add("Numele nu poate fi vid.");
            return;
        }
        if(!(post.toLowerCase().equals("part time") || post.toLowerCase().equals("full time") || post.toLowerCase().equals("extra time") || post.toLowerCase().equals("0 time")) ){
            this.errList.add("Nu ai introdus un post valid. Variante corect: \"\"Part time\"\"; \"Full time\"; \"Extra time\"; \"0 time\".");
        }
    }

    private void validateSalary(Double salary){
        if(salary <= 0.0){
            this.errList.add("Salary must be real positive number");
        }
    }

    @Override
    public void validate(Post el) throws EntityValidatorException {
        errList.clear();
        validateId(el.getId());
        validateName(el.getName());
        validateType(el.getType());
        if(errList.size() > 0){
            throw new EntityValidatorException(String.join("\n",errList));
        }
    }
}
