import Controller.*;
import domain.ConnectionWrapper;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import utils.DBManager.TaskDBManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import GUI.GUI;


/**
 * Created by Sergiu on 1/4/2017.
 */
public class Main extends Application {
//    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//    private static final String DB_URL = "jdbc:mysql://localhost/proiectdb";
//
//    //  Database credentials
//    private static final String USER = "root";
//    private static final String PASS = "password";
    private static ApplicationContext context;
    private static final Integer pageSize = 15;
    public static void main(String args[]) {
        launch(args);
    }

    private static <T> T getClassBean(Class<T> tClass) throws UnsupportedEncodingException {
        return (T) context.getBean(tClass.getSimpleName()) ;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        context = new ClassPathXmlApplicationContext(URLDecoder.decode(Main.class.getResource("/persistence/classes.xml").toString(),"UTF-8"));
        ConnectionWrapper cw = getClassBean(ConnectionWrapper.class);
        PostController postController = getClassBean(PostController.class);
        TaskController taskController = getClassBean(TaskController.class);
        JobDescriptionController jobDescriptionController =  getClassBean(JobDescriptionController.class);
        UserController userController =getClassBean(UserController.class);
        UserRequestController userRequestController = getClassBean(UserRequestController.class);
        GUI gui = new GUI(primaryStage,postController,taskController,jobDescriptionController,userController,userRequestController,cw);
        gui.start();
    }


}
