package gr.ihu.ict.sportvideoanalysis;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DatabaseController implements Initializable {

    @FXML protected AnchorPane databasePane;
    @FXML protected Button createDbButton;
    @FXML protected Button loadDbButton;
    @FXML protected Button updateDbButton;
    @FXML protected Button checkDbButton;
    @FXML protected Button unsavedClipsButton;
    @FXML protected Button savedClipsButton;
    @FXML protected Button saveClipsButton;
    @FXML protected Button queryButton;
    @FXML protected Button saveHighlightButton;
    @FXML protected TextArea queryArea;
    @FXML protected Button exitButton;
    @FXML protected TableView<ObservableList<String>> tableView;
    private LabelListViewMapDTO labelListViewMapDTO;
    private ControllerManager controllerManager;
    private DatabaseManager databaseManager;

    public void initialize(URL url, ResourceBundle resourceBundle){
        databaseManager = new DatabaseManager();
    }

    public void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    public void setLabelListViewMapDTO(LabelListViewMapDTO labelListViewMapDTO) {
        this.labelListViewMapDTO = labelListViewMapDTO;
    }
    public void createDatabaseOnAction(){
        databaseManager.setLabelListViewMapDTO(labelListViewMapDTO);
        databaseManager.createDatabase((Stage) databasePane.getScene().getWindow());
        populateTableView(new File(Main.activeProfile.getProfName() + ".db"));
    }

    public void loadDatabaseOnClick(){
        loadDatabase((Stage) databasePane.getScene().getWindow());

    }
    private void loadDatabase(Stage primaryStage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Database Files","*.db"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null){
            // Load the selected database file and populate the TableView
            populateTableView(selectedFile);
        }
    }
    private void populateTableView(File databaseFile){
        try {
            Class.forName("org.sqlite.JDBC");

            // Connect to the selected database file
            String dbName = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            Connection connection = DriverManager.getConnection(dbName);

            tableView.getColumns().clear(); // Clear existing columns
            tableView.getItems().clear(); // Clear existing data

            // Iterate through the tables in the database
            try (Statement statement = connection.createStatement()) {
                ResultSet tables = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");
                while (tables.next()) {
                    String tableName = tables.getString("name");

                    // Create a column for the table
                    TableColumn<ObservableList<String>, String> tableColumn = new TableColumn<>(tableName);
                    tableColumn.setPrefWidth(200); // Set a preferred width

                    // Fetch data from the table and add it as columns
                    ResultSet tableData = statement.executeQuery("SELECT * FROM " + tableName + ";");
                    ResultSetMetaData metaData = tableData.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        final int columnIndex = i;
                        String columnName = metaData.getColumnName(i);

                        // Create a column for each column in the table
                        TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
                        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(columnIndex)));
                        tableColumn.getColumns().add(column);
                    }

                    tableView.getColumns().add(tableColumn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exitButtonOnAction(){
        Stage stage = (Stage) createDbButton.getScene().getWindow();
        stage.close();
    }
}
