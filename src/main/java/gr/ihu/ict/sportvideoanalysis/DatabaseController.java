package gr.ihu.ict.sportvideoanalysis;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class DatabaseController implements Initializable {

    @FXML protected AnchorPane databasePane;
    @FXML protected Button createDbButton;
    @FXML protected Button loadDbButton;
    @FXML protected Button updateDbButton;
    @FXML protected Button checkDbButton;
    @FXML protected Button unsavedClipsButton;
    @FXML protected Button savedClipsButton;
    @FXML protected Button saveClipsButton;
    @FXML protected Button queryButton;
    @FXML protected Button saveHighlightButton;
    @FXML protected TextArea queryArea;
    private LabelListViewMapDTO labelListViewMapDTO;
    private ControllerManager controllerManager;
    private DatabaseManager databaseManager;

    public void initialize(URL url, ResourceBundle resourceBundle){
        databaseManager = new DatabaseManager(labelListViewMapDTO);
        //todo: get active profile and check if there is a database file with the same name and display its tables except for the one with the highlights,
        //      if not show message asking user if he wants to create a new database based on the profile and the loaded data in the listviews

    }

    public void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    public void setLabelListViewMapDTO(LabelListViewMapDTO labelListViewMapDTO) {
        this.labelListViewMapDTO = labelListViewMapDTO;
    }
    public void createDatabaseOnAction(){
        //todo: create a new database based on the profile and the loaded data in the listviews
        System.out.println("test");
        databaseManager.setLabelListViewMapDTO(labelListViewMapDTO);
        databaseManager.createDatabase();
    }

    private void loadDatabase(){
        //todo: load a database
    }

    private void updateDatabase(){
        //todo: update current database with new data(add the missing tables or entries and delete missing after showing message that there are missing entries or tables)
    }

    private void checkDatabase(){
        //todo: check database to see if the listview contents match the contents of the database
    }
}
