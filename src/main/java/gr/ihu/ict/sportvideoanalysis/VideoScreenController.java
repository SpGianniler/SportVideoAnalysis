package gr.ihu.ict.sportvideoanalysis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class VideoScreenController implements Initializable {
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
        System.out.println("Get record start time");
    }

    public void recStopButtonOnAction(){
        System.out.println("Get record stop time");
    }

    public void addRecButtonOnAction(){
        System.out.println("Add record to db");
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
            e.getCause();
        }
    }

}