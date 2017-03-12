package utils.params_loader;

import domain.JobDescription;
import domain.Post;
import utils.exceptions.ControllerException;
import validator.StaticValidator;

/**
 * Created by Sergiu on 1/7/2017.
 */
public class JobDescriptionParamsLoader  implements IParamsLoader<JobDescription,Integer>{
    public JobDescriptionParamsLoader(){

    }

    private boolean validateKey(String key)
    {
        if(null == key){
            return false;
        }
        return StaticValidator.isIntegerNumber(key);
    }

    private void validateElementParams(String... obj) throws ControllerException {
        if(obj.length != 3)
        {
            throw new ControllerException("Invalid numbers of parameters. The method must have 3 parameters(id:int,idPost:int,idTask:int");
        }
        if(!StaticValidator.isIntegerNumber(obj[0]))
        {
            throw new ControllerException("First parameter must be non negative integer.");
        }
        if(!StaticValidator.isIntegerNumber(obj[1]))
        {
            throw new ControllerException("Second parameter must be non negative integer.");
        }
        if(!StaticValidator.isDoubleNumber(obj[2])){
            throw new ControllerException("Third parameter must be non negative integer.");
        }
    }

    private void validateIdParams(String... obj) throws ControllerException {
        if (obj.length != 1)
        {
            throw new ControllerException("Invalid numbers of parameters. The method must have 1 parameter(id:int");
        }
        if (!validateKey(obj[0]))
        {
            throw new ControllerException("First parameter must be non negative integer.");
        }
    }

    @Override
    public JobDescription getElement(String... obj) throws ControllerException {
        validateElementParams(obj);
        return new JobDescription(Integer.parseInt(obj[0]), Integer.parseInt(obj[1]), Integer.parseInt(obj[2]));
    }

    @Override
    public Integer getId(String... obj) throws ControllerException {
        validateIdParams(obj);
        return Integer.parseInt(obj[0]);
    }
}
