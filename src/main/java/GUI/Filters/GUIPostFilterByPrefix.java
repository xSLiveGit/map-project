package GUI.Filters;

import Controller.PostController;
import GUI.JobDescription.GUIControllerPostAddTask;
import GUI.JobDescription.GUIControllerPostDeleteTask;
import domain.Post;
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
import utils.exceptions.ControllerException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;
import utils.mapper.IMapper;
import utils.mapper.MapperPost;
import validator.IValidator;
import validator.PostValidator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Sergiu on 1/11/2017.
 */
public class GUIPostFilterByPrefix extends GUIAbstractFilterByPrefix<Post> {
    @FXML
    TableColumn<Post,String>    columnName;
    @FXML
    TableColumn<Post,String>    columnType;
    @FXML
    TableColumn<Post,Double>    columnSalary;

    private static String[] entityFields = {"Name","Type","Salary"};
    private final int pageSize = 12;
    private String currentName;
    private String currentType;
    private String currentSalary;

    public GUIPostFilterByPrefix(){}

    @Override
    protected void initController() throws RepositoryOpException, SQLException, MapperException, FileNotFoundException {
        IValidator<Post> val = new PostValidator();
        IMapper<Post,Integer> mapperPost = new MapperPost();
        IDataBaseRepository<Post,Integer> repoPost = new DBRepository<Post,Integer>(connection,new PostValidator(),mapperPost,new PostDBManager(connection,mapperPost,"tempposts"),pageSize);
        repoPost.clearAll();
        syncTableWithColumns();
        this.newController = new PostController(repoPost);
        updateModel();
        currentName="";
        currentType="";
        currentSalary="";
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
            List<Post> filtered = ((PostController)this.controller).filterByPrefixFields(controller.getAllFromCurrentPage(),currentName,currentType,currentSalary);
            for(Post p : filtered){
                //System.out.println(p.getId() + " " + p);
                this.newController.addOriginal(p.getId().toString(),p.getName(),p.getType(),p.getSalary().toString());
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
        Post p = table.getSelectionModel().getSelectedItem();
        if(null == p){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You must select a post");;
            alert.show();
        }
        else{
            try {
                Stage stage = new Stage();
                FXMLLoader loaderPostAddTask = new FXMLLoader(getClass().getResource("/GUIFxml/Posts/JobDescription/PostAddTask.fxml"));
                Pane pane = loaderPostAddTask.load();
                GUIControllerPostAddTask guiControllerPostAddTask = loaderPostAddTask.getController();
                guiControllerPostAddTask.initComponents(this.jobDescriptionController,stage,this.currentStage,connection,p.getId());
                Scene scene = new Scene(pane);
                scene.getStylesheets().add(getClass().getResource("/css/post.css").toString());

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
        Post p = table.getSelectionModel().getSelectedItem();
        if (null == p) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You must select a post");
            alert.show();
        } else {
            try {
                Stage stage = new Stage();
                FXMLLoader loaderPostDeleteTask = new FXMLLoader(getClass().getResource("/GUIFxml/Posts/JobDescription/PostDeleteTask.fxml"));
                Pane pane = loaderPostDeleteTask.load();
                GUIControllerPostDeleteTask guiControllerPostDeleteTask = loaderPostDeleteTask.getController();
                guiControllerPostDeleteTask.initComponents(this.jobDescriptionController, stage, this.currentStage, connection, p.getId());
                Scene scene = new Scene(pane);
                stage.setScene(scene);
                scene.getStylesheets().add(getClass().getResource("/css/post.css").toString());

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
        columnName.setCellValueFactory(new PropertyValueFactory<Post, String>("name"));
        columnType.setCellValueFactory(new PropertyValueFactory<Post, String>("type"));
        columnSalary.setCellValueFactory(new PropertyValueFactory<Post, Double>("salary"));
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
                case "Name":
                    currentName = textFieldFilterInfo.getText();
                    break;
                case "Type":
                    currentType = textFieldFilterInfo.getText();
                    break;
                case "Salary":
                    currentSalary = textFieldFilterInfo.getText();
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
                case "Name":
                    textFieldFilterInfo.setText(currentName);
                    break;
                case "Type":
                    textFieldFilterInfo.setText(currentType);
                    break;
                case "Salary":
                    textFieldFilterInfo.setText(currentSalary);
                    break;
                default:
                    break;
            }
        }
    }


}
