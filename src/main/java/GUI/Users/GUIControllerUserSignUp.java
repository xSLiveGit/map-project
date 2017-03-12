package GUI.Users;

import Controller.UserController;
import domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.exceptions.ControllerException;
import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.io.FileNotFoundException;
import java.sql.SQLException;

/**
 * Created by Sergiu on 1/20/2017.
 */
public class GUIControllerUserSignUp {
    private UserController controller;
    private Stage currentStage;
    private Stage parentStage;
    public GUIControllerUserSignUp() {
    }

    public void initComponents(UserController userController,Stage currentStage,Stage parentStage){
        this.controller = userController;
        this.currentStage = currentStage;
        this.parentStage = parentStage;
        this.buttonSignUp.setId("my-register-button");
    }

    @FXML
    TextField textFieldUserName;
    @FXML
    PasswordField textFieldPassword;

    @FXML
    Button buttonSignUp;

    public void signUpHandler(){
        try {
            User u = controller.findById(textFieldUserName.getText());
            if(null == u){
                controller.add(textFieldUserName.getText(),textFieldPassword.getText(),"Basic");
                controller.saveAll();
                this.parentStage.show();
                this.currentStage.close();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("There exist this username. Try again with other username.");
                alert.setTitle("Error");
                alert.showAndWait();
            }
        } catch (MapperException | ControllerException | RepositoryOpException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.setTitle("Error");
            alert.showAndWait();
        } catch (SQLException | DuplicateEnityException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.setTitle("Error");
            alert.showAndWait();
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.setTitle("Error");
            alert.showAndWait();
        }
    }
}
