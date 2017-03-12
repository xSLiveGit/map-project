package utils.DBManager;

import domain.ConnectionWrapper;
import domain.Task;
import utils.mapper.IMapper;

import java.sql.Connection;

/**
 * Created by Sergiu on 1/5/2017.
 */
public class TaskDBManager extends DBAbstractManager<Task,Integer> {
    public TaskDBManager(ConnectionWrapper connection, IMapper<Task, Integer> mapper, String tableName) {
        super(connection, mapper, tableName);
    }

    @Override
    public void setTableName(String tableName) {
        super.setTableName(tableName);
    }

    @Override
    public void setConnection(ConnectionWrapper cw) {
        super.setConnection(cw);
    }

    @Override
    public void setMapper(IMapper<Task, Integer> mapper) {
        super.setMapper(mapper);
    }

    public TaskDBManager() {
    }
}
