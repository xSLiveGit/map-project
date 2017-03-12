package utils.mapper;

import domain.User;
import domain.UserRequest;
import utils.exceptions.MapperException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergiu on 1/20/2017.
 */
public class MapperUserRequest implements IMapper<UserRequest,String> {
    @Override
    public UserRequest toObject(ResultSet row) throws SQLException, MapperException {
        if(null == row){
            throw new MapperException("Null object given");
        }
        String userName = row.getString("name");
        String oldType = row.getString("oldType");
        String newType = row.getString("newType");
        return new UserRequest(userName,oldType,newType);
    }

    @Override
    public Map<String, String> toMapOriginal(UserRequest object) throws MapperException {
        return null;
    }

    @Override
    public Map<String, String> toMap(UserRequest object) throws MapperException {
        if(null == object){
            throw new MapperException("Null object given");
        }
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("name", object.getId().toString() );
        properties.put("oldType",object.getOldType());
        properties.put("newType",object.getNewType());
        return properties;
    }

    @Override
    public String getIdPrototype() {
        return "name";
    }
}
