package GUI.Tasks;

import Controller.PostController;
import Controller.TaskController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utils.exceptions.ControllerException;
import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.sql.SQLException;

/**
 * Created by Sergiu on 1/6/2017.
 */
public class GUIControllerTaskUpdate {
    private TaskController taskController;
    private Stage thisStage;
    String currentId;

    @FXML
    private TextField textFieldType;

    @FXML
    private TextField textFieldDuration;
    @FXML
    private TextField textFieldDescription;

    @FXML
    private Button buttonUpdate;
    private Stage parentStage;
    public GUIControllerTaskUpdate(){

    }

    public void initComponents(TaskController taskController,Stage thisStage,Stage parentStage,String currentId){
        this.taskController = taskController;
        this.thisStage = thisStage;
        this.parentStage = parentStage;
        this.currentId = currentId;

    }

    public void updateButtonHandler() throws SQLException {
        String type = this.textFieldType.getText();
        String duration = this.textFieldDuration.getText();
        String description = this.textFieldDescription.getText();
        try {
            this.taskController.update(currentId, type, duration,description);
            thisStage.getOnCloseRequest().handle(new WindowEvent(thisStage, WindowEvent.WINDOW_CLOSE_REQUEST));

            this.thisStage.close();
        } catch (MapperException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.setTitle("Error.");
            alert.showAndWait();
        } catch (RepositoryOpException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.setTitle("Error.");
            alert.showAndWait();
        } catch (ControllerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.setTitle("Error.");
            alert.showAndWait();
        } catch (DuplicateEnityException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.setTitle("Error.");
            alert.showAndWait();
        }
    }

}
