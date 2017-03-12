package utils.DBManager;

import domain.ConnectionWrapper;
import domain.JobDescription;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;
import utils.mapper.IMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Sergiu on 1/5/2017.
 */
public class JobDescriptionDBManager extends DBAbstractManager<JobDescription,Integer> implements IDBManager<JobDescription,Integer> {
    public JobDescriptionDBManager(ConnectionWrapper connection, IMapper<JobDescription, Integer> mapper, String tableName) {
        super(connection, mapper, tableName);
    }//must be add and update verification if id exists

    public JobDescriptionDBManager() {
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
    public void setMapper(IMapper<JobDescription, Integer> mapper) {
        super.setMapper(mapper);
    }

    @Override
    public void doDBAdd(JobDescription element) throws RepositoryOpException, MapperException, SQLException {
        verifyTheseId(element.getPostId(),element.getTaskId());
        verifyDuplicate(element.getPostId(),element.getTaskId());
        super.doDBAdd(element);
    }

    private void verifyDuplicate(Integer postId,Integer taskId) throws SQLException, RepositoryOpException {
        String query = String.format("SELECT t.* FROM `%s` t WHERE t.idPost=? AND t.idTask=?", this.tableName);
        PreparedStatement stmt = this.connection.prepareStatement(query);
        stmt.setString(1, postId.toString());
        stmt.setString(2, taskId.toString());
        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        if (rs.next()){
            throw new RepositoryOpException("There exist a pair with these postID and taskId");
        }
    }

    private void verifyTheseId(Integer postId,Integer taskId) throws SQLException, RepositoryOpException {
        String query = String.format("SELECT t.* FROM `%s` t WHERE t.idPost=?", "posts");
        PreparedStatement stmt = this.connection.prepareStatement(query);
        stmt.setString(1, postId.toString());
        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        while(!rs.next()){
            throw new RepositoryOpException("There is no post with this id.");
        }
        rs.close();

        query = String.format("SELECT * FROM `%s` WHERE idTask = %d", "tasks", taskId);
        stmt = this.connection.prepareStatement(query);
        stmt.execute();
        rs = stmt.getResultSet();
        if(!rs.next()){
            throw new RepositoryOpException("There is no task with this id.");
        }
        rs.close();
    }
}
