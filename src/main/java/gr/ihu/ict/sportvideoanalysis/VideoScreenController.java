package gr.ihu.ict.sportvideoanalysis;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class VideoScreenController implements Initializable {
    @FXML protected ImageView image;
    @FXML protected Label videoScreenTitle;
    @FXML protected Hyperlink powerLink;
    @FXML protected Button playButton;
    @FXML protected Button pauseButton;
    @FXML protected Button stopButton;
    @FXML protected Button recStartButton;
    @FXML protected Button recStopButton;
    @FXML protected Button addRecButton;
    @FXML protected Label startTimeLabel;
    @FXML protected Label endTimeLabel;
    @FXML protected TextField startTimeTextField;
    @FXML protected TextField endTimeTextField;
    @FXML public ListView<String> listView1;
    @FXML public ListView<String> listView2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File chartFile = new File("src/main/resources/images/download.jpg");
        Image chartImage = new Image(chartFile.toURI().toString());
        image.setImage(chartImage);
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

    public void playButtonOnAction(){
        VideoPlayer.playVideo();
    }

    public void pauseButtonOnAction(){
        VideoPlayer.pauseVideo();
    }

    public void stopButtonOnAction(){
        VideoPlayer.stopVideo();
    }

    public void recStartButtonOnAction(){
        System.err.println("Get record start time");
    }

    public void recStopButtonOnAction(){
        System.err.println("Get record stop time");
    }

    public void addRecButtonOnAction(){
        System.err.println("Add record to db");
    }


}