package GUI.Reports;

import Controller.JobDescriptionController;
import Controller.PostController;
import Controller.TaskController;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import domain.ConnectionWrapper;
import domain.DTO.TopPosts;
import domain.DTO.TopTasks;
import domain.Post;
import domain.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sergiu on 1/10/2017.
 */
public class GUIControllerTopTasks {

    public GUIControllerTopTasks() {
    }

    private static final Integer pageSize = 20;

    @FXML
    TableView<Post> tableViewPost;
    @FXML
    TableColumn<Post,String> tableColumnPostName;
    @FXML
    TableColumn<Post,String> tableColumnPostType;
    @FXML
    TableColumn<Post,Double> tableColumnPostSalary;

    @FXML
    TableView<Task> tableViewTask;
    @FXML
    TableColumn<Task,String> tableColumnTaskType;
    @FXML
    TableColumn<Task,String> tableColumnTaskDescription;
    @FXML
    TableColumn<Task,Integer> tableColumnTaskDuration;

    @FXML
    Slider sliderPercent;

    @FXML
    Button back;
    @FXML
    Button next;
    @FXML
    TextField textFieldPageNr;

    @FXML
    Button top;
    @FXML
    Button topPercent;
    @FXML
    Button export;
    @FXML
    TextField textFieldTop;
    @FXML
    TextField textFieldTop1;
    @FXML
    Pane diagramPane;
    Image imgReports;
    private TaskController taskController;
    private JobDescriptionController jobDescriptionController;
    private Stage thisStage;
    private ObservableList<Post> modelPost;
    private ObservableList<Task> modelTask;
    private ObservableList<TopTasks> modelTopTasks;
    private ConnectionWrapper connection;
    private Stage parentStage;

    public void initComponents(JobDescriptionController jobDescriptionController, Stage stage, ConnectionWrapper connection,Stage parentStage){
        this.jobDescriptionController = jobDescriptionController;
        this.thisStage = stage;
        modelPost = FXCollections.observableArrayList();
        modelTask = FXCollections.observableArrayList();
        modelTopTasks = FXCollections.observableArrayList();
        tableViewPost.setItems(modelPost);
        tableViewTask.setItems(modelTask);
        this.connection = connection;
        this.parentStage = parentStage;
        setProperty();
        setEnableDisableButtons();
        generateBarChart();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                parentStage.show();
                if(taskController != null){
                    if(taskController != null) {
                        try {
                            taskController.clearAll();
                        } catch (SQLException | RepositoryOpException | MapperException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        imgReports = null;
        this.export.setDisable(true);
        this.next.setText("");
        this.back.setText("");
        ImageView imnext = new ImageView(new Image(getClass().getResourceAsStream("/buttons/forward-icon.png")));
        ImageView imback = new ImageView(new Image(getClass().getResourceAsStream("/buttons/back-icon.png")));
        imback.setFitWidth(40);
        imback.setFitHeight(20);
        imnext.setFitWidth(40);
        imnext.setFitHeight(20);

        this.next.setGraphic(imnext);
        this.back.setGraphic(imback);
    }

    private void updateModel() throws RepositoryOpException, MapperException, ControllerException, SQLException {
        updateModelPost();
        updateModelTask();
        this.export.setDisable(false);
    }

    private void updateModelTask(){
        modelTask.setAll(taskController.getAllFromCurrentPage());

        tableViewTask.refresh();
        //generateBarChart();
    }

    private void updateModelPost() throws MapperException, SQLException, RepositoryOpException, ControllerException {
        Task t = tableViewTask.getSelectionModel().getSelectedItem();
        if(t == null){
            modelPost.clear();
        }
        else{
            modelPost.setAll(jobDescriptionController.getAllPostWithTaskIdRelation(t.getId().toString()));
        }
    }

    private void setProperty(){
        tableColumnTaskDuration.setCellValueFactory(new PropertyValueFactory<Task, Integer>("duration"));
        tableColumnTaskType.setCellValueFactory(new PropertyValueFactory<Task, String>("type"));
        tableColumnTaskDescription.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));

        tableColumnPostName.setCellValueFactory(new PropertyValueFactory<Post, String>("name"));
        tableColumnPostType.setCellValueFactory(new PropertyValueFactory<Post, String>("type"));
        tableColumnPostSalary.setCellValueFactory(new PropertyValueFactory<Post, Double>("salary"));
    }

    private void setController() throws MapperException, SQLException, RepositoryOpException, FileNotFoundException {

        IMapper<Task,Integer> mapperTask = new MapperTask();
        IDataBaseRepository<Task,Integer> repoTask = new DBRepository<Task,Integer>(connection,new TaskValidator(),mapperTask,new TaskDBManager(connection,mapperTask,"temptasks"),pageSize);
        repoTask.clearAll();
        this.taskController = new TaskController(repoTask);
        try {
            Integer i =1;
            for(Task t : this.modelTask){
                this.taskController.addOriginal(t.getId().toString(),t.getType(),t.getDuration().toString(),t.getDescription());
            }
            setEnableDisableButtons();
        } catch (ControllerException e) {
            msgBox(e);
        }
    }

    public void selectTopHandler(){
        try {
            ArrayList<Task> list = new ArrayList<>();
            this.modelTopTasks .setAll(jobDescriptionController.getTopTask(textFieldTop.getText()));
            modelTopTasks.forEach(e->list.add(e.getTask()));
            modelTask.setAll(list);
            setController();
            updateModel();
            generateBarChart();
            System.out.println(taskController.size());
        } catch (ControllerException | SQLException | FileNotFoundException | MapperException | RepositoryOpException e) {
            msgBox(e);
        }
    }

    public void selectTopPercentHandler() throws MapperException, RepositoryOpException, ControllerException {
        try{
            ArrayList<Task> list = new ArrayList<>();
            this.modelTopTasks.setAll(jobDescriptionController.getTopTasksPercent(textFieldTop1.getText()));
            modelTopTasks.forEach(e->list.add(e.getTask()));
            modelTask.setAll(list);
            setController();
            updateModel();
            generateBarChart();
        } catch (ControllerException | SQLException | RepositoryOpException | FileNotFoundException | MapperException e) {
            msgBox(e);
        }
    }

    public void selectedTaskHandler() throws MapperException, SQLException, RepositoryOpException, ControllerException {
        Task t = tableViewTask.getSelectionModel().getSelectedItem();
        if(t!=null) {
            this.modelPost.setAll(this.jobDescriptionController.getAllPostWithTaskIdRelation(t.getId().toString()));
        }
    }

    public void nextPageHandler() throws MapperException, RepositoryOpException, SQLException, ControllerException {
        if(this.taskController!= null){
            if(this.taskController.hasNextPage()){
                this.taskController.nextPage();
                updateModel();
                setEnableDisableButtons();
            }
        }
    }

    public void backPageHandler() throws MapperException, RepositoryOpException, SQLException, ControllerException {
        if(this.taskController!= null){
            if(this.taskController.hasPreviousPage()){
                this.taskController.previousPage();
                updateModel();
                setEnableDisableButtons();
            }
        }
    }

    private void setEnableDisableButtons(){
        if(this.taskController!= null){
            if(this.taskController.hasPreviousPage()){
                this.back.setDisable(false);
            }
            else{
                this.back.setDisable(true);
            }
            if(this.taskController.hasNextPage()){
                this.next.setDisable(false);
            }
            else{
                this.next.setDisable(true);
            }
        }
        else{
            this.next.setDisable(true);
            this.back.setDisable(true);
        }
    }

    private void msgBox(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(e.getMessage());
        alert.show();
    }
//pana aici e ok cu modificatul
    private void generateBarChart(){

        diagramPane.getChildren().clear();
        final int dimension = 380;
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);

        bc.setTitle("Top Tasks");
        xAxis.setLabel("Posts");
        yAxis.setLabel("Registered posts");

        XYChart.Series<String, Number> data = new XYChart.Series<>();
        List<TopTasks> tasks = this.modelTopTasks;
        for (TopTasks tp: tasks) {
            data.getData().add(new XYChart.Data<>(tp.getTask().getType() + " " + tp.getTask().getDescription(), tp.getNrOfPostsRegistered()));
        }
        bc.setMaxWidth(dimension);
        bc.setMaxHeight(dimension);
        bc.getData().add(data);

        diagramPane.setMaxWidth(dimension);
        diagramPane.setMaxHeight(dimension);
        diagramPane.getChildren().add(bc);
        imgReports = bc.snapshot(new SnapshotParameters(), null);
        export.setDisable(false);
    }
//ok
    public void exportReportsToPDF() throws SQLException, ControllerException, RepositoryOpException, IOException, MapperException, DocumentException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF doc (*.pdf)", "*.pdf"));
        File file = fileChooser.showSaveDialog(this.thisStage);
        if (file != null) {
            String filePath = file.getAbsolutePath();
            exportReportsToPDFAux(filePath);
        }
        //exportReportsToPDFAux("D://itext-test.pdf");
    }

    public void exportReportsToPDFAux(String pathDestination) throws IOException, DocumentException, MapperException, RepositoryOpException, ControllerException, SQLException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pathDestination));
        document.open();
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 2});
        BufferedImage image = SwingFXUtils.fromFXImage(imgReports, null);
        BufferedImage newBufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
        com.itextpdf.text.Image img1 = com.itextpdf.text.Image.getInstance(newBufferedImage, null);
        img1.scaleAbsolute(image.getWidth(),image.getHeight());
        PdfPCell cell;
        table.addCell(img1);
        cell = new PdfPCell();
        String text = "";
        Collection<Task> pst = taskController.getAll();
        System.out.println(pst.size());
        HashMap<Task,Collection<Post>> hash = new HashMap<Task, Collection<Post>>();
        for(Task t : pst){
            Collection<Post> clt = jobDescriptionController.getAllPostWithTaskIdRelation(t.getId().toString());
            hash.put(t,clt);
            text += t.toString() + "\n\t\t\t\t\t\t" + "Nr of posts: " + clt.size() + "\n";
        }
        System.out.println(taskController.size());
        System.out.println(text);
        Paragraph prg = new Paragraph();
        prg.add(text);
        cell.addElement(prg);
        table.addCell(cell);
        document.add(table);
        text = "";
        System.out.println(hash.keySet().size());
        for(Task t : hash.keySet()){
            text = "\tThe post: " + t.toString() + ":\n";
            for(Post p : hash.get(t)){
                text+="\t\t\t\t\t\tThe task:" + p.toString() + "\n";
            }
            text += "\n";
            prg.clear();
            prg.add(text);
            document.add(prg);
        }
        document.close();
    }
}
