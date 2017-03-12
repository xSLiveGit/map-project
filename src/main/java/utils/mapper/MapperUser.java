package utils.mapper;

import domain.User;
import utils.exceptions.MapperException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergiu on 1/19/2017.
 */
public class MapperUser implements IMapper<User,String> {
    @Override
    public User toObject(ResultSet row) throws SQLException, MapperException {
        if(null == row){
            throw new MapperException("Null object given");
        }
        String userName = row.getString("name");
        String userPassword = row.getString("password");
        String userType = row.getString("type");
        return new User(userName,userPassword,userType);
    }

    @Override
    public Map<String, String> toMapOriginal(User object) throws MapperException {
        return null;
    }

    @Override
    public Map<String, String> toMap(User object) throws MapperException {
        if(null == object){
            throw new MapperException("Null object given");
        }
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("name",object.getId());
        properties.put("password",object.getPassword());
        properties.put("type",object.getUserType());
        return properties;
    }

    @Override
    public String getIdPrototype() {
        return "name";
    }
}
