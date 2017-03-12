package GUI.Reports;

import Controller.JobDescriptionController;
import Controller.PostController;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import domain.ConnectionWrapper;
import domain.DTO.TopPosts;
import domain.Post;
import domain.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.image.WritableImage;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import repository.DBRepository;
import repository.IDataBaseRepository;
import utils.DBManager.PostDBManager;
import utils.ObserverPattern.Observable;
import utils.exceptions.ControllerException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;
import utils.mapper.IMapper;
import utils.mapper.MapperPost;
import validator.IValidator;
import validator.PostValidator;


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
 * Created by Sergiu on 1/8/2017.
 */
public class GUIControllerTopPosts {

    public GUIControllerTopPosts() {
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
    private PostController postController;
    private JobDescriptionController jobDescriptionController;
    private Stage thisStage;
    private ObservableList<Post> modelPost;
    private ObservableList<Task> modelTask;
    private ObservableList<TopPosts> modelTopPosts;
    private ConnectionWrapper connection;
    private Stage parentStage;
    public void initComponents(JobDescriptionController jobDescriptionController, Stage stage, ConnectionWrapper connection, Stage parentStage){
        this.jobDescriptionController = jobDescriptionController;
        this.thisStage = stage;
        modelPost = FXCollections.observableArrayList();
        modelTask = FXCollections.observableArrayList();
        modelTopPosts = FXCollections.observableArrayList();
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
                if(postController != null){
                    if(postController != null) {
                        try {
                            postController.clearAll();

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

    private void updateModelPost(){
        modelPost.setAll(postController.getAllFromCurrentPage());

        tableViewPost.refresh();
        //generateBarChart();
    }

    private void updateModelTask() throws MapperException, SQLException, RepositoryOpException, ControllerException {
        Post p = tableViewPost.getSelectionModel().getSelectedItem();
        if(p == null){
            modelTask.clear();
        }
        else{
            modelTask.setAll(jobDescriptionController.getAllTaskWithPostIdRelation(p.getId().toString()));
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
        IValidator<Post> val = new PostValidator();
        IMapper<Post,Integer> mapperPost = new MapperPost();
        IDataBaseRepository<Post,Integer> repoPost = new DBRepository<Post,Integer>(connection,new PostValidator(),mapperPost,new PostDBManager(connection,mapperPost,"tempposts"),pageSize);
        repoPost.clearAll();
        this.postController = new PostController(repoPost);
        try {
            Integer i =1;
            for(Post p : this.modelPost){
                this.postController.addOriginal(p.getId().toString(),p.getName(),p.getType(),p.getSalary().toString());
            }
            setEnableDisableButtons();
        } catch (ControllerException e) {
            msgBox(e);
        }
    }

    public void selectTopHandler(){
        try {
            ArrayList<Post> list = new ArrayList<>();
            this.modelTopPosts .setAll(jobDescriptionController.getTopPosts(textFieldTop.getText()));
            modelTopPosts.forEach(e->list.add(e.getPost()));
            modelPost.setAll(list);
            setController();
            updateModel();
            generateBarChart();
            System.out.println(postController.size());
        } catch (ControllerException | SQLException | FileNotFoundException | MapperException | RepositoryOpException e) {
            msgBox(e);
        }
    }

    public void selectTopPercentHandler() throws MapperException, RepositoryOpException, ControllerException {
        try{
            ArrayList<Post> list = new ArrayList<>();
            this.modelTopPosts.setAll(jobDescriptionController.getTopPostsPercent(textFieldTop1.getText()));
            modelTopPosts.forEach(e->list.add(e.getPost()));
            modelPost.setAll(list);
            setController();
            updateModel();
            generateBarChart();
        } catch (ControllerException | SQLException | RepositoryOpException | FileNotFoundException | MapperException e) {
            msgBox(e);
        }
    }

    public void selectedPostHandler() throws MapperException, SQLException, RepositoryOpException, ControllerException {
        Post p = tableViewPost.getSelectionModel().getSelectedItem();
        if(p!=null) {
            this.modelTask.setAll(this.jobDescriptionController.getAllTaskWithPostIdRelation(p.getId().toString()));
        }
    }

    public void nextPageHandler() throws MapperException, RepositoryOpException, SQLException, ControllerException {
        if(this.postController!= null){
            if(this.postController.hasNextPage()){
                this.postController.nextPage();
                updateModel();
                setEnableDisableButtons();
            }
        }
    }

    public void backPageHandler() throws MapperException, RepositoryOpException, SQLException, ControllerException {
        if(this.postController!= null){
            if(this.postController.hasPreviousPage()){
                this.postController.previousPage();
                updateModel();
                setEnableDisableButtons();
            }
        }
    }

    private void setEnableDisableButtons(){
        if(this.postController!= null){
            if(this.postController.hasPreviousPage()){
                this.back.setDisable(false);
            }
            else{
                this.back.setDisable(true);
            }
            if(this.postController.hasNextPage()){
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

    private void generateBarChart(){

        diagramPane.getChildren().clear();
         final int dimension = 380;
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);

        bc.setTitle("Top posts");
        xAxis.setLabel("Tasks");
        yAxis.setLabel("Registered tasks");

        XYChart.Series<String, Number> data = new XYChart.Series<>();
        List<TopPosts> posts = this.modelTopPosts;
        for (TopPosts tp: posts) {
            data.getData().add(new XYChart.Data<>(tp.getPost().getName() + " " + tp.getPost().getSalary(), tp.getNrOfTasksRegistered()));
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
        Collection<Post> pst = postController.getAll();
        System.out.println(pst.size());
        HashMap<Post,Collection<Task>> hash = new HashMap<Post, Collection<Task>>();
        for(Post p : pst){
            Collection<Task> clt = jobDescriptionController.getAllTaskWithPostIdRelation(p.getId().toString());
            hash.put(p,clt);
            text += p.toString() + "\n\t\t\t\t\t\t" + "Nr of tasks: " + clt.size() + "\n";
        }
        System.out.println(postController.size());
        System.out.println(text);
        Paragraph prg = new Paragraph();
        prg.add(text);
        cell.addElement(prg);
        table.addCell(cell);
        document.add(table);
        text = "";
        System.out.println(hash.keySet().size());
        for(Post p : hash.keySet()){
            text = "\tThe post: " + p.toString() + ":\n";
            for(Task t : hash.get(p)){
                text+="\t\t\t\t\t\tThe task:" + t.toString() + "\n";
            }
            text += "\n";
            prg.clear();
            prg.add(text);
            document.add(prg);
        }
        document.close();
    }
}
