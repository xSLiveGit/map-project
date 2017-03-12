package utils.mapper;

import domain.JobDescription;
import utils.exceptions.MapperException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergiu on 1/5/2017.
 */
public class MapperJobDescription implements IMapper<JobDescription,Integer> {
    @Override
    public JobDescription toObject(ResultSet row) throws SQLException, MapperException {
        if(null == row){
            throw new MapperException("Invalid object given");
        }
        Integer id = row.getInt("id");
        Integer idPost = row.getInt("idPost");
        Integer idTask = row.getInt("idTask");
        return new JobDescription(id,idPost,idTask);
    }

    @Override
    public Map<String, String> toMapOriginal(JobDescription object) throws MapperException {
        return null;
        //@todo
    }

    @Override
    public Map<String, String> toMap(JobDescription object) throws MapperException {
        if(null == object){
            throw new MapperException("Null object given");
        }
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("id",object.getId().toString());
        properties.put("idPost",object.getPostId().toString());
        properties.put("idTask",object.getTaskId().toString());
        return properties;
    }

    @Override
    public String getIdPrototype() {
        return "id";
    }
}
