package GUI;

import Controller.*;
import GUI.Users.GUIControllerUserLogIn;
import domain.ConnectionWrapper;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Sergiu on 1/6/2017.
 */
public class GUI {
    private Stage logStage;
    private Scene logScene;
    private FXMLLoader GUILoader;
    private GUIControllerUserLogIn logInController;
    private PostController postController;
    private TaskController taskController;
    private Pane logpane;
    private JobDescriptionController jobDescriptionController;
    private ConnectionWrapper connection;
    private UserController userController;
    private UserRequestController userRequestController;

    public GUI(Stage mainStage, PostController postController, TaskController taskController, JobDescriptionController jobDescriptionController, UserController userController, UserRequestController userRequestController, ConnectionWrapper connection) throws Exception {
        this.logStage = mainStage;
        this.connection = connection;
        this.postController = postController;
        this.taskController = taskController;
        this.jobDescriptionController = jobDescriptionController;
        this.userController = userController;
        this.userRequestController = userRequestController;
//        GUILoader = new FXMLLoader(getClass().getResource("/GUIFxml/GUI.fxml"));
//        mainScene = GUILoader.load();
//        guiController = GUILoader.getController();
//        guiController.initComponents(mainStage,this.postController,this.taskController,this.jobDescriptionController,this.userController,this.connection);

        logStage = new Stage();
        FXMLLoader loaderLog = new FXMLLoader(getClass().getResource("/GUIFxml/Users/LogIn.fxml"));
        logpane = loaderLog.load();
        GUIControllerUserLogIn controller = loaderLog.getController();
        controller.initComponents(logStage,postController,taskController,jobDescriptionController,userController,userRequestController,connection);
        logScene = new Scene(logpane);
        this.logScene.getStylesheets().add(getClass().getResource("/css/log.css").toString());

    }
    public void start(){
        logStage.show();
        logStage.setTitle("Application");
        logStage.setScene(logScene);
        logStage.setResizable(false);
        logStage.show();
        logStage.setOnCloseRequest(e -> {//
            try {
                this.postController.saveAll();
                this.taskController.saveAll();
                this.jobDescriptionController.saveAll();
                this.userController.saveAll();
                this.userRequestController.saveAll();
                connection.getConnection().close();
                try{
                    if(connection.getConnection()!=null)
                        connection.getConnection().close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
                Platform.exit();
            } catch (SQLException | FileNotFoundException | MapperException | RepositoryOpException e1) {
                e1.printStackTrace();
            }
        });
    }
}
