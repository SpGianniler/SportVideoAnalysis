package gr.ihu.ict.sportvideoanalysis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class VideoScreenController implements Initializable {
    @FXML
    private Label videoScreenTitle;

    @FXML
    private Hyperlink powerLink;


    @FXML
    protected void onHelloButtonClick() {
        videoScreenTitle.setText("Video Analysis");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        nothing to initialize yet
    }

    public void powerButtonOnAction(ActionEvent actionEvent){
        Stage stage = (Stage) powerLink.getScene().getWindow();
        stage.close();
    }


}