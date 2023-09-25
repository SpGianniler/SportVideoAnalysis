package gr.ihu.ict.sportvideoanalysis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static gr.ihu.ict.sportvideoanalysis.Main.isValidFile;

public class VideoScreenController implements Initializable {
    @FXML protected AnchorPane videoPane;
    @FXML protected ImageView image;
    @FXML protected Label videoScreenTitle;
    @FXML protected Label managementLabel;
    @FXML protected Hyperlink powerLink;
    @FXML protected Button playButton;
    @FXML protected Button pauseButton;
    @FXML protected Button stopButton;
    @FXML protected Button recStartButton;
    @FXML protected Button recStopButton;
    @FXML protected Button addRecButton;
    @FXML protected Label startTimeLabel;
    @FXML protected Label endTimeLabel;
    @FXML protected Label loadVideoLabel;
    @FXML protected TextField startTimeTextField;
    @FXML protected TextField endTimeTextField;
    @FXML protected MediaView mediaView;
    @FXML protected VBox listsBox;
    @FXML public ListView<String> listView1;
    @FXML public ListView<String> listView2;

    FileChooser videoChooser = new FileChooser();
    private File selectedFile;
    private Media media;
    private MediaPlayer mediaPlayer;


    public VideoScreenController(){
        videoChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.flv", "*.avi"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedFile = new File("src/main/resources/vids/testVid.mp4");
        media = new Media(selectedFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
//        File chartFile = new File("src/main/resources/images/download.jpg");
//        Image chartImage = new Image(chartFile.toURI().toString());
//        image.setImage(chartImage);
        listView1.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView1.getItems().add("Karagounis");
        listView1.getItems().add("Katsouranis");
        listView2.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView2.getItems().add("Shot");
        listView2.getItems().add("Pass");
    }

    public void powerButtonOnAction(){
        Stage stage = (Stage) powerLink.getScene().getWindow();
        stage.close();
    }

    public void loadVideo(){
        selectedFile = videoChooser.showOpenDialog(videoPane.getScene().getWindow());
        if (isValidFile(selectedFile)){
            String file = selectedFile.getAbsolutePath();
            String title = "Check Check"; // temporary check to make sure that the program reads the correct file
            Main.showErrorDialog(title,"File Name \n" + file);
        }
        else{
            String title = "Wrong file type";
            String message = "Please make sure you have selected a valid .json file";
            Main.showErrorDialog(title,message);
        }

    }

    public void playButtonOnAction(){
        mediaPlayer.play();
        mediaView.setMediaPlayer(mediaPlayer);
    }

    public void pauseButtonOnAction(){
        mediaPlayer.pause();
    }

    public void stopButtonOnAction(){
        mediaPlayer.stop();
    }

    public void recStartButtonOnAction(){
        Duration currentTime = mediaPlayer.getCurrentTime();
        String formattedTime = formatTime(currentTime);
        startTimeTextField.setText(formattedTime);
    }


    public void recStopButtonOnAction(){
        Duration currentTime = mediaPlayer.getCurrentTime();
        String formattedTime = formatTime(currentTime);
        endTimeTextField.setText(formattedTime);
    }

    public void addRecButtonOnAction(){
        Main.showErrorDialog("Check","Add record to db");
    }

    public void managementOnAction() {
        openManagement();
    }
    public void openManagement(){
        try{
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("managementView.fxml")));
            Stage managementStage = new Stage();
            managementStage.initStyle(StageStyle.TRANSPARENT);
            Scene managementScene = new Scene(root);

            managementStage.setScene(managementScene);
            managementScene.setFill(Color.TRANSPARENT);
            managementStage.initModality(Modality.APPLICATION_MODAL);
            managementStage.setTitle("Profile Manager");

            managementStage.showAndWait();
        }catch (Exception e){
            Main.handleException(e);
            e.getCause();
        }
    }

    private String formatTime(Duration time){
        long millis = (long) time.toMillis();
        LocalTime localTime = LocalTime.ofSecondOfDay(millis/1000);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return localTime.format(formatter);
    }

}