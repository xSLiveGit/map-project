package GUI.Filters;

import Controller.IController;
import Controller.JobDescriptionController;
import domain.ConnectionWrapper;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.exceptions.ControllerException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergiu on 1/11/2017.
 */
public abstract class GUIAbstractFilterByPrefix<E> {
    protected ObservableList<E> model;
    protected Stage currentStage;
    protected Stage parentStage;
    protected IController<E> controller;
    protected static String[] optionOperations = {"Add","Delete"};
    protected IController<E> newController;
    protected ConnectionWrapper connection;
    protected static Integer pageSize = 15;
    protected JobDescriptionController jobDescriptionController;
    protected User user;
    @FXML
    protected TableView<E> table;

    @FXML
    protected ComboBox<String> comboBoxOptionsOperation;
    @FXML
    protected Button buttonSelectOptionOperation;

    @FXML
    protected Button buttonNext;
    @FXML
    protected Button buttonBack;
    @FXML
    protected TextField textFieldPage;

    @FXML
    protected ComboBox<String> comboBoxFilterFields;
    @FXML
    protected TextField textFieldFilterInfo;
    @FXML
    protected Button buttonFilter;
    public GUIAbstractFilterByPrefix(){

    }

    public void initComponents(IController<E> controller, JobDescriptionController jobDescriptionController, Stage parentStage, Stage currentStage, ConnectionWrapper connection, User user) throws MapperException, RepositoryOpException, SQLException, FileNotFoundException {
        this.controller = controller;
        this.parentStage = parentStage;
        this.currentStage = currentStage;
        this.connection = connection;
        this.jobDescriptionController = jobDescriptionController;
        this.user = user;
        syncTableWithColumns();
        //model = FXCollections.observableList((ArrayList<E>)controller.getAllFromCurrentPage());
        model = FXCollections.observableArrayList();
        this.table.setItems(model);
        updateModel();
        updateNextBackButtons();
        populateFilterFieldsComboBox();
        populateOptionsOperation();
        initController();
    }
    protected abstract void initController() throws RepositoryOpException, SQLException, MapperException, FileNotFoundException;
    @FXML
    protected abstract void filterHandler() throws MapperException, SQLException, RepositoryOpException, ControllerException;
    protected  void updateModel(){
        if(null != this.newController){
            this.model.setAll(this.newController.getAllFromCurrentPage());
        }
    }

    protected void updateNextBackButtons() throws RepositoryOpException, MapperException {
        if(null == this.newController){
            this.buttonNext.setDisable(true);
            this.buttonBack.setDisable(true);
        }
        else{
            if(this.newController.hasNextPage()){
                this.buttonNext.setDisable(false);
            }
            else{
                this.buttonNext.setDisable(true);
            }

            if(this.newController.hasPreviousPage()){
                this.buttonBack.setDisable(false);
            }
            else{
                this.buttonBack.setDisable(true);
            }
        }
    }

    @FXML
    protected void nextPage() throws RepositoryOpException, MapperException, SQLException {
        if(this.newController.hasNextPage()){
            this.newController.nextPage();
            updateModel();
            updateNextBackButtons();
        }
    }

    @FXML
    protected void backPage() throws RepositoryOpException, MapperException, SQLException {
        if(this.newController.hasPreviousPage()){
            this.newController.previousPage();
            updateModel();
            updateNextBackButtons();
        }
    }

    @FXML
    protected void populateOptionsOperation(){
        ObservableList<String> avaibleOptionOperation = FXCollections.observableArrayList();
        if(user.hasAddRelationPrivileges()){
            avaibleOptionOperation.add(optionOperations[0]);
        }
        if(user.hasDeleteRelationPrivileges()){
            avaibleOptionOperation.add(optionOperations[1]);
        }
        comboBoxOptionsOperation.setItems(avaibleOptionOperation);
    }

    @FXML
    private void selectOptionOperationHandler(){
        String item = comboBoxOptionsOperation.getSelectionModel().getSelectedItem();
        if(null != item){
            switch (item){
                case "Add":
                    addOption();
                    break;
                case "Delete":
                    deleteOption();
                    break;
                default:
                    break;
            }
        }
    }

    protected abstract void addOption();

    protected abstract void deleteOption();

    @FXML
    protected abstract void addOptionHandler();
    @FXML
    protected abstract void deleteOptionHandler();
    @FXML
    protected abstract void actualiseFieldsInformation();
    protected abstract void syncTableWithColumns();
    protected abstract void populateFilterFieldsComboBox();
}
