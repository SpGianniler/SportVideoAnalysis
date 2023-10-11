package gr.ihu.ict.sportvideoanalysis;

import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:"; // SQLite database URL
    private Connection connection;

    private LabelListViewMapDTO labelListViewMapDTO;

    public DatabaseManager(LabelListViewMapDTO labelListViewMapDTO) {
        this.labelListViewMapDTO = labelListViewMapDTO;
    }

    public LabelListViewMapDTO getLabelListViewMapDTO() {
        return labelListViewMapDTO;
    }

    public void setLabelListViewMapDTO(LabelListViewMapDTO labelListViewMapDTO) {
        this.labelListViewMapDTO = labelListViewMapDTO;
    }

    public void createDatabase() {
        try {
            // Initialize the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to the database (creates the database if it doesn't exist)
            String dbName = DB_URL+Main.activeProfile.getProfName()+".db";
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

    private void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
