package gr.ihu.ict.sportvideoanalysis;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class DatabaseController implements Initializable {

    @FXML
    protected AnchorPane databasePane;
    public void initialize(URL url, ResourceBundle resourceBundle){

        //todo: get active profile and check if there is a database file with the same name and display its tables except for the one with the highlights,
        //      if not show message asking user if he wants to create a new database based on the profile and the loaded data in the listviews

    }

    public DatabaseController() {}

    private void createDatabase(){
        //todo: create a new database based on the profile and the loaded data in the listviews
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
