package utils.params_loader;

import domain.Post;
import utils.exceptions.ControllerException;
import validator.StaticValidator;

/**
 * Created by Sergiu on 1/5/2017.
 */
public class PostParamsLoader implements IParamsLoader<Post,Integer>{

    public PostParamsLoader() { }

    private boolean validateKey(String key)
    {
        if(null == key){
            return false;
        }
        return StaticValidator.isIntegerNumber(key);
    }

    private void validateElementParams(String... obj) throws ControllerException {
        if(obj.length != 4)
        {
            throw new ControllerException("Invalid numbers of parameters. The method must have 3 parameters(id:int,name:string,type:string");
        }
        if(!StaticValidator.isIntegerNumber(obj[0]))
        {
            throw new ControllerException("First parameter must be non negative integer.");
        }
        if(!StaticValidator.isDoubleNumber(obj[3])){
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
    public Post getElement(String... obj) throws ControllerException {
        validateElementParams(obj);
        return new Post(Integer.parseInt(obj[0]), obj[1], obj[2],Double.parseDouble(obj[3]));
    }

    @Override
    public Integer getId(String... obj) throws ControllerException {
        validateIdParams(obj);
        return Integer.parseInt(obj[0]);
    }


}
