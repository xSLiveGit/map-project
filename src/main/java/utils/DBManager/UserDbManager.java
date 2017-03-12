package utils.DBManager;

import domain.ConnectionWrapper;
import domain.User;
import utils.mapper.IMapper;

import java.sql.Connection;

/**
 * Created by Sergiu on 1/19/2017.
 */
public class UserDbManager extends DBAbstractManager<User,String> {
    public UserDbManager(ConnectionWrapper connection, IMapper<User, String> mapper, String tableName) {
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
    public void setMapper(IMapper<User, String> mapper) {
        super.setMapper(mapper);
    }


}
