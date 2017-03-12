package GUI.JobDescription;

import Controller.JobDescriptionController;
import Controller.TaskController;
import GUI.GUIController;
import domain.ConnectionWrapper;
import domain.JobDescription;
import domain.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import repository.DBRepository;
import repository.IDataBaseRepository;
import utils.DBManager.TaskDBManager;
import utils.exceptions.ControllerException;
import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;
import utils.mapper.IMapper;
import utils.mapper.MapperTask;
import validator.IValidator;
import validator.TaskValidator;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created by Sergiu on 1/7/2017.
 */
public class GUIControllerPostAddTask {
    private TaskController taskController;
    private JobDescriptionController jobDescriptionController;
    private ObservableList<Task> model;
    private final Integer pageSize = 10;
    private Stage parentStage;
    private Integer currentIdPost;
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonBack;
    @FXML
    private TextField textFieldPage;
    @FXML
    private Button buttonSelect;
    @FXML
    private TableView<Task> tableViewTask;
    @FXML
    private TableColumn<Task,Integer> columnDurationTask;
    @FXML
    private TableColumn<Task,String> columnTypeTask;
    @FXML
    private TableColumn<Task,String> columnDescriptionTask;
    String oldNumber;

    ConnectionWrapper connection;
    private Stage currentStage;

    public GUIControllerPostAddTask(){}

    public void initComponents(JobDescriptionController jobDescriptionController, Stage currentStage,Stage parentStage, ConnectionWrapper connection,Integer currentIdPost) throws MapperException, FileNotFoundException, RepositoryOpException, SQLException {
        this.parentStage = parentStage;
        this.connection = connection;
        this.jobDescriptionController = jobDescriptionController;
        this.currentStage = currentStage;
        this.currentIdPost = currentIdPost;

        setColumnInfo();
        createTaskController();
        model = FXCollections.observableArrayList(((Collection<Task>) taskController.getAllFromCurrentPage()));
        this.tableViewTask.setItems(model);

        this.currentStage.setOnCloseRequest(c-> {
                try {
                    taskController.clearAll();

                } catch (SQLException | MapperException | RepositoryOpException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText(e.getMessage());
                    alert.setTitle("Error!");
                    alert.show();
                }
            try {
                jobDescriptionController.saveAll();
            } catch (MapperException | SQLException | RepositoryOpException | FileNotFoundException e) {
                e.printStackTrace();
            }
            parentStage.show();
        });
        oldNumber = "0";
    }

    private void createTaskController() throws FileNotFoundException, RepositoryOpException, MapperException, SQLException {
        IValidator<Task> val = new TaskValidator();
        IMapper<Task,Integer> mapperTask = new MapperTask();
        IDataBaseRepository<Task,Integer> repoTask = new DBRepository<Task,Integer>(connection,new TaskValidator(),mapperTask,new TaskDBManager(connection,mapperTask,"temptasks"),pageSize);
        repoTask.clearAll();
        this.taskController = new TaskController(repoTask);
        try {
            Collection<Task> col = jobDescriptionController.getAllTaskWithoutPostId(currentIdPost.toString());
            for(Task t : col){
                taskController.addOriginal(t.getId().toString(),t.getType(),t.getDuration().toString(),t.getDescription());
            }
        } catch (ControllerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.setTitle("Error!");
            alert.show();
        }
        taskController.saveAll();

    }

    private void setColumnInfo(){
        columnDurationTask.setCellValueFactory(new PropertyValueFactory<Task, Integer>("duration"));
        columnTypeTask.setCellValueFactory(new PropertyValueFactory<Task, String>("type"));
        columnDescriptionTask.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
    }

    private void updateModel() {
        model.setAll((Collection<Task>) taskController.getAllFromCurrentPage());
        tableViewTask.refresh();
        if(taskController.hasNextPage()){
            this.buttonNext.setDisable(false);
        }
        else{
            this.buttonNext.setDisable(true);
        }
        if(taskController.hasPreviousPage()){
            this.buttonBack.setDisable(false);
        }
        else{
            this.buttonBack.setDisable(true);
        }
        this.textFieldPage.setText(((Integer)(taskController.getCurrentPageNumber()+1)).toString());
    }


    public void selectHandler() throws FileNotFoundException {

        if(this.tableViewTask.getSelectionModel().getSelectedItem() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You haven't selected any items");
            alert.setTitle("Warning!");
            alert.show();
        }
        else{
            Task t = this.tableViewTask.getSelectionModel().getSelectedItem();
            try {

                this.jobDescriptionController.add("1",this.currentIdPost.toString(),t.getId().toString());
                this.taskController.delete(t.getId().toString());
                updateModel();
                jobDescriptionController.saveAll();
            } catch (MapperException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.setTitle("Error!");
                alert.show();
            } catch (RepositoryOpException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.setTitle("Error!");
                alert.show();
            } catch (ControllerException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.setTitle("Error!");
                alert.show();
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.setTitle("Error!");
                alert.show();
            } catch (DuplicateEnityException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.setTitle("Error!");
                alert.show();
            }
        }
    }

    public void getPageNrHandler() throws SQLException {
        String nr = this.textFieldPage.getText();
        try {
            taskController.setCurrentPageNumber(nr);
            oldNumber = nr;
            updateModel();
        } catch (RepositoryOpException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            textFieldPage.setText(oldNumber);
            alert.show();
        } catch (ControllerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            textFieldPage.setText(oldNumber);
            alert.show();
        } catch (MapperException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            textFieldPage.setText(oldNumber);
            alert.show();
        }
    }

    public void nextButtonHandler() {
        if(taskController.hasNextPage()){
            try {
                taskController.nextPage();
            } catch (MapperException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            } catch (RepositoryOpException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            }
            updateModel();
        }
    }

    public void backButtonHandler() {
        if(taskController.hasPreviousPage()){
            try {
                taskController.previousPage();
                updateModel();
            } catch (MapperException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            } catch (RepositoryOpException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }


}
