package utils.DBManager;

import domain.ConnectionWrapper;
import domain.Post;
import utils.mapper.IMapper;

import java.sql.Connection;

/**
 * Created by Sergiu on 1/5/2017.
 */
public class PostDBManager extends DBAbstractManager<Post,Integer> {


    @Override
    public void setTableName(String tableName) {
        super.setTableName(tableName);
    }

    @Override
    public void setConnection(ConnectionWrapper cw) {
        super.setConnection(cw);
    }

    @Override
    public void setMapper(IMapper<Post, Integer> mapper) {
        super.setMapper(mapper);
    }

    public PostDBManager() {
    }

    public PostDBManager(ConnectionWrapper connection, IMapper<Post, Integer> mapper, String tableName) {
        super(connection, mapper, tableName);
    }
}
