package GUI.Users;

import Controller.UserController;
import Controller.UserRequestController;
import GUI.GUIController;
import domain.Post;
import domain.User;
import domain.UserRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.exceptions.ControllerException;
import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.sql.SQLException;

/**
 * Created by Sergiu on 1/20/2017.
 */
public class GUIControllerUserRequest {
    @FXML
    TableView<UserRequest> table;
    @FXML
    TableColumn<UserRequest,String> columnName;
    @FXML
    TableColumn<UserRequest,String>    columnOldType;
    @FXML
    TableColumn<UserRequest,Double>    columnNewType;

    @FXML
    Button buttonAccept;
    @FXML
    Button buttonReject;

    private Stage currentStage;
    private Stage parentStage;

    ObservableList<UserRequest> model;
    UserRequestController userRequestController;
    UserController userController;
    public GUIControllerUserRequest(){

    }

    public void initComponents(UserRequestController userRequestController, UserController userController,Stage currentStage, Stage parentStage) throws MapperException, RepositoryOpException, SQLException {
        this.userRequestController = userRequestController;
        this.currentStage = currentStage;
        this.parentStage = parentStage;
        this.userController = userController;
        syncColumn();
        model = FXCollections.observableArrayList(userRequestController.getAll());
        System.out.println(model.size());
        table.setItems(model);
    }

    public void syncColumn(){
        this.columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.columnNewType.setCellValueFactory(new PropertyValueFactory<>("newType"));
        this.columnOldType.setCellValueFactory(new PropertyValueFactory<>("oldType"));
    }

    private void updateModel() throws MapperException, RepositoryOpException, SQLException {
        this.model.clear();
        this.model.setAll(userRequestController.getAll());
    }

    public void acceptRequestHandler() throws MapperException, RepositoryOpException, ControllerException, SQLException, DuplicateEnityException {
        if(this.table.getSelectionModel().getSelectedItem() != null){
            UserRequest userR = this.table.getSelectionModel().getSelectedItem();
            User u = userController.findById(userR.getId());
            userController.update(u.getId(),u.getPassword(),u.getNextUserType());
            userRequestController.delete(userR.getId());
            updateModel();
        }
    }

    public void rejectRequestHandler() throws MapperException, SQLException, RepositoryOpException, ControllerException {
        if(this.table.getSelectionModel().getSelectedItem() != null){
            UserRequest userR = this.table.getSelectionModel().getSelectedItem();
            userRequestController.delete(userR.getId());
            updateModel();
        }
    }


}
