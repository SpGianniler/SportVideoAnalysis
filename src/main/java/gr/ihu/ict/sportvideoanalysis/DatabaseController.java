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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;

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
    private java.util.ArrayList<Integer> selectedClipIds = new java.util.ArrayList<>();
    private String searchMode = "tags";
    private static final String ACTIVE_COLOR = "-fx-background-color: #3a9e5e; -fx-background-radius: 0px;";
    private static final String DEFAULT_COLOR = "-fx-background-color: E96151; -fx-background-radius: 0px;";

    public void initialize(URL url, ResourceBundle resourceBundle){
        databaseManager = new DatabaseManager();
        unsavedClipsButton.setOnAction(e -> tagsSearchOnAction());
        savedClipsButton.setOnAction(e -> categorySearchOnAction());
        queryButton.setOnAction(e -> queryButtonOnAction());
        updateDbButton.setOnAction(e -> clearSelectedClipsOnAction());
        checkDbButton.setOnAction(e -> showSelectedClipsOnAction());
        saveClipsButton.setOnAction(e -> selectClipOnAction());
        saveHighlightButton.setOnAction(e -> saveHighlightOnAction());
        unsavedClipsButton.setText("Tags Search");
        savedClipsButton.setText("Category Search");
        saveClipsButton.setText("Select Clip");
        saveClipsButton.setManaged(true);
    }

    public void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    public void setLabelListViewMapDTO(LabelListViewMapDTO labelListViewMapDTO) {
        this.labelListViewMapDTO = labelListViewMapDTO;
    }
    public void createDatabaseOnAction(){
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
    private void populateTableView(File databaseFile) {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbName = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            Connection connection = DriverManager.getConnection(dbName);
        
            tableView.getColumns().clear();
            tableView.getItems().clear();
            try (Statement statement = connection.createStatement()) {
                ResultSet tables = statement.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' ORDER BY name"
                );

                while (tables.next()) {
                    String tableName = tables.getString("name");

                    ResultSet columns = statement.executeQuery(
                        "PRAGMA table_info(" + tableName + ");"
                    );
                    List<String> colNames = new ArrayList<>();
                    while (columns.next()) {
                        colNames.add(columns.getString("name"));
                    }

                    // Create one TableColumn per column name and set its cell factory
                    for (int colIndex = 0; colIndex < colNames.size(); colIndex++) {
                        final int idx = colIndex; // for lambda
                        TableColumn<ObservableList<String>, String> column =
                            new TableColumn<>(colNames.get(colIndex));
                        String colName = colNames.get(colIndex);
                        int width = 120;
                        if (colName.equalsIgnoreCase("id")) width = 60;
                        else if (colName.equalsIgnoreCase("VideoLoc")) width = 200;
                        else if (colName.equalsIgnoreCase("Start") || colName.equalsIgnoreCase("End")) width = 90;
                        else if (colName.equalsIgnoreCase("tags")) width = 300;
                        column.setPrefWidth(width);
                        column.setCellFactory(tc -> {
                            TableCell<ObservableList<String>, String> cell = new TableCell<>();
                            cell.setWrapText(true);
                            cell.itemProperty().addListener((obs, oldVal, newVal) -> {
                                cell.setText(newVal != null ? newVal : "");
                            });
                            return cell;
                        });
                        column.setCellValueFactory(cellData -> {
                            ObservableList<String> row = cellData.getValue();
                            String value = "";
                            if (row != null && idx < row.size()) {
                                value = row.get(idx) != null ? row.get(idx) : "";
                            }
                            return new SimpleStringProperty(value);
                        });

                        tableView.getColumns().add(column);
                    }

                    // Load table rows and add them to the TableView
                    try (PreparedStatement selectStmt =
                         connection.prepareStatement("SELECT * FROM " + tableName)) {
                        ResultSet result = selectStmt.executeQuery();
                        while (result.next()) {
                            ObservableList<String> row = FXCollections.observableArrayList();
                            for (int c = 0; c < colNames.size(); c++) {
                                String val = result.getString(c + 1);
                                row.add(val != null ? val : "");
                            }
                            tableView.getItems().add(row);
                        }
                    }
                }
            }
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSelectedClipsOnAction() {
        if (selectedClipIds == null || selectedClipIds.isEmpty()) {
            Main.showErrorDialog("No Selection", "Select clips first using \"Select Clip\".");
            return;
        }
        String profileName = (Main.activeProfile != null && Main.activeProfile.getProfName() != null) ? Main.activeProfile.getProfName() : "defaultProfile";
        String dbPath = profileName + ".db";
        if (!new File(dbPath).exists()) {
            Main.showErrorDialog("No DB", "Load a database first.");
            return;
        }
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            StringBuilder sql = new StringBuilder("SELECT * FROM video WHERE id IN (");
            for (int i = 0; i < selectedClipIds.size(); i++) {
                if (i > 0) sql.append(",");
                sql.append("?");
            }
            sql.append(")");
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < selectedClipIds.size(); i++) {
                ps.setInt(i + 1, selectedClipIds.get(i));
            }
            ResultSet rs = ps.executeQuery();
            tableView.getItems().clear();
            tableView.getColumns().clear();
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                final int colIdx = i - 1;
                String colName = meta.getColumnName(i);
                int width = 120;
                if (colName.equalsIgnoreCase("id")) width = 60;
                else if (colName.equalsIgnoreCase("VideoLoc")) width = 200;
                else if (colName.equalsIgnoreCase("Start") || colName.equalsIgnoreCase("End") || colName.equalsIgnoreCase("tags") || colName.equalsIgnoreCase("categories")) width = 150;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(colName);
                col.setPrefWidth(width);
                col.setCellValueFactory(cd -> {
                    ObservableList<String> row = cd.getValue();
                    return new SimpleStringProperty(row != null && colIdx < row.size() ? row.get(colIdx) : "");
                });
                tableView.getColumns().add(col);
            }
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= colCount; i++) {
                    row.add(rs.getString(i) != null ? rs.getString(i) : "");
                }
                tableView.getItems().add(row);
            }
            tableView.refresh();
            System.out.println("Show selected clips returned " + tableView.getItems().size() + " rows.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tagsSearchOnAction() {
        searchMode = "tags";
        unsavedClipsButton.setStyle(ACTIVE_COLOR);
        savedClipsButton.setStyle(DEFAULT_COLOR);
    }
    public void categorySearchOnAction() {
        searchMode = "categories";
        savedClipsButton.setStyle(ACTIVE_COLOR);
        unsavedClipsButton.setStyle(DEFAULT_COLOR);
    }
    public void selectClipOnAction() {
        ObservableList<String> row = tableView.getSelectionModel().getSelectedItem();
        if (row != null && !row.isEmpty()) {
            try {
                int id = Integer.parseInt(row.get(0));
                if (!selectedClipIds.contains(id)) {
                    selectedClipIds.add(id);
                    Main.showErrorDialog("Selected", "Record id " + id + " added to highlight.");
                } else {
                    Main.showErrorDialog("Info", "Record id " + id + " already selected.");
                }
            } catch (Exception ex) {
                Main.showErrorDialog("Error", "Could not parse id from row.");
            }
        } else {
            Main.showErrorDialog("Select", "Please select a row in the table.");
        }
    }

    public void saveHighlightOnAction() {
        if (selectedClipIds.isEmpty()) {
            Main.showErrorDialog("No Selection", "Select at least one clip first.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Highlight Video");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP4 Files", "*.mp4"));
        File file = fileChooser.showSaveDialog(databasePane.getScene().getWindow());
        if (file == null) return;
        try {
            String profileName = (Main.activeProfile != null && Main.activeProfile.getProfName() != null) ? Main.activeProfile.getProfName() : "defaultProfile";
            String dbPath = profileName + ".db";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            StringBuilder ffmpegCmd = new StringBuilder("ffmpeg");
            // Gather clips info for concat or segment extraction
            for (int id : selectedClipIds) {
                PreparedStatement ps = conn.prepareStatement("SELECT VideoLoc, Start, End FROM video WHERE id = ?");
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String videoLoc = rs.getString("VideoLoc");
                    String start = rs.getString("Start");
                    String end = rs.getString("End");
                    // For simplicity, we log info; full concatenation requires more complex ffmpeg command
                    System.out.println("Highlight segment: " + videoLoc + " start=" + start + " end=" + end);
                }
            }
            // Build temp segment files and concat list
            java.nio.file.Path tempDir = java.nio.file.Files.createTempDirectory("highlight");
            java.util.ArrayList<String> concatList = new java.util.ArrayList<>();
            int segIdx = 0;
            for (int id : selectedClipIds) {
                java.nio.file.Path segmentFile = tempDir.resolve("seg_" + segIdx + ".mp4");
                java.sql.PreparedStatement psSeg = conn.prepareStatement("SELECT VideoLoc, Start, End FROM video WHERE id = ?");
                psSeg.setInt(1, id);
                java.sql.ResultSet rsSeg = psSeg.executeQuery();
                if (rsSeg.next()) {
                    String videoLoc = rsSeg.getString("VideoLoc");
                    String start = rsSeg.getString("Start");
                    String end = rsSeg.getString("End");
                    // Build ffmpeg segment extraction command
                    ProcessBuilder pbSeg = new ProcessBuilder(
                        "ffmpeg", "-i", videoLoc,
                        "-ss", start,
                        "-to", end,
                        "-c", "copy",
                        segmentFile.toString()
                    );
                    pbSeg.inheritIO();
                    pbSeg.start().waitFor();
                    concatList.add("file '" + segmentFile.toAbsolutePath().toString() + "'");
                }
                segIdx++;
            }
            java.nio.file.Path listFile = tempDir.resolve("list.txt");
            java.nio.file.Files.write(listFile, concatList);
            ProcessBuilder pbConcat = new ProcessBuilder(
                "ffmpeg", "-f", "concat", "-safe", "0", "-i", listFile.toString(),
                "-c", "copy", file.getAbsolutePath()
            );
            pbConcat.inheritIO();
            pbConcat.start().waitFor();
            Main.showErrorDialog("Highlight Saved", "Saved to: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            Main.showErrorDialog("Error", "Failed to save highlight.");
        }
    }

    public void queryButtonOnAction() {
        String text = queryArea.getText();
        if (text == null || text.trim().isEmpty()) return;
        String[] terms = text.split(",");
        String dbPath = (Main.activeProfile != null && Main.activeProfile.getProfName() != null) ? Main.activeProfile.getProfName() + ".db" : null;
        if (dbPath == null || !new File(dbPath).exists()) {
            Main.showErrorDialog("No DB", "Load a database first.");
            return;
        }
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            StringBuilder sql = new StringBuilder("SELECT * FROM video WHERE 1=1");
            java.util.ArrayList<String> conditions = new java.util.ArrayList<>();
            for (String raw : terms) {
                String term = raw.trim();
                if (!term.isEmpty()) {
                    conditions.add(" (" + searchMode + " LIKE ?)");
                }
            }
            for (String cond : conditions) {
                sql.append(" OR ").append(cond);
            }
            if (!conditions.isEmpty()) {
                // Replace first OR with WHERE since we started with WHERE 1=1
                String queryStr = sql.toString();
                queryStr = queryStr.replaceFirst("WHERE 1=1 OR ", "WHERE ");
                sql = new StringBuilder(queryStr);
            }
            
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            for (String raw : terms) {
                String term = raw.trim();
                if (!term.isEmpty()) {
                    ps.setString(idx++, "%" + term + "%");
                }
            }
            ResultSet rs = ps.executeQuery();
            tableView.getItems().clear();
            tableView.getColumns().clear();
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                final int colIdx = i - 1;
                String colName = meta.getColumnName(i);
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(colName);
                col.setPrefWidth(120);
                col.setCellValueFactory(cd -> {
                    ObservableList<String> row = cd.getValue();
                    return new SimpleStringProperty(row != null && colIdx < row.size() ? row.get(colIdx) : "");
                });
                tableView.getColumns().add(col);
            }
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= colCount; i++) {
                    row.add(rs.getString(i) != null ? rs.getString(i) : "");
                }
                tableView.getItems().add(row);
            }
            tableView.refresh();
            System.out.println("Query returned " + tableView.getItems().size() + " rows.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void clearSelectedClipsOnAction() {
        selectedClipIds.clear();
        Main.showErrorDialog("Cleared", "Selected clips cleared.");
        // Reload all records from DB
        String profileName = (Main.activeProfile != null && Main.activeProfile.getProfName() != null) ? Main.activeProfile.getProfName() : "defaultProfile";
        String dbPath = profileName + ".db";
        if (new File(dbPath).exists()) {
            populateTableView(new File(dbPath));
        }
    }

    public void exitButtonOnAction(){
        Stage stage = (Stage) createDbButton.getScene().getWindow();
        stage.close();
    }
}
