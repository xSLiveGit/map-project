package GUI.Posts;

import Controller.PostController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sun.plugin2.ipc.windows.WindowsEvent;
import utils.exceptions.ControllerException;
import utils.exceptions.DuplicateEnityException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.sql.SQLException;

/**
 * Created by Sergiu on 1/6/2017.
 */
public class GUIControllerPostAdd {
    private PostController postController;
    private Stage thisStage;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldType;

    @FXML
    private TextField textFieldSalary;

    @FXML
    private Button buttonAdd;
    private Stage parentStage;
    public GUIControllerPostAdd(){

    }

    public void initComponents(PostController postController,Stage thisStage,Stage parentStage){
        this.postController = postController;
        this.thisStage = thisStage;
        this.parentStage = parentStage;
    }

    public void addButtonHandler(){
        String name = this.textFieldName.getText();
        String type = this.textFieldType.getText();
        String salary = this.textFieldSalary.getText();
        try {

            this.postController.add("1", name, type, salary);
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
        } catch (SQLException e) {
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
