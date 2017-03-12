package validator;


import domain.Task;
import utils.exceptions.EntityValidatorException;

import java.util.ArrayList;

/**
 * Created by Sergiu on 10/20/2016.
 */
public class TaskValidator implements IValidator<Task> {
    private ArrayList<String> errList;
    public TaskValidator(){
        errList = new ArrayList<>();
    }

    private void validateId(int id){
//        if(id <=0 ){
//            errList.add("Id-ul trebuie sa pie un intreg pozitiv nenul.");
//        }
    }

    private void validateDuration(int duration){
        if(duration <= 0){
            errList.add("Durata trebuie sa fie un intreg pozitiv nenul.");
        }
    }

    private void validateDesription(String type){
        if(! (type.toLowerCase().equals("easy") || type.toLowerCase().equals("medium") || type.toLowerCase().equals("hard") || type.toLowerCase().equals("expert")) ){
            errList.add("Descrierea trebuie sa fie \"easy\" sau \"medium \" sau \"hard \" sau \"extra time \" sau \"expert \" ");
        }
    }

    @Override
    public void validate(Task el) throws EntityValidatorException {
        errList.clear();
        validateId( el.getId() );
        validateDesription(el.getType());
        validateDuration(el.getDuration());
        if(errList.size() > 0){
            throw new EntityValidatorException(String.join("\n",errList));
        }
    }
}
