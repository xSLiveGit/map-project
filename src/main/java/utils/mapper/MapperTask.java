package utils.mapper;

import domain.Task;
import utils.exceptions.MapperException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Sergiu on 1/4/2017.
 */
public class MapperTask implements IMapper<Task,Integer> {

    public Task toObject(ResultSet row) throws SQLException, MapperException  {
        if(null == row){
            throw new MapperException("Null object given");
        }
        Integer id = row.getInt("idTask");
        Integer duration  = row.getInt("durationTask");
        String type = row.getString("typeTask");
        String description = row.getString("descriptionTask");
        return new Task(id,type,duration,description);
    }

    @Override
    public Map<String, String> toMapOriginal(Task object) throws MapperException {
        return null;
    }

    public Map<String, String> toMap(Task object) throws MapperException {
        if(object == null){
            throw new MapperException("Null object given");
        }
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("idTask",object.getId().toString());
        properties.put("typeTask",object.getType());
        properties.put("durationTask",object.getDuration().toString());
        properties.put("descriptionTask",object.getDescription());
        return properties;
    }

    @Override
    public String getIdPrototype() {
        return "idTask";
    }
}
