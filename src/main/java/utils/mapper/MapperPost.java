package utils.mapper;

import domain.Post;
import utils.exceptions.MapperException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergiu on 1/4/2017.
 */
public class MapperPost implements IMapper<Post,Integer> {
    public Post toObject(ResultSet row) throws SQLException, MapperException {
        if(null == row){
            throw new MapperException("Invalid object given");
        }
        Integer id = row.getInt("idPost");
        Double salary  = row.getDouble("salaryPost");
        String type = row.getString("typePost");
        String name = row.getString("namePost");
        return new Post(id,name,type,salary);
    }

    @Override
    public Map<String, String> toMapOriginal(Post object) throws MapperException {
        return null;
    }

    public Map<String, String> toMap(Post object) throws MapperException {
        if(null == object){
            throw new MapperException("Null object given");
        }
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("idPost",object.getId().toString());
        properties.put("salaryPost",object.getSalary().toString());
        properties.put("typePost",object.getType());
        properties.put("namePost",object.getName());
        return properties;
    }

    @Override
    public String getIdPrototype() {
        return "idPost";
    }
}
