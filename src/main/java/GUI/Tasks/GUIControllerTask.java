package GUI.Tasks;

import Controller.JobDescriptionController;
import Controller.PostController;
import Controller.TaskController;
import Controller.UserRequestController;
import GUI.Filters.GUIPostFilterByPrefix;
import GUI.Filters.GUITaskFilterByPrefix;
import GUI.GUIController;
import GUI.JobDescription.GUIControllerTaskAddPost;
import GUI.JobDescription.GUIControllerTaskDeletePost;
import GUI.Reports.GUIControllerTopPosts;
import GUI.Reports.GUIControllerTopTasks;
import GUI.Tasks.GUIControllerTaskAdd;
import GUI.Tasks.GUIControllerTaskUpdate;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utils.exceptions.ControllerException;
import utils.exceptions.MapperException;
import utils.exceptions.RepositoryOpException;

import javax.xml.soap.Text;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Sergiu on 1/6/2017.
 */
public class GUIControllerTask {
    private static String operationsEntity[]={"Add","Update","Delete"};
    private final static String operationsJD[] = {"Add","Delete"};
    private final static String filterTypes[]={"Filter By Prefix"};
    private final  String reports[]={"Top posts","Top Tasks"};
    private boolean needToSaveToDb;
    private Stage currentStage;
    private Parent currentScene;
    private ObservableList<Task> model;
    private TaskController taskController;
    private String oldNumber;
    private ConnectionWrapper connection;
    private JobDescriptionController jobDescriptionController;
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
    private TableView<Task> tableViewTask;
    @FXML
    private TableColumn<Task,Integer> columnDurationTask;
    @FXML
    private TableColumn<Task,String> columnTypeTask;
    @FXML
    private TableColumn<Task,String> columnDescriptionTask;

    @FXML
    ComboBox<String> comboBoxChoseAction;
    @FXML
    ComboBox<String> comboBoxChoseFilterType;
    @FXML
    ComboBox<String> comboBoxChoseOptionAction;
    @FXML
    ComboBox<String> comboBoxChoseReportType;
    public GUIControllerTask(){

    }

    public void initComponents(Stage currentStage, Parent currentScene, TaskController taskController, JobDescriptionController jobDescriptionController, ConnectionWrapper connection,User user){
        this.connection = connection;
        this.currentStage = currentStage;
        this.currentScene = currentScene;
        this.jobDescriptionController = jobDescriptionController;
        this.taskController = taskController;
        this.user = user;

        columnDurationTask.setCellValueFactory(new PropertyValueFactory<Task, Integer>("duration"));
        columnTypeTask.setCellValueFactory(new PropertyValueFactory<Task, String>("type"));
        columnDescriptionTask.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
        model = FXCollections.observableArrayList(((Collection<Task>) taskController.getAllFromCurrentPage()));
        this.tableViewTask.setItems(model);
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

        needToSaveToDb=false;
    }

    private void addComboBoxItems(){
        addActionCombo();
        addJDOperation();
        addFilterTypeCombo();
        addReportsCombo();
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
            this.comboBoxChoseAction.getSelectionModel().select(0);
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
        model.setAll((Collection<Task>) taskController.getAllFromCurrentPage());
        tableViewTask.refresh();
        if(taskController.hasNextPage()){
            this.buttonNext.setDisable(false);
        }
        else{
            this.buttonNext.setDisable(true);
        }
        if(taskController.hasPreviousPage()){
            this.buttonBack.setDisable(false);
        }
        else{
            this.buttonBack.setDisable(true);
        }
        this.textFieldPage.setText(((Integer)(taskController.getCurrentPageNumber()+1)).toString());
        setEnableChoseActionButton();
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

    public void addPostButtonHandler(){

    }

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
        for(Task el : this.tableViewTask.getSelectionModel().getSelectedItems()){
            this.taskController.delete(el.getId().toString());
        }
        updateModel();
    }

    private void createAddWindows() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loaderAdd =  new FXMLLoader(getClass().getResource("/GUIFxml/Tasks/Operations/AddTask.fxml"));
        Pane pane = loaderAdd.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/css/task.css").toString());
        GUIControllerTaskAdd gc = loaderAdd.getController();
        gc.initComponents(taskController,stage,currentStage);
        currentStage.hide();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        stage.setOnCloseRequest(event -> {
            updateModel();
            currentStage.show();
        });
    }


    private void createUpdateWindows() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loaderAdd =  new FXMLLoader(getClass().getResource("/GUIFxml/Tasks/Operations/UpdateTask.fxml"));
        Pane pane = loaderAdd.load();
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getResource("/css/task.css").toString());

        stage.setScene(scene);
        GUIControllerTaskUpdate gc = loaderAdd.getController();
        if(tableViewTask.getSelectionModel().getSelectedItem() != null){
            Integer id = tableViewTask.getSelectionModel().getSelectedItem().getId();
            gc.initComponents(taskController,stage,currentStage,id.toString());
            currentStage.hide();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            stage.setOnCloseRequest(event -> {
                updateModel();
                currentStage.show();
            });
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You haven't selected an item");
            alert.setTitle("You must select an item");
            alert.show();
        }
    }

    public void nextButtonHandler() throws RepositoryOpException, MapperException, SQLException {
        if(taskController.hasNextPage()){
            taskController.nextPage();
            updateModel();
        }
    }

    public void backButtonHandler() {
        if(taskController.hasPreviousPage()){
            try {
                taskController.previousPage();
                updateModel();
            } catch (MapperException | SQLException | RepositoryOpException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }

    public void getPageNr() throws SQLException {
        String nr = this.textFieldPage.getText();
        try {
            taskController.setCurrentPageNumber(nr);
            oldNumber = nr;
            updateModel();
        } catch (RepositoryOpException | ControllerException | MapperException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            textFieldPage.setText(oldNumber);
            alert.show();
        }
    }

    public void choseOptionActionHandler() throws MapperException, SQLException, RepositoryOpException, IOException {
        if(this.comboBoxChoseOptionAction.getSelectionModel().getSelectedItem() != null){
            String option = comboBoxChoseOptionAction.getSelectionModel().getSelectedItem();
            switch (option){
                case "Add":
                    addPostToTask();
                    break;
                case "Delete":
                    deletePostFromTask();
                    break;
                default:
                    break;
            }
        }
    }

    private void addPostToTask(){
        Task t = tableViewTask.getSelectionModel().getSelectedItem();
        if(null == t){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You must select a post");;
            alert.show();
        }
        else{
            try {
                Stage stage = new Stage();
                FXMLLoader loaderTaskAddPost = new FXMLLoader(getClass().getResource("/GUIFxml/Tasks/JobDescription/TaskAddPost.fxml"));

                Pane pane = loaderTaskAddPost.load();
                GUIControllerTaskAddPost guiControllerTaskAddPost = loaderTaskAddPost.getController();
                guiControllerTaskAddPost.initComponents(this.jobDescriptionController,stage,this.currentStage,connection,t.getId());
                Scene scene = new Scene(pane);
                scene.getStylesheets().add(getClass().getResource("/css/task.css").toString());
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
    private void deletePostFromTask() throws IOException, MapperException, RepositoryOpException, SQLException {
        Task t = tableViewTask.getSelectionModel().getSelectedItem();
        if (null == t) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You must select a post");
            alert.show();
        } else {
            //try {
                Stage stage = new Stage();
                FXMLLoader loaderTaskDeletePost = new FXMLLoader(getClass().getResource("/GUIFxml/Tasks/JobDescription/TaskDeletePost.fxml"));
                Pane pane = loaderTaskDeletePost.load();
                GUIControllerTaskDeletePost guiControllerTaskDeletePost = loaderTaskDeletePost.getController();
                guiControllerTaskDeletePost.initComponents(this.jobDescriptionController, stage, this.currentStage, connection, t.getId());
                Scene scene = new Scene(pane);
                scene.getStylesheets().add(getClass().getResource("/css/task.css").toString());
                stage.setScene(scene);
                stage.show();
                this.currentStage.hide();
            /*
            } catch (IOException e) {
                msgBox(e);
            } catch (SQLException e) {
                msgBox(e);
            } catch (RepositoryOpException e) {
                msgBox(e);
            } catch (MapperException e) {
                msgBox(e);
            }*/

        }
    }


    public void saveAll(){
        try {
            this.taskController.saveAll();
            needToSaveToDb = false;
        } catch (MapperException | SQLException | RepositoryOpException | FileNotFoundException e) {
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
            comboBoxChoseFilterType.getSelectionModel().select(0);
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
        FXMLLoader loaderFilter = new FXMLLoader(getClass().getResource("/GUIFxml/Tasks/Filters/GUIFilterByPrefix.fxml"));
        Pane pane = loaderFilter.load();
        GUITaskFilterByPrefix controller = loaderFilter.getController();
        controller.initComponents(taskController,jobDescriptionController,currentStage,stage,connection,user);
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getResource("/css/task.css").toString());
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


        Integer oldPage = this.taskController.getCurrentPageNumber();
        this.taskController.setCurrentPageNumber("1");
        PdfPCell cellNameT = new PdfPCell(new Paragraph("Type:"));
        PdfPCell cellTypeT = new PdfPCell(new Paragraph("Description: "));
        PdfPCell cellSalaryT = new PdfPCell(new Paragraph("Duration: "));
        table.addCell(cellNameT);
        table.addCell(cellTypeT);
        table.addCell(cellSalaryT);
        while(true){

            taskController.getAllFromCurrentPage().forEach(el->{
                PdfPCell cellName = new PdfPCell(new Paragraph(el.getType()));
                PdfPCell cellType = new PdfPCell(new Paragraph(el.getDescription()));
                PdfPCell cellSalary = new PdfPCell(new Paragraph(el.getDuration().toString()));

                table.addCell(cellName);
                table.addCell(cellType);
                table.addCell(cellSalary);

            });
            if(taskController.hasNextPage()){
                taskController.nextPage();
            }
            else{
                break;
            }
        }
        this.taskController.setCurrentPageNumber(((Integer)(oldPage+1)).toString());
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
        Integer oldPage = this.taskController.getCurrentPageNumber();
        this.taskController.setCurrentPageNumber("1");
        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathDestination)))
        ) {

            while(true){
                taskController.getAllFromCurrentPage().forEach(el->{
                    try {
                        writer.write(el.getType() + "," + el.getDescription() + "," + el.getDuration() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                if(taskController.hasNextPage()){
                    taskController.nextPage();
                }
                else{
                    break;
                }
            }
        }
        this.taskController.setCurrentPageNumber(((Integer)(oldPage+1)).toString());
    }
}
