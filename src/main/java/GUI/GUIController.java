package GUI;

import Controller.*;
import GUI.Posts.GUIControllerPost;
import GUI.Tasks.GUIControllerTask;
import GUI.Users.GUIControllerUserRequest;
import com.itextpdf.text.DocumentException;
import domain.ConnectionWrapper;
import domain.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utils.exceptions.ControllerException;
import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Sergiu on 1/6/2017.
 */
public class GUIController{

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuSettings;
    @FXML
    private Menu menuEntity;
    @FXML
    private Menu menuExport;


    private MenuItem menuItemUsersReuqests;

    @FXML
    private MenuItem menuItemLogOut;
    @FXML
    private MenuItem menuItemExit;
    @FXML
    private MenuItem menuItemSaveToDb;
    @FXML
    private MenuItem menuItemPost;
    @FXML
    private MenuItem menuItemTask;
    @FXML
    private MenuItem menuItemExportPDF;
    @FXML
    private MenuItem menuItemExportCSV;


    @FXML
    private Pane currentScenePane;

    private GUIControllerPost guiControllerPost;
    private GUIControllerTask guiControllerTask;

    private Stage stageCurrent;

    private PostController postController;
    private TaskController taskController;

    private Parent scenePost;
    private Parent sceneTask;

    private FXMLLoader loaderPost;
    private FXMLLoader loaderTask;

    private ConnectionWrapper connection;
    private JobDescriptionController jobDescriptionController;
    private Stage parentStage;
    private User user;
    private UserController userController;

    private UserRequestController userRequestController;
    public void initComponents(Stage currentStage, PostController postController, TaskController taskController, JobDescriptionController jobDescriptionController, UserController userController, UserRequestController userRequestController, ConnectionWrapper connection, Stage parentStage, User user) throws IOException, MapperException, RepositoryOpException, ControllerException {

        this.stageCurrent = currentStage;
        this.connection = connection;
        loaderPost = new FXMLLoader(getClass().getResource("/GUIFxml/Posts/GUIPost.fxml"));
        this.scenePost = loaderPost.load();
        this.scenePost.getStylesheets().add(getClass().getResource("/css/post.css").toString());
        this.user = user;
        this.userRequestController = userRequestController;
        this.userController = userController;

        guiControllerPost = loaderPost.getController();
        this.postController = postController;
        this.jobDescriptionController = jobDescriptionController;
        guiControllerPost.initComponents(stageCurrent,this.scenePost,this.postController,this.jobDescriptionController,connection,user);

        loaderTask = new FXMLLoader(getClass().getResource("/GUIFxml/Tasks/GUITask.fxml"));
        this.sceneTask = this.loaderTask.load();
        guiControllerTask = loaderTask.getController();
        this.sceneTask.getStylesheets().add(getClass().getResource("/css/task.css").toString());
        this.taskController = taskController;
        guiControllerTask.initComponents(stageCurrent,this.sceneTask,this.taskController,this.jobDescriptionController,connection,user);

        this.parentStage = parentStage;
        if(user.isMasterUser()){
            menuItemUsersReuqests = new MenuItem();
            menuItemUsersReuqests.setText("Users requests.");
            menuItemUsersReuqests.setOnAction(event -> modifyUsersPrivilegesHandler());
            this.menuSettings.getItems().add(menuItemUsersReuqests);
        }
        else if(!user.isEndUser() && userRequestController.findById(user.getId())==null){
            menuItemUsersReuqests = new MenuItem();
            menuItemUsersReuqests.setText("Do request for privileges.");
            menuItemUsersReuqests.setOnAction(event -> doRequestForPrivilegesHandler());
            this.menuSettings.getItems().add(menuItemUsersReuqests);
        }


//        Stage logstage = new Stage();
//        FXMLLoader loaderLog = new FXMLLoader(getClass().getResource("/GUIFxml/Users/LogIn.fxml"));
//        Pane logpane = loaderLog.load();
//        GUIControllerUserLogIn controller = loaderLog.getController();
//        controller.initComponents(userController,logstage);
//        Scene logscene = new Scene(logpane);
//        logstage.setScene(logscene);
//        logstage.show();

        //setTaskSceneHandler();
        setPostSceneHandler();
    }

    @FXML
    private void setTaskSceneHandler(){
        if(currentScenePane!=sceneTask){
            currentScenePane.getChildren().clear();
            currentScenePane.getChildren().add(sceneTask);
            guiControllerPost.saveAll();
            guiControllerTask.saveAll();
            guiControllerPost.setMustSave(false);
            this.stageCurrent.getScene().getStylesheets().clear();
            this.stageCurrent.getScene().getStylesheets().add(getClass().getResource("/css/task.css").toString());
        }
    }

    @FXML
    private void setPostSceneHandler(){
        if(currentScenePane!=scenePost) {
            currentScenePane.getChildren().clear();
            currentScenePane.getChildren().add(scenePost);
            guiControllerTask.saveAll();
            guiControllerPost.saveAll();
            guiControllerTask.setMustSave(false);
            if(this.stageCurrent != null && this.stageCurrent.getScene() != null) {
                this.stageCurrent.getScene().getStylesheets().clear();
                this.stageCurrent.getScene().getStylesheets().add(getClass().getResource("/css/post.css").toString());
            }
        }
    }

    @FXML
    private void exitHandler() throws MapperException, FileNotFoundException, RepositoryOpException, SQLException {
        if(this.stageCurrent != null && this.stageCurrent.getScene() != null) {
            stageCurrent.getOnCloseRequest().handle(new WindowEvent(stageCurrent, WindowEvent.WINDOW_CLOSE_REQUEST));
            stageCurrent.close();
        }
    }

    @FXML
    private void saveToDbHandler() throws MapperException, FileNotFoundException, RepositoryOpException, SQLException {
        this.guiControllerTask.saveAll();
        this.guiControllerPost.saveAll();
        this.jobDescriptionController.saveAll();
        this.guiControllerPost.setMustSave(false);
        this.guiControllerTask.setMustSave(false);
        this.guiControllerPost.setReportsButtonOn();

    }

    @FXML
    private void logOutHandler(){
        this.parentStage.show();
       // this.stageCurrent.getOnCloseRequest();
        this.stageCurrent.close();
    }


    public GUIController(){
    }

    private void modifyUsersPrivilegesHandler()  {
        try {
            Stage requestStage = new Stage();
            FXMLLoader requestLoader = new FXMLLoader(getClass().getResource("/GUIFxml/Users/UsersRequests.fxml"));
            Pane logpane = requestLoader.load();
            GUIControllerUserRequest controller = requestLoader.getController();
            controller.initComponents(userRequestController,userController,stageCurrent,requestStage);
            Scene requestScene = new Scene(logpane);
            if(currentScenePane.getChildren().get(0) == scenePost){
                requestScene.getStylesheets().add(getClass().getResource("/css/post.css").toString());
            }
            else{
                requestScene.getStylesheets().add(getClass().getResource("/css/task.css").toString());
            }
            requestStage.setScene(requestScene);
            requestStage.setOnCloseRequest(e->stageCurrent.show());
            stageCurrent.hide();
            requestStage.show();
        } catch (IOException | MapperException | RepositoryOpException | SQLException e) {
            e.printStackTrace();
        }

    }

    private void doRequestForPrivilegesHandler(){
        try {
            userRequestController.add(user.getName(),user.getUserType(),user.getNextUserType());
            menuItemUsersReuqests.setDisable(true);
        } catch (MapperException | ControllerException | DuplicateEnityException | RepositoryOpException | SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void exportToPDFHandler(){
        if(currentScenePane.getChildren().get(0) == scenePost){
            try {
                this.guiControllerPost.exportToPDF();
            } catch (SQLException | DocumentException | MapperException | IOException | RepositoryOpException | ControllerException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
        else{
            try {
                this.guiControllerTask.exportToPDF();
            } catch (SQLException | DocumentException | MapperException | IOException | RepositoryOpException | ControllerException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void exportToCSVHandler(){
        if(currentScenePane.getChildren().get(0) == scenePost){
            this.guiControllerPost.exportToCSV();
        }
        else{
            this.guiControllerTask.exportToCSV();
        }
    }
}
