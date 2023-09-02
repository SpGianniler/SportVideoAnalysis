package gr.ihu.ict.sportvideoanalysis;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ManagementController{

    @FXML protected Label newLabel;
    @FXML protected Label loadLabel;
    @FXML protected Label saveLabel;
    @FXML protected Label saveAsLabel;
    @FXML protected Label closeLabel;


    public void closeLabelOnAction(){
        Stage stage = (Stage) closeLabel.getScene().getWindow();
        stage.close();
    }
}
