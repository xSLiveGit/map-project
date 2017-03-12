package validator;


import domain.JobDescription;
import utils.exceptions.EntityValidatorException;

import java.util.ArrayList;

/**
 * Created by Sergiu on 11/30/2016.
 */

public class JobDescriptionValidator implements IValidator<JobDescription> {
    ArrayList<String> errList;
    private void validateId(Integer id){
//        if(id <= 0){
//            errList.add("Id-ul trebuie sa fie pozitiv");
//        }

    }
    @Override
    public void validate(JobDescription el) throws EntityValidatorException {
        errList = new ArrayList<>();
        validateId(el.getId());
        if(!errList.isEmpty()){
            throw new EntityValidatorException(String.join("\n",errList));
        }
    }
}
