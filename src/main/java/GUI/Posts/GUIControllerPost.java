package GUI.Posts;

import Controller.JobDescriptionController;
import Controller.PostController;
import Controller.UserRequestController;
import GUI.Filters.GUIPostFilterByPrefix;
import GUI.JobDescription.GUIControllerPostAddTask;
import GUI.JobDescription.GUIControllerPostDeleteTask;
import GUI.Reports.GUIControllerTopPosts;
import GUI.Reports.GUIControllerTopTasks;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import domain.ConnectionWrapper;
import domain.Post;
import domain.Task;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utils.ObserverPattern.Observable;
import utils.exceptions.ControllerException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by Sergiu on 1/6/2017.
 */
public class GUIControllerPost {
    private final  String operationsEntity[]={"Add","Update","Delete"};
    private final  String operationsJD[] = {"Add","Delete"};
    private final  String reports[]={"Top posts","Top Tasks"};
    private final  String filterTypes[]={"Filter By Prefix"};

    private boolean needToSaveToDb;
    private Stage currentStage;
    private Parent currentScene;
    private ObservableList<Post> model;
    private PostController postController;
    private JobDescriptionController jobDescriptionController;
    private String oldNumber;
    private ConnectionWrapper connection;
    private User user;
    Integer currentId;
    @FXML
    private Button buttonChoseAction;
    @FXML
    private Button buttonChoseFilterType;
    @FXML
    private Button buttonChoseOptionAction;
    @FXML
    private Button buttonChoseReportType;

    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonBack;
    @FXML
    private TextField textFieldPage;

    @FXML
    private TableView<Post> tableViewPost;
    @FXML
    private TableColumn<Post,String> columnNamePost;
    @FXML
    private TableColumn<Post,String> columnTypePost;
    @FXML
    private TableColumn<Post,Double> columnSalaryPost;

    @FXML
    ComboBox<String> comboBoxChoseAction;
    @FXML
    ComboBox<String> comboBoxChoseFilterType;
    @FXML
    ComboBox<String> comboBoxChoseOptionAction;
    @FXML
    ComboBox<String> comboBoxChoseReportType;

    private FXMLLoader loaderPostAddTask;
    public GUIControllerPost(){

    }

    public void initComponents(Stage currentStage, Parent currentScene, PostController postController, JobDescriptionController jobDescriptionController, ConnectionWrapper connection, User user){
        this.connection = connection;
        this.currentStage = currentStage;
        this.currentScene = currentScene;
        this.postController = postController;
        this.jobDescriptionController = jobDescriptionController;
        this.user = user;
        columnNamePost.setCellValueFactory(new PropertyValueFactory<Post, String>("name"));
        columnTypePost.setCellValueFactory(new PropertyValueFactory<Post, String>("type"));
        columnSalaryPost.setCellValueFactory(new PropertyValueFactory<Post, Double>("salary"));
        model = FXCollections.observableArrayList(((Collection<Post>) postController.getAllFromCurrentPage()));
        this.tableViewPost.setItems(model);
        oldNumber = "0";
        addComboBoxItems();
        updateModel();
        this.buttonNext.setText("");
        this.buttonBack.setText("");

        ImageView imnext = new ImageView(new Image(getClass().getResourceAsStream("/buttons/forward-icon.png")));
        ImageView imback = new ImageView(new Image(getClass().getResourceAsStream("/buttons/back-icon.png")));
        imback.setFitWidth(40);
        imback.setFitHeight(20);
        imnext.setFitWidth(40);
        imnext.setFitHeight(20);

        this.buttonNext.setGraphic(imnext);
        this.buttonBack.setGraphic(imback);
        this.buttonBack.setMaxWidth(120);
        this.buttonNext.setMaxWidth(120);
        initFXMLLoaders();
        needToSaveToDb = false;
    }

    private void initFXMLLoaders() {
        loaderPostAddTask = new FXMLLoader(getClass().getResource("/GUIFxml/Posts/JobDescription/PostAddTask.fxml"));
    }

    private void addComboBoxItems(){
        addActionCombo();
        addJDOperation();
        addReportsCombo();
        addFilterTypeCombo();
    }
    private void addActionCombo(){
        //ObservableList<String> obs = FXCollections.observableArrayList(operationsEntity);
        ObservableList<String> obs= FXCollections.observableArrayList();
        if(user.hasAddEntityPrivileges()){
            obs.add(operationsEntity[0]);
        }
        if(user.hasUpdateEntityPrivileges()){
            obs.add(operationsEntity[1]);
        }
        if(user.hasDeleteEntityPrivileges()){
            obs.add(operationsEntity[2]);
        }
        comboBoxChoseAction.setItems(obs);
        if(!comboBoxChoseAction.getItems().isEmpty()){
            comboBoxChoseAction.getSelectionModel().select(0);
        }
    }

    private void addReportsCombo(){
        //ObservableList<String> obs = FXCollections.observableArrayList(reports);
        ObservableList<String> obs= FXCollections.observableArrayList();
        if(user.hasReportsPrivileges()){
            obs.setAll(reports);
        }
        comboBoxChoseReportType.setItems(obs);
        if(!comboBoxChoseReportType.getItems().isEmpty()){
            this.comboBoxChoseReportType.getSelectionModel().select(0);
        }
    }

    private void addJDOperation(){
        ObservableList<String> obs = FXCollections.observableArrayList();
        if(user.hasAddRelationPrivileges()){
            obs.add(operationsJD[0]);
        }
        if(user.hasDeleteRelationPrivileges()){
            obs.add(operationsJD[1]);
        }
        comboBoxChoseOptionAction.setItems(obs);
        if(!comboBoxChoseOptionAction.getItems().isEmpty()){
            this.comboBoxChoseOptionAction.getSelectionModel().select(0);
        }
    }




    private void populateComboBoxAction(){
       // ObservableList<Image> imgList = FXCollections.observableList()
       // this.comboBoxChoseAction.setItems();
       // AICI
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
        setEnableChoseActionButton();
    }

    public void addPostButtonHandler(){

    }

    private void setEnableChoseActionButton(){
        if(needToSaveToDb){
            this.buttonChoseOptionAction.setDisable(true);
            this.buttonChoseFilterType.setDisable(true);
            this.buttonChoseReportType.setDisable(true);
        }
        else{
            this.buttonChoseOptionAction.setDisable(false);
            this.buttonChoseFilterType.setDisable(false);
            this.buttonChoseReportType.setDisable(false);
        }
    }

    public void nextButtonHandler() {
        if(postController.hasNextPage()){
            try {
                postController.nextPage();
            } catch (MapperException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            } catch (RepositoryOpException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            } catch (SQLException e) {
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
            } catch (MapperException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            } catch (RepositoryOpException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }

    public void getPageNr() throws SQLException {
        String nr = this.textFieldPage.getText();
        try {
            postController.setCurrentPageNumber(nr);
            oldNumber = nr;
            updateModel();
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

    //                                                        ----  Chose action handler ---

    public void buttonChoseActionHandler() throws IOException, RepositoryOpException, MapperException, ControllerException, SQLException {
        if(this.comboBoxChoseAction.getSelectionModel().getSelectedItem() != null){
            String text = this.comboBoxChoseAction.getSelectionModel().getSelectedItem().toString();
            switch (text){
                case "Add":
                    createAddWindows();
                    updateModel();
                    needToSaveToDb = true;
                    break;
                case "Delete":
                    deleteHandler();
                    updateModel();
                    needToSaveToDb = true;
                    break;
                case "Update":
                    createUpdateWindows();
                    updateModel();
                    needToSaveToDb = true;
                default:
                    break;
            }
        }

    }

    private void deleteHandler() throws RepositoryOpException, MapperException, ControllerException, SQLException {
        for(Post el : this.tableViewPost.getSelectionModel().getSelectedItems()){
            this.postController.delete(el.getId().toString());
        }
        updateModel();
    }

    private void createAddWindows() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loaderAdd =  new FXMLLoader(getClass().getResource("/GUIFxml/Posts/Operations/AddPost.fxml"));
        Pane pane = loaderAdd.load();
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getResource("/css/post.css").toString());

        stage.setScene(scene);
        GUIControllerPostAdd gc = loaderAdd.getController();
        gc.initComponents(postController,stage,currentStage);
        currentStage.hide();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                currentStage.show();
                updateModel();
            }
        });
    }


    private void createUpdateWindows() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loaderAdd =  new FXMLLoader(getClass().getResource("/GUIFxml/Posts/Operations/UpdatePost.fxml"));
        Pane pane = loaderAdd.load();
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getResource("/css/post.css").toString());
        stage.setScene(scene);
        GUIControllerPostUpdate gc = loaderAdd.getController();
        if(tableViewPost.getSelectionModel().getSelectedItem() != null){
            Integer id = tableViewPost.getSelectionModel().getSelectedItem().getId();
            gc.initComponents(postController,stage,currentStage,id.toString());
            currentStage.hide();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    updateModel();
                    currentStage.show();
                }
            });
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You haven't selected an item");
            alert.setTitle("You must select an item");
            alert.show();
        }
    }

    //                                                                  ---- Option Actions ----

    public void choseOptionActionHandler() throws MapperException, SQLException, RepositoryOpException, IOException {
        if(this.comboBoxChoseOptionAction.getSelectionModel().getSelectedItem() != null){
            String option = comboBoxChoseOptionAction.getSelectionModel().getSelectedItem();
            switch (option){
                case "Add":
                    addTaskToPost();
                    break;
                case "Delete":
                    deleteTaskFromPost();
                    break;
                default:
                    break;
            }
            this.buttonChoseReportType.setDisable(true);
        }
    }

    public void setReportsButtonOn(){
        this.buttonChoseReportType.setDisable(false);
    }

    private void addTaskToPost() throws IOException, MapperException, RepositoryOpException, SQLException {
        Post p = tableViewPost.getSelectionModel().getSelectedItem();
        if(null == p){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You must select a post");;
            alert.show();
        }
        else{
            try {
                Stage stage = new Stage();
                loaderPostAddTask = new FXMLLoader(getClass().getResource("/GUIFxml/Posts/JobDescription/PostAddTask.fxml"));
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

    private void deleteTaskFromPost() {
        Post p = tableViewPost.getSelectionModel().getSelectedItem();
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
                scene.getStylesheets().add(getClass().getResource("/css/post.css").toString());
                stage.setScene(scene);
                stage.show();
                this.currentStage.hide();
            } catch (IOException | MapperException | RepositoryOpException | SQLException e) {
                msgBox(e);
            }

        }
    }

    public void saveAll(){
        try {
            this.postController.saveAll();
            needToSaveToDb = false;
        } catch (MapperException | FileNotFoundException | SQLException | RepositoryOpException e) {
            msgBox(e);
        }
    }

    private void msgBox(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(e.getMessage());
        alert.show();
    }

    public void setMustSave(boolean value){
        this.needToSaveToDb = value;
        setEnableChoseActionButton();
        updateModel();
    }

    //                              ------
    public void choseReportsType() throws IOException, MapperException, RepositoryOpException, SQLException {
        if(this.comboBoxChoseReportType.getSelectionModel().getSelectedItem() != null){
            String option = comboBoxChoseReportType.getSelectionModel().getSelectedItem();
            switch (option){
                case "Top posts":
                    createPostReports();
                    break;
                case "Top Tasks":
                    createTaskReports();
                    break;
                default:
                    break;
            }
            jobDescriptionController.saveAll();
        }
    }

    private void createTaskReports() throws IOException {
        Stage stage = new Stage();
        FXMLLoader reportsLoader = new FXMLLoader(getClass().getResource("/GUIFxml/Reports/TopTasks.fxml"));
        BorderPane borderPane =  reportsLoader.load();
        GUIControllerTopTasks guiControllerTopTasks = reportsLoader.getController();
        guiControllerTopTasks.initComponents(this.jobDescriptionController,stage,connection,currentStage);
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/css/task.css").toString());
        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                currentStage.show();
//           }
//        });
        currentStage.hide();
        stage.show();
    }

    private void createPostReports() throws IOException {
        Stage stage = new Stage();
        FXMLLoader reportsLoader = new FXMLLoader(getClass().getResource("/GUIFxml/Reports/TopPosts.fxml"));
        BorderPane borderPane =  reportsLoader.load();
        GUIControllerTopPosts guiControllerTopPosts = reportsLoader.getController();
        guiControllerTopPosts.initComponents(this.jobDescriptionController,stage,connection,currentStage);
        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(getClass().getResource("/css/post.css").toString());
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                currentStage.show();
//           }
//        });
        currentStage.hide();
        stage.show();
    }

    private void addFilterTypeCombo(){
        ObservableList<String> obs = FXCollections.observableArrayList();
        if(user.hasFilterByPrefixPrivileges()){
            obs.add(filterTypes[0]);
        }
        comboBoxChoseFilterType.setItems(obs);
        if(!comboBoxChoseFilterType.getItems().isEmpty()){
            this.comboBoxChoseFilterType.getSelectionModel().select(0);
        }
    }

    public void filterHandle() throws IOException, MapperException, RepositoryOpException, SQLException {
        String item = comboBoxChoseFilterType.getSelectionModel().getSelectedItem();
        if(null != item){
            switch (item){
                case "Filter By Prefix":
                    filterByPrefix();
                    break;
                default:
                    break;
            }
        }
    }

    private void filterByPrefix() throws IOException, MapperException, RepositoryOpException, SQLException {
        Stage stage = new Stage();
        FXMLLoader loaderFilter = new FXMLLoader(getClass().getResource("/GUIFxml/Posts/Filters/GUIFilterByPrefix.fxml"));
        Pane pane = loaderFilter.load();
        GUIPostFilterByPrefix controller = loaderFilter.getController();
        controller.initComponents(postController,jobDescriptionController,currentStage,stage,connection,user);
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getResource("/css/post.css").toString());

        stage.setScene(scene);
        stage.show();
        currentStage.hide();
    }

    public void exportToPDF() throws SQLException, ControllerException, RepositoryOpException, IOException, MapperException, DocumentException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF doc (*.pdf)", "*.pdf"));
        File file = fileChooser.showSaveDialog(this.currentStage);
        if (file != null) {
            String filePath = file.getAbsolutePath();
            exportToPDFAux(filePath);
        }
        //exportReportsToPDFAux("D://itext-test.pdf");
    }

    private void exportToPDFAux(String pathDestination) throws IOException, DocumentException, MapperException, RepositoryOpException, ControllerException, SQLException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pathDestination));
        document.open();
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);


        Integer oldPage = this.postController.getCurrentPageNumber();
        this.postController.setCurrentPageNumber("1");
        PdfPCell cellNameT = new PdfPCell(new Paragraph("Name:"));
        PdfPCell cellTypeT = new PdfPCell(new Paragraph("Type: "));
        PdfPCell cellSalaryT = new PdfPCell(new Paragraph("Salary: "));
        table.addCell(cellNameT);
        table.addCell(cellTypeT);
        table.addCell(cellSalaryT);
        while(true){

            postController.getAllFromCurrentPage().forEach(el->{
                PdfPCell cellName = new PdfPCell(new Paragraph(el.getName()));
                PdfPCell cellType = new PdfPCell(new Paragraph(el.getType()));
                PdfPCell cellSalary = new PdfPCell(new Paragraph(el.getSalary().toString()));

                table.addCell(cellName);
                table.addCell(cellType);
                table.addCell(cellSalary);

            });
            if(postController.hasNextPage()){
                postController.nextPage();
            }
            else{
                break;
            }
        }
        this.postController.setCurrentPageNumber(((Integer)(oldPage+1)).toString());
        document.add(table);
        document.close();
    }

    public void exportToCSV(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV doc (*.csv)", "*.csv"));
        File file = fileChooser.showSaveDialog(this.currentStage);
        if (file != null) {
            String filePath = file.getAbsolutePath();
            try {
                exportToCSVAux(filePath);
            } catch (MapperException | IOException | ControllerException | RepositoryOpException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportToCSVAux(String pathDestination) throws MapperException, SQLException, RepositoryOpException, ControllerException, IOException {
        Integer oldPage = this.postController.getCurrentPageNumber();
        this.postController.setCurrentPageNumber("1");
        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathDestination)))
        ) {

            while(true){
                postController.getAllFromCurrentPage().forEach(el->{
                    try {
                        writer.write(el.getName() + "," + el.getType() + "," + el.getSalary() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                if(postController.hasNextPage()){
                    postController.nextPage();
                }
                else{
                    break;
                }
            }
        }
        this.postController.setCurrentPageNumber(((Integer)(oldPage+1)).toString());
    }
}

