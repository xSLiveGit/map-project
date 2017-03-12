package GUI.Users;

import Controller.*;
import GUI.Filters.GUITaskFilterByPrefix;
import GUI.GUIController;
import domain.ConnectionWrapper;
import domain.Task;
import domain.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utils.exceptions.ControllerException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import javax.xml.soap.Text;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Sergiu on 1/20/2017.
 */
public class GUIControllerUserLogIn {
    private UserController userController;
    private UserRequestController userRequestController;
    private Stage currentStage;
    private PostController postController;
    private TaskController taskController;
    private JobDescriptionController jobDescriptionController;
    private ConnectionWrapper connection;

    public GUIControllerUserLogIn() {
    }

    public void initComponents(Stage currentStage, PostController postController, TaskController taskController, JobDescriptionController jobDescriptionController, UserController userController, UserRequestController userRequestController, ConnectionWrapper connection) throws IOException {
        this.userController = userController;
        this.userRequestController = userRequestController;
        this.currentStage = currentStage;
        this.postController = postController;
        this.taskController = taskController;
        this.jobDescriptionController = jobDescriptionController;
        this.connection = connection;
        this.buttonLogIn.setId("my-log-button");
        this.buttonSignUp.setId("my-register-button");
    }

    @FXML
    TextField textFieldUserName;
    @FXML
    PasswordField textFieldPassword;

    @FXML
    Button buttonLogIn;
    @FXML
    Button buttonSignUp;

    public void logInHandler() throws IOException {
        String userName = textFieldUserName.getText();
        try {
            User user = userController.findById(userName);

            if(null != user){
                if(this.textFieldPassword.getText().equals(user.getPassword())){
                    FXMLLoader GUILoader = new FXMLLoader(getClass().getResource("/GUIFxml/GUI.fxml"));
                    BorderPane pane = GUILoader.load();
                    Scene scene = new Scene(pane);
                    scene.getStylesheets().add(getClass().getResource("/css/post.css").toString());
                    GUIController guiController = GUILoader.getController();
                    Stage stage = new Stage();
                    guiController.initComponents(stage,this.postController,this.taskController,this.jobDescriptionController,this.userController,this.userRequestController,this.connection,this.currentStage,user);
                    stage.setTitle("Application");
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.setTitle("User: " + user.getId() + " | Type: " + user.getUserType());
                    stage.setOnCloseRequest(e -> {//
                        try {
                            this.postController.saveAll();
                            this.taskController.saveAll();
                            this.jobDescriptionController.saveAll();
                            this.userController.saveAll();
                            connection.getConnection().close();
                            currentStage.show();
                            try{
                                if(connection!=null)
                                    connection.getConnection().close();
                            }catch(SQLException se){
                                se.printStackTrace();
                            }
                            Platform.exit();
                        } catch (SQLException | FileNotFoundException | MapperException | RepositoryOpException e1) {
                            e1.printStackTrace();
                        }
                    });
                    currentStage.hide();
                    stage.show();
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Incorrect username or password");
                    alert.setTitle("Error");
                    alert.showAndWait();
                }
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Incorrect username or password");
                alert.setTitle("Error");
                alert.showAndWait();
            }
        } catch (MapperException | ControllerException | RepositoryOpException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.setTitle("Error");
            alert.showAndWait();
        }
    }

    public void signUpHandler() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loaderSignUp = new FXMLLoader(getClass().getResource("/GUIFxml/Users/SignUp.fxml"));
        Pane pane = loaderSignUp.load();
        GUIControllerUserSignUp controller = loaderSignUp.getController();
        controller.initComponents(this.userController,stage,currentStage);
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getResource("/css/log.css").toString());
        stage.setScene(scene);
        stage.setOnCloseRequest(e->currentStage.show());
        stage.show();
        currentStage.hide();
    }
}
