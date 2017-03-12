package utils.DBManager;

import domain.ConnectionWrapper;
import domain.User;
import domain.UserRequest;
import utils.mapper.IMapper;

import java.sql.Connection;

/**
 * Created by Sergiu on 1/20/2017.
 */
public class UserRequestDBManager  extends DBAbstractManager<UserRequest,String> {
    public UserRequestDBManager(ConnectionWrapper connection, IMapper<UserRequest, String> mapper, String tableName) {
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
    public void setMapper(IMapper<UserRequest, String> mapper) {
        super.setMapper(mapper);
    }

    public UserRequestDBManager() {
    }
}
