package gr.ihu.ict.sportvideoanalysis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gr.ihu.ict.sportvideoanalysis.JsonParser.loadAndPopulateAll;
import static gr.ihu.ict.sportvideoanalysis.JsonParser.loadAndPopulateOne;
import static gr.ihu.ict.sportvideoanalysis.Main.activeProfile;
import static gr.ihu.ict.sportvideoanalysis.Main.isValidFile;

public class VideoScreenController implements Initializable {
    @FXML protected AnchorPane videoPane;
    @FXML protected Label videoScreenTitle;
    @FXML protected Label managementLabel;
    @FXML protected Label databaseLabel;
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
    @FXML protected HBox listViewContainer;
    @FXML protected Label videoLengthLabel;
    @FXML protected TextField videoCurrentText;
    @FXML protected Slider seekSlider;
    @FXML protected Slider volumeSlider;

    FileChooser videoChooser = new FileChooser();
    private File selectedFile;
    private Media media;
    private MediaPlayer mediaPlayer;
    private Map<String, ListView<String>> labelToListViewMap = new HashMap<>();



    public VideoScreenController(){
        videoChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.flv", "*.avi"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createListViews();
        volumeSlider.setValue(0.5);
        // Bind the volume slider to the MediaPlayer's volume property
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });
    }

    public void powerButtonOnAction(){
        Stage stage = (Stage) powerLink.getScene().getWindow();
        stage.close();
    }

    public void loadVideo(){
        selectedFile = videoChooser.showOpenDialog(videoPane.getScene().getWindow());
        List<String> allowedFileTypes = new ArrayList<>();
        allowedFileTypes.add("mp4");
        allowedFileTypes.add("avi");
        allowedFileTypes.add("flv");
        if (isValidFile(selectedFile, allowedFileTypes)){
            String file = selectedFile.getAbsolutePath();
            String title = "Check Check"; // temporary check to make sure that the program reads the correct file
            Main.showErrorDialog(title,"File Name \n" + file);
        }
        else{
            String title = "Wrong file type";
            String message = "Please make sure you have selected a valid video file";
            Main.showErrorDialog(title,message);
        }

    }

    public void loadVideoButtonOnaAction(){
        loadVideo();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        media = new Media(selectedFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

    }

    public void playButtonOnAction(){
        if(checkVideoLoaded()){
            mediaPlayer.play();
            mediaView.setMediaPlayer(mediaPlayer);
            Duration lengthDur = media.getDuration();
            String length = formatTime(lengthDur);
            videoLengthLabel.setText(length);

            // Bind the seek slider to the video's current time
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                double currentTimeInSeconds = newValue.toSeconds();
                seekSlider.setValue(currentTimeInSeconds);

                // Update the current time TextField
                videoCurrentText.setText(formatTime(newValue));
            });

            // Set the maximum value of the seek slider to the video's total duration in seconds
            double totalDurationInSeconds = lengthDur.toSeconds();
            seekSlider.setMax(totalDurationInSeconds);

            // Set the initial value of the seek slider to 0
            seekSlider.setValue(0);

            // Handle seek slider changes
            seekSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (seekSlider.isValueChanging()) {
                    double newTimeInSeconds = newValue.doubleValue();
                    mediaPlayer.seek(new Duration(newTimeInSeconds * 1000));
                }
            });

            // Update the seek slider text in real-time as the user interacts with it
            seekSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    double currentTimeInSeconds = seekSlider.getValue();
                    seekSlider.setTooltip(new Tooltip(formatTime(new Duration((long) (currentTimeInSeconds * 1000)))));
                } else {
                    seekSlider.setTooltip(null);
                }
            });
        }
    }

    public void pauseButtonOnAction(){
        if (checkVideoLoaded()) {
            mediaPlayer.pause();
        }
    }

    public void stopButtonOnAction(){
        if(checkVideoLoaded()){
            mediaPlayer.stop();
        }
    }

    public void recStartButtonOnAction(){
        if(checkVideoLoaded()){
            Duration currentTime = mediaPlayer.getCurrentTime();
            String formattedTime = formatTime(currentTime);
            startTimeTextField.setText(formattedTime);
        }
    }


    public void recStopButtonOnAction(){
        if(checkVideoLoaded()){
            Duration currentTime = mediaPlayer.getCurrentTime();
            String formattedTime = formatTime(currentTime);
            endTimeTextField.setText(formattedTime);
        }
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
            e.printStackTrace();
        }
    }

    public void databaseOnAction() {
        openDatabaseManagement();
    }
    public void openDatabaseManagement(){
        try{
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("databaseView.fxml")));
            Stage managementStage = new Stage();
            managementStage.initStyle(StageStyle.TRANSPARENT);
            Scene managementScene = new Scene(root);

            managementStage.setScene(managementScene);
            managementScene.setFill(Color.TRANSPARENT);
            managementStage.initModality(Modality.APPLICATION_MODAL);
            managementStage.setTitle("Database Manager");

            managementStage.showAndWait();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createListViews(){
        listViewContainer.getChildren().clear();

        for(int i = 0; i < activeProfile.getListNo(); i++){
            VBox vBox = new VBox();
            ListView<String> listView = new ListView<>();
            Label label = new Label(activeProfile.getListNames().get(i));
            // Set the selection mode of the ListView to MULTIPLE
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


            //Customize label properties
            label.setMinHeight(50);
            label.setFont(new Font(14));
            label.setTextFill(Color.WHITE);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-background-color: E96151; -fx-border-color: #FDF5E6; -fx-border-width: 0.1px;");
            addContextMenu(label,listView);

            listView.getItems().addAll("1","2");
            label.prefWidthProperty().bind(listView.widthProperty());
            vBox.getChildren().addAll(label, listView);
            listViewContainer.getChildren().add(vBox);

        }
    }



    private String formatTime(Duration time){
        long millis = (long) time.toMillis();
        LocalTime localTime = LocalTime.ofSecondOfDay(millis/1000);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return localTime.format(formatter);
    }

    private boolean checkVideoLoaded(){
        if (mediaPlayer == null) {
            String title = "No video file loaded";
            String message = "Please make sure you have loaded a valid video file";
            Main.showErrorDialog(title,message);
            return false;
        }
        return true;
    }

    // Parse time in HH:mm:ss format and return as a Duration
    private Duration parseTime(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime localTime = LocalTime.parse(time, formatter);
            return Duration.seconds(localTime.toSecondOfDay());
        } catch (Exception e) {
            // Handle parsing errors (invalid format)
            return null;
        }
    }

    @FXML
    private void handleTimeInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            // Parse the user's input and seek to the specified time
            String inputTime = videoCurrentText.getText().trim();
            Duration seekTime = parseTime(inputTime);
            if (seekTime != null) {
                mediaPlayer.seek(seekTime);
            }
        }
    }

    private void addContextMenu(Label label, ListView<String> listView) {
        ContextMenu contextMenu = new ContextMenu();

        // Add the label and its associated ListView to the map
        labelToListViewMap.put(label.getText(), listView);

        MenuItem loadIntoSingleList = new MenuItem("load Data into "+ label.getText());
        loadIntoSingleList.setOnAction(event -> loadAndPopulateOne(listView));

        MenuItem loadIntoMultipleList = new MenuItem("load Data into all lists");
        loadIntoMultipleList.setOnAction(event -> loadAndPopulateAll(labelToListViewMap));

        contextMenu.getItems().addAll(loadIntoSingleList,loadIntoMultipleList);
        label.setContextMenu(contextMenu);
    }






}