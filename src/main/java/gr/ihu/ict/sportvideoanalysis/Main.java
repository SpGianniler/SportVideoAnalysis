package gr.ihu.ict.sportvideoanalysis;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Main extends Application {

    public static final String EXE_LOCATION = System.getProperty("user.dir");
    public static final String DEFAULT_PROFILES_DIRECTORY = EXE_LOCATION + "\\src\\main\\resources\\profiles";
    public static final String DEFAULT_PROFILE = EXE_LOCATION + "\\src\\main\\resources\\profiles\\defaultProfile.json";
//    public static final String DEFAULT_PROFILE = EXE_LOCATION + "\\src\\main\\resources\\profiles\\defaultProfile.json";
    private double xOffset = 0;
    private double yOffset = 0;

    public static Profile activeProfile;


    @Override
    public void start(Stage primaryStage) throws Exception {
        activeProfile = JsonParser.importFromJson(DEFAULT_PROFILE);
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(root);

        root.setOnMousePressed(event->{
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        primaryStage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void showErrorDialog(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static void catastrophicErrorDialog(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.setOnHidden(event -> Platform.exit());

        alert.showAndWait();
    }

    public static void handleException(Exception e){
        try{
            LocalDateTime now = LocalDateTime.now();
            String formattedTimeStamp = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            FileWriter fileWriter = new FileWriter("exception-"+formattedTimeStamp+".txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            e.printStackTrace();
            printWriter.close();
        } catch (IOException ex) {
            Main.handleException(ex);
            catastrophicErrorDialog("Error", String.valueOf(ex.getCause()));
        }
    }

    public static boolean isValidFile(File file){
        if (file != null && file.isFile()){
            //Checking if file is a ".json" file
            String fileName = file.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1);

            return fileExtension.equalsIgnoreCase("json");
        }
        return false;
    }
}