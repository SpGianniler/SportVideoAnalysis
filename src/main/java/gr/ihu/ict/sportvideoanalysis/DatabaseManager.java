package gr.ihu.ict.sportvideoanalysis;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:";
    private Connection connection;
    private String dbName;

    public DatabaseManager(){}

    public DatabaseManager(String dbName) {
        this.dbName = dbName;
    }

    public Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        try {
            Class.forName("org.sqlite.JDBC");
            String profileName = (Main.activeProfile != null && Main.activeProfile.getProfName() != null) ? Main.activeProfile.getProfName() : "defaultProfile";
            String databaseName = dbName != null ? dbName : profileName + ".db";
            String dbUrl = DB_URL + databaseName;
            File dbFile = new File(databaseName);
            if (dbFile.exists()) {
                connection = DriverManager.getConnection(dbUrl);
                return connection;
            } else {
                return null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createDatabase(Stage primaryStage) {
        try {
            Class.forName("org.sqlite.JDBC");
            String profileName = (Main.activeProfile != null && Main.activeProfile.getProfName() != null) ? Main.activeProfile.getProfName() : "defaultProfile";
            String dbNameStr = DB_URL + profileName + ".db";
            File dbFile = new File(profileName + ".db");
            if (dbFile.exists()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(primaryStage);
                alert.setTitle("Database Exists");
                alert.setHeaderText("The database already exists.");
                alert.setContentText("Do you want to overwrite it?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    dbFile.delete();
                } else {
                    return;
                }
            }
            connection = DriverManager.getConnection(dbNameStr);
            try (Statement statement = connection.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS video ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "VideoLoc TEXT,"
                        + "Start TEXT,"
                        + "End TEXT,"
                        + "tags TEXT,"
                        + "categories TEXT);";
                statement.executeUpdate(sql);
            }
            System.out.println("Database created successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
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
