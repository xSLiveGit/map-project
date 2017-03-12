package utils.DBManager;

import domain.ConnectionWrapper;
import domain.HasID;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;
import utils.mapper.IMapper;

import java.sql.*;
import java.util.*;

/**
 * Created by Sergiu on 1/5/2017.
 */
public abstract class DBAbstractManager<E extends HasID<ID>,ID> implements IDBManager<E,ID> {
    protected Connection connection;
    protected IMapper<E,ID> mapper;
    protected String tableName;

    public DBAbstractManager(){

    }
    public DBAbstractManager(ConnectionWrapper connection, IMapper<E,ID> mapper, String tableName){
        this.connection = connection.getConnection();
        this.mapper = mapper;
        this.tableName = tableName;

    }


    public void setTableName(String tableName){
        this.tableName = tableName;
    }

    public void setConnection(ConnectionWrapper cw){
        this.connection = cw.getConnection();
    }

    public void setMapper(IMapper<E,ID> mapper){
        this.mapper = mapper;
    }

    @Override
    public void doDBUpdate(E element,ID id) throws MapperException, RepositoryOpException, SQLException {
        String fields;
        String codition;
        String querry;

        Map<String, String> properties = this.mapper.toMap(element);
        properties.remove(this.mapper.getIdPrototype());
        properties.remove(id.toString());

        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();

        for(Map.Entry<String,String> pair : properties.entrySet()){
            values.add(pair.getValue());
            keys.add(pair.getKey() + "=?");
        }

        String columns = String.join(", ",keys);
        String where = mapper.getIdPrototype()+"=?";
        String query = String.format("UPDATE `%s` SET %s WHERE %s", this.tableName, columns ,where);

        boolean fromAdd = false;
        PreparedStatement stmt = this.connection.prepareStatement(query);
        for(int i = 0; i < values.size(); i++) {
            stmt.setString(i + 1, values.get(i));
        }
        stmt.setString(values.size()+1,id.toString());
        try {
            fromAdd = true;
            stmt.execute();
        }catch (SQLException e){
            if(!fromAdd){
                throw e;
            }
            else{
                throw new RepositoryOpException("Duplicate entity error.");
            }
        }

    }

    @Override
    public E doDBDelete(ID id) throws MapperException, RepositoryOpException {
        E el = null;
        PreparedStatement stmt = null;
        String condition ="";
        condition += mapper.getIdPrototype();
        condition += " = ";
        condition += "'" + id.toString() + "'";
        String query = String.format("DELETE FROM `%s`  WHERE %s", this.tableName,condition);
        try {
            el = doDBFindById(id);
            if(null == el) {
                return el;
            }
            stmt = this.connection.prepareStatement(query);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return el;
    }

    @Override
    public void doDBAdd(E element) throws RepositoryOpException, MapperException, SQLException {
        Map<String, String> props = this.mapper.toMap(element);
        props.remove(mapper.getIdPrototype());
        Set<String> keys = props.keySet();
        ArrayList<String> values = new ArrayList<>();

        for(String key : keys) {
            values.add(props.get(key));
        }

        String columns = String.join(", ", props.keySet());
        String vals = (new String(new char[props.size() - 1]).replace("\0", "?, ")) + "?";

        String query = String.format("INSERT INTO `%s` (%s) VALUES (%s)", this.tableName, columns, vals);
        boolean fromAdd = false;
        PreparedStatement stmt = this.connection.prepareStatement(query);
        for(int i = 0; i < values.size(); i++) {
            stmt.setString(i + 1, values.get(i));
        }
        try {
            fromAdd = true;
            stmt.execute();
        }catch (SQLException e){
            if(!fromAdd){
                throw e;
            }
            else{
               throw new RepositoryOpException("Duplicate entity error.");
            }
        }
    }

    @Override
    public E doDBFindById(ID id) throws MapperException, RepositoryOpException {
        String query = String.format("SELECT t.* FROM `%s` t WHERE t.`%s`=?", this.tableName,mapper.getIdPrototype());
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            stmt.setString(1, id.toString());
            stmt.execute();

            ResultSet rs = stmt.getResultSet();
            E obj = null;

            if (rs.next()) {
                obj = this.mapper.toObject(rs);
            }
            rs.close();
            return obj;

        } catch (SQLException e) {
            throw new RepositoryOpException(e.getMessage());
        }
    }

    @Override
    public Collection<E> doDBGetAll() throws MapperException, RepositoryOpException {

        String query = String.format("SELECT t.* FROM `%s` t", this.tableName);
        Collection<E> result = new ArrayList<>();
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            stmt.execute();

            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                result.add(this.mapper.toObject(rs));
            }
            rs.close();

            return result;
        } catch (SQLException e) {
            throw new RepositoryOpException(e.getMessage());
        }
    }

    @Override
    public Collection<E> getDBPartial(Integer offset,Integer quantity) throws MapperException, RepositoryOpException {
        Collection<E> result = new ArrayList<E>();
        String query = String.format("SELECT * FROM `%s` LIMIT %d, %d", tableName, offset, quantity);
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            stmt.execute();

            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                E el = this.mapper.toObject(rs);
                result.add(el);
            }
            rs.close();
            return result;
        } catch (SQLException e) {
            throw new RepositoryOpException(e.getMessage());
        }
    }

    @Override
    public Integer getDBSize() {
        Integer size = 0;
        try {
            String query = String.format("SELECT COUNT(*) as size FROM `%s`", this.tableName);
            PreparedStatement stmt = this.connection.prepareStatement(query);
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                size = rs.getInt("size");
            }
            rs.close();
        } catch (SQLException e) {
        }
        return size;
    }

    @Override
    public void clearAll() throws SQLException {
        String condition = mapper.getIdPrototype();
        condition+=" > 0";
        String query = String.format("DELETE FROM `%s`  WHERE %s", this.tableName,condition);
        PreparedStatement stmt = this.connection.prepareStatement(query);
        stmt.execute();
    }

    @Override
    public void doDBAddOriginal(E element) throws MapperException, RepositoryOpException, SQLException {
        Map<String, String> props = this.mapper.toMap(element);
        Set<String> keys = props.keySet();
        ArrayList<String> values = new ArrayList<>();

        for(String key : keys) {
            values.add(props.get(key));
        }

        String columns = String.join(", ", props.keySet());
        String vals = (new String(new char[props.size() - 1]).replace("\0", "?, ")) + "?";

        String query = String.format("INSERT INTO `%s` (%s) VALUES (%s)", this.tableName, columns, vals);
        boolean fromAdd = false;
        PreparedStatement stmt = this.connection.prepareStatement(query);
        for(int i = 0; i < values.size(); i++) {
            stmt.setString(i + 1, values.get(i));
        }
        try {
            fromAdd = true;
            stmt.execute();
        }catch (SQLException e){
            if(!fromAdd){
                throw e;
            }
            else{
                throw new RepositoryOpException("Duplicate entity error.");
            }
        }
    }

    @Override
    public boolean exist(E element) throws MapperException, SQLException {
        Map<String, String> props = this.mapper.toMap(element);
        props.remove(mapper.getIdPrototype());
        String query = String.format("SELECT * FROM `%s` WHERE",this.tableName);
        ArrayList<String> keys = new ArrayList<String>();
        props.keySet().forEach(e->keys.add(e));
        for(Integer i=0;i< keys.size()-1;i++){
            query += " " + keys.get(i) + "= '"+ props.get(keys.get(i)) + "' AND ";
        }
        query+= " " + keys.get(keys.size()-1) + "= '" + props.get(keys.get(keys.size()-1)) + "'" ;
        PreparedStatement stmt = this.connection.prepareStatement(query);
        stmt.execute();
        ResultSet rs = stmt.getResultSet();
        if (rs.next()) {
            rs.close();
            return true;
        }
        rs.close();
        return false;
    }
}
