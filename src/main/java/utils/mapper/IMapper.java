package utils.mapper;


import domain.HasID;
import utils.exceptions.MapperException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by Sergiu on 1/4/2017.
 */
public interface IMapper<T extends HasID<ID>,ID> {
    T toObject(ResultSet row) throws SQLException, MapperException ;
    Map<String, String> toMapOriginal(T object) throws MapperException ;
    Map<String, String> toMap(T object) throws MapperException ;
    String getIdPrototype();
}
