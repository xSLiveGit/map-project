package domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by Sergiu on 1/20/2017.
 */
public class ConnectionWrapper {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/proiectdb";

    //  Database credentials
    private static final String USER = "root";
    private static final String PASS = "password";


    private Connection connection;

    public ConnectionWrapper() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        System.out.println("Connecting to database...");
        this.connection = DriverManager.getConnection(DB_URL,USER,PASS);
    }

    public Connection getConnection() {
        return connection;
    }
}
