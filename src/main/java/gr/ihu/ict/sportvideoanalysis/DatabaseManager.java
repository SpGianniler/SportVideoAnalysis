package gr.ihu.ict.sportvideoanalysis;

import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:your_database_name.db"; // SQLite database URL
    private Connection connection;
    private final Map<String, ListView<String>> labelListViewMap;

    public DatabaseManager(Map<String, ListView<String>> labelListViewMap) {
        this.labelListViewMap = labelListViewMap;
    }

    public void createDatabase() {
        try {
            // Initialize the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");


            // Connect to the database (creates the database if it doesn't exist)
            connection = DriverManager.getConnection(DB_URL);

            // Define table schemas based on the label names
            defineTableSchemas();

            System.out.println("Database created successfully.");
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
            for (Map.Entry<String, ListView<String>> entry : labelListViewMap.entrySet()) {
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
