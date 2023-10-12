package gr.ihu.ict.sportvideoanalysis;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:"; // SQLite database URL
    private Connection connection;

    private LabelListViewMapDTO labelListViewMapDTO;
    private String dbName;

    public DatabaseManager(LabelListViewMapDTO labelListViewMapDTO) {
        this.labelListViewMapDTO = labelListViewMapDTO;
    }

    public DatabaseManager(){}

    public DatabaseManager(String dbName) {
        this.dbName = dbName;
    }

    public LabelListViewMapDTO getLabelListViewMapDTO() {
        return labelListViewMapDTO;
    }

    public void setLabelListViewMapDTO(LabelListViewMapDTO labelListViewMapDTO) {
        this.labelListViewMapDTO = labelListViewMapDTO;
    }

    public Connection getConnection() {
        if (connection != null) {
            return connection;
        }

        try {
            // Initialize the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            String databaseName = dbName != null ? dbName : Main.activeProfile.getProfName() + ".db";
            String dbUrl = DB_URL + databaseName;

            File dbFile = new File(databaseName);

            if (dbFile.exists()) {
                connection = DriverManager.getConnection(dbUrl);
                return connection;
            } else {
                // Handle the case where the database file doesn't exist
                // can throw an exception or return null
                return null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createDatabase(Stage primaryStage) {
        try {
            // Initialize the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to the database (creates the database if it doesn't exist)
            String dbName = DB_URL+Main.activeProfile.getProfName()+".db";

            // Check if the database file already exists
            File dbFile = new File(Main.activeProfile.getProfName()+".db");

            if (dbFile.exists()) {
                // Database file already exists; ask the user if they want to overwrite
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(primaryStage); // Set the owner to primaryStage
                alert.setTitle("Database Exists");
                alert.setHeaderText("The database already exists.");
                alert.setContentText("Do you want to overwrite it?");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // User chose to overwrite the database
                    dbFile.delete(); // Delete the existing database
                } else {
                    return; // User canceled, do nothing
                }
            }

            connection = DriverManager.getConnection(dbName);

            // Define table schemas based on the label names
            defineTableSchemas();
            populateTablesFromListViews();

            System.out.println("Database created and populated successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void defineTableSchemas() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Create a "video" table
            String createVideoTableSQL = "CREATE TABLE IF NOT EXISTS video ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "VideoLoc TEXT,"
                    + "Start TEXT,"
                    + "End TEXT);";
            statement.executeUpdate(createVideoTableSQL);

            // Iterate through the labelToListViewMap
            for (Map.Entry<String, ListView<String>> entry : labelListViewMapDTO.getLabelListViewMapDTO().entrySet()) {
                String tableName = entry.getKey();

                String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "column_name TEXT);";

                statement.executeUpdate(createTableSQL);

                // Create a junction table to establish many-to-many relationship
                String createJunctionTableSQL = "CREATE TABLE IF NOT EXISTS video_" + tableName + " ("
                        + "video_id INTEGER,"
                        + tableName + "_id INTEGER,"
                        + "FOREIGN KEY (video_id) REFERENCES video(id),"
                        + "FOREIGN KEY (" + tableName + "_id) REFERENCES " + tableName + "(id));";

                statement.executeUpdate(createJunctionTableSQL);
            }
        }
    }

    private void populateTablesFromListViews() throws SQLException {
        for (Map.Entry<String, ListView<String>> entry : labelListViewMapDTO.getLabelListViewMapDTO().entrySet()) {
            String tableName = entry.getKey();
            ListView<String> listView = entry.getValue();

            try (Statement statement = connection.createStatement()) {
                // Clear existing data in the table
                String deleteAllRowsSQL = "DELETE FROM " + tableName;
                statement.executeUpdate(deleteAllRowsSQL);

                // Insert new data from the ListView into the table
                for (String item : listView.getItems()) {
                    String insertDataSQL = "INSERT INTO " + tableName + " (column_name) VALUES ('" + item + "');";
                    statement.executeUpdate(insertDataSQL);
                }
            }
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
