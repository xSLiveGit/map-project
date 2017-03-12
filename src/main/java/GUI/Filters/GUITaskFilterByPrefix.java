package GUI.Filters;

import Controller.PostController;
import Controller.TaskController;
import GUI.JobDescription.GUIControllerPostAddTask;
import GUI.JobDescription.GUIControllerPostDeleteTask;
import GUI.JobDescription.GUIControllerTaskAddPost;
import GUI.JobDescription.GUIControllerTaskDeletePost;
import domain.Post;
import domain.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import repository.DBRepository;
import repository.IDataBaseRepository;
import utils.DBManager.PostDBManager;
import utils.DBManager.TaskDBManager;
import utils.exceptions.ControllerException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;
import utils.mapper.IMapper;
import utils.mapper.MapperPost;
import utils.mapper.MapperTask;
import validator.IValidator;
import validator.PostValidator;
import validator.TaskValidator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Sergiu on 1/11/2017.
 */
public class GUITaskFilterByPrefix extends GUIAbstractFilterByPrefix<Task> {
    @FXML
    TableColumn<Task,String>    columnType;
    @FXML
    TableColumn<Task,Integer>    columnDuration;
    @FXML
    TableColumn<Task,String>    columnDescription;

    private static String[] entityFields = {"Type","Duration","Description"};
    private final int pageSize = 12;
    private String currentDuration;
    private String currentType;
    private String currentDescription;

    public GUITaskFilterByPrefix(){}

    @Override
    protected void initController() throws RepositoryOpException, SQLException, MapperException, FileNotFoundException {
        IValidator<Task> val = new TaskValidator();
        IMapper<Task,Integer> mapperTask = new MapperTask();
        IDataBaseRepository<Task,Integer> repoTask = new DBRepository<Task,Integer>(connection,new TaskValidator(),mapperTask,new TaskDBManager(connection,mapperTask,"temptasks"),pageSize);
        repoTask.clearAll();
        syncTableWithColumns();
        this.newController = new TaskController(repoTask);
        updateModel();
        currentDescription="";
        currentType="";
        currentDuration="";
        this.currentStage.setOnCloseRequest(e->{
            try {
                jobDescriptionController.saveAll();
            } catch (MapperException | SQLException | RepositoryOpException | FileNotFoundException e1) {
                e1.printStackTrace();
            }
            parentStage.show();
        });
    }

    @Override
    protected void filterHandler() throws MapperException, SQLException, RepositoryOpException, ControllerException {
        Integer oldPage = this.controller.getCurrentPageNumber();
        this.controller.setCurrentPageNumber("1");
        this.newController.clearAll();
        do{
            List<Task> filtered = ((TaskController)this.controller).filterByPrefixFields(controller.getAllFromCurrentPage(),currentType,currentDuration,currentDescription);
            for(Task p : filtered){
                //System.out.println(p.getId() + " " + p);
                this.newController.addOriginal(p.getId().toString(),p.getType(),p.getDuration().toString(),p.getDescription());
            }
            if(controller.hasNextPage()){
                this.controller.nextPage();
            }
            else{
                break;
            }
        }while(true);
        this.controller.setCurrentPageNumber(((Integer)(oldPage+1)).toString());
        updateModel();
        updateNextBackButtons();
    }

    @Override
    public void updateModel() {
        System.out.println(newController);

        if(newController != null) {
            System.out.println(newController.getAllFromCurrentPage().size());
            model.clear();
            newController.getAllFromCurrentPage().forEach(el -> model.add(el));
        }
        System.out.println(model.size());
    }

    @Override
    protected void addOption() {
        Task p = table.getSelectionModel().getSelectedItem();
        if(null == p){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You must select a task");;
            alert.show();
        }
        else{
            try {
                Stage stage = new Stage();
                FXMLLoader loaderTaskAddPost = new FXMLLoader(getClass().getResource("/GUIFxml/Tasks/JobDescription/TaskAddPost.fxml"));
                Pane pane = loaderTaskAddPost.load();
                GUIControllerTaskAddPost guiControllerTaskAddPost = loaderTaskAddPost.getController();
                guiControllerTaskAddPost.initComponents(this.jobDescriptionController,stage,this.currentStage,connection,p.getId());
                Scene scene = new Scene(pane);
                scene.getStylesheets().add(getClass().getResource("/css/task.css").toString());

                stage.setScene(scene);
                stage.show();
                this.currentStage.hide();

            } catch (IOException | MapperException | RepositoryOpException | SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }

    public void getPageNr() throws SQLException {
        System.out.println(this.textFieldPage);
        String nr = this.textFieldPage.getText();

        String oldNumber = "";
        try {
            if(null != this.newController) {
                this.newController.setCurrentPageNumber(nr);
                oldNumber = nr;
                updateModel();
            }
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

    @Override
    protected void deleteOption() {
        Task p = table.getSelectionModel().getSelectedItem();
        if (null == p) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You must select a post");
            alert.show();
        } else {
            try {
                Stage stage = new Stage();
                FXMLLoader loaderTaskDeletePost = new FXMLLoader(getClass().getResource("/GUIFxml/Tasks/JobDescription/TaskDeletePost.fxml"));
                Pane pane = loaderTaskDeletePost.load();
                GUIControllerTaskDeletePost guiControllerTaskDeletePost = loaderTaskDeletePost.getController();
                guiControllerTaskDeletePost.initComponents(this.jobDescriptionController, stage, this.currentStage, connection, p.getId());
                Scene scene = new Scene(pane);
                scene.getStylesheets().add(getClass().getResource("/css/task.css").toString());

                stage.setScene(scene);
                stage.show();
                this.currentStage.hide();
            } catch (IOException | MapperException | RepositoryOpException | SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.setTitle("Error!");
                alert.show();
            }

        }
    }

    @Override
    protected void syncTableWithColumns() {
        columnDescription.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
        columnType.setCellValueFactory(new PropertyValueFactory<Task, String>("type"));
        columnDuration.setCellValueFactory(new PropertyValueFactory<Task, Integer>("duration"));
    }

    @Override
    protected void populateFilterFieldsComboBox() {
        ObservableList<String> obsList = FXCollections.observableArrayList();
        obsList.setAll(entityFields);
        comboBoxFilterFields.setItems(obsList);
        comboBoxFilterFields.getSelectionModel().select(1);
    }

    @Override
    protected void addOptionHandler() {

    }

    @Override
    protected void deleteOptionHandler() {

    }

    @Override
    protected void actualiseFieldsInformation() {
        String item = comboBoxFilterFields.getSelectionModel().getSelectedItem();
        if(item != null){
            switch (item){
                case "Duration":
                    currentDuration = textFieldFilterInfo.getText();
                    break;
                case "Type":
                    currentType = textFieldFilterInfo.getText();
                    break;
                case "Description":
                    currentDescription = textFieldFilterInfo.getText();
                    break;
                default:
                    break;
            }
        }
    }

    @FXML
    private void getFieldsInfo(){
        String item = comboBoxFilterFields.getSelectionModel().getSelectedItem();
        if(item != null){
            switch (item){
                case "Duration":
                    textFieldFilterInfo.setText(currentDescription);
                    break;
                case "Type":
                    textFieldFilterInfo.setText(currentType);
                    break;
                case "Description":
                    textFieldFilterInfo.setText(currentDescription);
                    break;
                default:
                    break;
            }
        }
    }



}
