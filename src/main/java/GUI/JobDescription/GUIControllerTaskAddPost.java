package GUI.JobDescription;

import Controller.JobDescriptionController;
import Controller.PostController;
import Controller.TaskController;
import domain.ConnectionWrapper;
import domain.Post;
import domain.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import repository.DBRepository;
import repository.IDataBaseRepository;
import utils.DBManager.PostDBManager;
import utils.DBManager.TaskDBManager;
import utils.exceptions.ControllerException;
import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;
import utils.mapper.IMapper;
import utils.mapper.MapperPost;
import utils.mapper.MapperTask;
import validator.IValidator;
import validator.PostValidator;
import validator.TaskValidator;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by Sergiu on 1/7/2017.
 */
public class GUIControllerTaskAddPost {

    private PostController postController;
    private JobDescriptionController jobDescriptionController;
    private ObservableList<Post> model;
    private final Integer pageSize = 10;
    private Stage parentStage;
    private Integer currentIdTask;
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonBack;
    @FXML
    private TextField textFieldPage;
    @FXML
    private Button buttonSelect;
    @FXML
    private TableView<Post> tableViewPost;
    @FXML
    private TableColumn<Post,Double> columnSalaryPost;
    @FXML
    private TableColumn<Post,String> columnTypePost;
    @FXML
    private TableColumn<Post,String> columnNamePost;
    String oldNumber;

    ConnectionWrapper connection;
    private Stage currentStage;

    public GUIControllerTaskAddPost(){}

    public void initComponents(JobDescriptionController jobDescriptionController, Stage currentStage, Stage parentStage, ConnectionWrapper connection, Integer currentIdTask) throws MapperException, FileNotFoundException, RepositoryOpException, SQLException {
        this.parentStage = parentStage;
        this.connection = connection;
        this.jobDescriptionController = jobDescriptionController;
        this.currentStage = currentStage;
        this.currentIdTask = currentIdTask;

        setColumnInfo();
        createPostController();
        model = FXCollections.observableArrayList(((Collection<Post>) postController.getAllFromCurrentPage()));
        this.tableViewPost.setItems(model);

        this.currentStage.setOnCloseRequest(c-> {
            try {
                postController.clearAll();
                parentStage.show();
            } catch (SQLException | RepositoryOpException | MapperException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.setTitle("Error!");
                alert.show();
            }
        });
        oldNumber = "0";
    }

    private void createPostController() throws FileNotFoundException, RepositoryOpException, MapperException, SQLException {
        IValidator<Post> val = new PostValidator();
        IMapper<Post,Integer> mapperPost = new MapperPost();
        IDataBaseRepository<Post,Integer> repoPost = new DBRepository<Post,Integer>(connection,new PostValidator(),mapperPost,new PostDBManager(connection,mapperPost,"tempposts"),pageSize);
        repoPost.clearAll();
        this.postController = new PostController(repoPost);
        try {
            Collection<Post> col = jobDescriptionController.getAllPostWithoutTaskId(currentIdTask.toString());
            for(Post p : col){
                postController.addOriginal(p.getId().toString(),p.getName(),p.getType(),p.getSalary().toString());
            }
        } catch (ControllerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.setTitle("Error!");
            alert.show();
        }
        postController.saveAll();

    }

    private void setColumnInfo(){
        columnNamePost.setCellValueFactory(new PropertyValueFactory<Post, String>("name"));
        columnTypePost.setCellValueFactory(new PropertyValueFactory<Post, String>("type"));
        columnSalaryPost.setCellValueFactory(new PropertyValueFactory<Post, Double>("salary"));
    }

    private void updateModel() {
        model.setAll((Collection<Post>) postController.getAllFromCurrentPage());
        tableViewPost.refresh();
        if(postController.hasNextPage()){
            this.buttonNext.setDisable(false);
        }
        else{
            this.buttonNext.setDisable(true);
        }
        if(postController.hasPreviousPage()){
            this.buttonBack.setDisable(false);
        }
        else{
            this.buttonBack.setDisable(true);
        }
        this.textFieldPage.setText(((Integer)(postController.getCurrentPageNumber()+1)).toString());
    }


    public void selectHandler() throws FileNotFoundException {
        if(this.tableViewPost.getSelectionModel().getSelectedItem() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You haven't selected any items");
            alert.setTitle("Warning!");
            alert.show();
        }
        else{
            Post p= this.tableViewPost.getSelectionModel().getSelectedItem();
            try {
                this.jobDescriptionController.add("1",p.getId().toString(),this.currentIdTask.toString());
                this.postController.delete(p.getId().toString());
                updateModel();
                jobDescriptionController.saveAll();
            } catch (MapperException | RepositoryOpException | ControllerException | DuplicateEnityException | SQLException e) {
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
            postController.setCurrentPageNumber(nr);
            oldNumber = nr;
            updateModel();
        } catch (RepositoryOpException | ControllerException | MapperException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            textFieldPage.setText(oldNumber);
            alert.show();
        }
    }

    public void nextButtonHandler() {
        if(postController.hasNextPage()){
            try {
                postController.nextPage();
            } catch (MapperException | RepositoryOpException | SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            }
            updateModel();
        }
    }

    public void backButtonHandler() {
        if(postController.hasPreviousPage()){
            try {
                postController.previousPage();
                updateModel();
            } catch (MapperException | RepositoryOpException | SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }



}


