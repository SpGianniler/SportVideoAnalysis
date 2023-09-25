package gr.ihu.ict.sportvideoanalysis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;

import static gr.ihu.ict.sportvideoanalysis.Main.DEFAULT_PROFILES_DIRECTORY;
import static gr.ihu.ict.sportvideoanalysis.Main.isValidFile;

public class ManagementController{

    @FXML protected Label newLabel;
    @FXML protected Label loadLabel;
    @FXML protected Label saveLabel;
    @FXML protected Label saveAsLabel;
    @FXML protected Label closeLabel;
    @FXML protected AnchorPane managementPane;
    @FXML protected TextField profNameText;
    @FXML protected TextField listNoText;
    @FXML protected TextField listsNameText;
    @FXML protected Button setAsActiveBtn;
    @FXML protected Button setAsActiveSaveBtn;

    FileChooser fileChooser = new FileChooser();
    File initialDirectory = new File(DEFAULT_PROFILES_DIRECTORY);

    public ManagementController() {
        fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Json Files", "*.json"));
    }

    public void loadLabelOnAction(){
        File selectedFile = fileChooser.showOpenDialog(managementPane.getScene().getWindow());
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

    public void closeLabelOnAction(){
        Stage stage = (Stage) closeLabel.getScene().getWindow();
        stage.close();
    }

    public void saveLabelOnClick(){
        Profile exportedProfile = getProfileTextValues();
        JsonParser.exportToJson(exportedProfile, DEFAULT_PROFILES_DIRECTORY, exportedProfile.getProfName());
    }

    public void saveAsLabelOnClick(){
        File file = fileChooser.showSaveDialog(managementPane.getScene().getWindow());
        if (file != null){
           Profile exportedProfile = getProfileTextValues();
           String filePath = file.getParent();
           String name = file.getName();
           name = FilenameUtils.removeExtension(name);
           JsonParser.exportToJson(exportedProfile, filePath, name);
        }
    }

    public void setAsActiveOnClick(){
        Main.activeProfile = getProfileTextValues();
        //todo: implement video screen refactor after changing the active profile
    }

    public void setSetAsActiveSaveBtn(){
        // placeholder
        saveLabelOnClick();
        setAsActiveOnClick();
        //todo: implement video screen refactor after changing the active profile
    }

    protected Profile getProfileTextValues(){
        Profile profile = new Profile();

        String inputText = listsNameText.getText();
        String[] splitParts = inputText.split(",");

        ArrayList<String> parts = new ArrayList<>();
        for (String part : splitParts){
            parts.add(part.trim());
        }

        profile.setProfName(profNameText.getText());
        profile.setListNo(Integer.parseInt(listNoText.getText()));
        profile.setListNames(parts);

        return profile;
    }



}
