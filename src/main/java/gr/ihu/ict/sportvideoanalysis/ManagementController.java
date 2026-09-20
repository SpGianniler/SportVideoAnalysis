package gr.ihu.ict.sportvideoanalysis;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import static gr.ihu.ict.sportvideoanalysis.Main.*;

public class ManagementController implements Initializable {

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

    public void initialize(URL url, ResourceBundle resourceBundle){

        assert activeProfile != null;
        profNameText.setText(activeProfile.getProfName());
        StringBuilder stringBuilder = new StringBuilder();
        for (String element : activeProfile.listNames){
            if (!stringBuilder.isEmpty()){
                stringBuilder.append(", ");
            }
            stringBuilder.append(element);
        }
        listNoText.setText( Integer.toString(activeProfile.getListNo()));
        listsNameText.setText(stringBuilder.toString());

    }
    public ManagementController() {
        fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Json Files", "*.json"));

    }

    public void loadLabelOnAction(){
        File selectedFile = fileChooser.showOpenDialog(managementPane.getScene().getWindow());
        if (isValidFile(selectedFile, Collections.singletonList("json"))){
            JsonParser jsonParser = new JsonParser();
            Profile loadedProfile = jsonParser.importFromJson(selectedFile.getAbsolutePath());
            if (loadedProfile != null) {
                Main.activeProfile = loadedProfile;
                profNameText.setText(loadedProfile.getProfName());
                listNoText.setText(String.valueOf(loadedProfile.getListNo()));
                StringBuilder sb = new StringBuilder();
                for (String s : loadedProfile.getListNames()) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(s);
                }
                listsNameText.setText(sb.toString());
                Main.showErrorDialog("Profile Loaded", "Loaded profile: " + loadedProfile.getProfName());
            } else {
                Main.showErrorDialog("Load Failed", "Could not load profile from file.");
            }
        } else {
            String title = "Wrong file type";
            String message = "Please make sure you have selected a valid .json file";
            Main.showErrorDialog(title, message);
        }
    }

    public void closeLabelOnAction(){
        Stage stage = (Stage) closeLabel.getScene().getWindow();
        stage.close();
    }

    public void saveLabelOnClick(){
        Profile exportedProfile = getProfileTextValues();
        JsonParser jsonParser = new JsonParser();
        jsonParser.exportToJson(exportedProfile, DEFAULT_PROFILES_DIRECTORY, exportedProfile.getProfName());
    }

    public void saveAsLabelOnClick(){
        File file = fileChooser.showSaveDialog(managementPane.getScene().getWindow());
        JsonParser jsonParser = new JsonParser();
        if (file != null){
           Profile exportedProfile = getProfileTextValues();
           String filePath = file.getParent();
           String name = file.getName();
           name = FilenameUtils.removeExtension(name);
            jsonParser.exportToJson(exportedProfile, filePath, name);
        }
    }

    public void setAsActiveOnClick(){
//        VideoScreenController.createListViews(); //maybe create the same in managementController
        Main.activeProfile = getProfileTextValues();
        VideoScreenController.refreshProfile();
    }

    public void setSetAsActiveSaveBtn(){
        // placeholder
        saveLabelOnClick();
        setAsActiveOnClick();
        //todo: implement video screen refactor after changing the active profile
    }

    protected Profile getProfileTextValues(){
        Profile profile = new Profile();
        String name = profNameText.getText() == null ? "" : profNameText.getText().trim();
        if (name.isEmpty()) {
            Main.showErrorDialog("Invalid Profile", "Profile name cannot be empty.");
            throw new IllegalArgumentException("Profile name empty");
        }
        profile.setProfName(name);

        String listNoTextVal = listNoText.getText();
        int listNo = 0;
        try {
            listNo = Integer.parseInt(listNoTextVal.trim());
            if (listNo <= 0) throw new IllegalArgumentException();
        } catch (Exception e) {
            Main.showErrorDialog("Invalid Number", "List number must be a positive integer.");
            throw new IllegalArgumentException("Invalid listNo");
        }
        profile.setListNo(listNo);

        String inputText = listsNameText.getText();
        ArrayList<String> parts = new ArrayList<>();
        if (inputText != null) {
            for (String part : inputText.split(",")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) parts.add(trimmed);
            }
        }
        if (parts.isEmpty()) {
            Main.showErrorDialog("Invalid Lists", "At least one list name is required.");
            throw new IllegalArgumentException("Empty list names");
        }
        if (parts.size() > listNo) {
            Main.showErrorDialog("List Mismatch", "There seem to be more list names (" + parts.size() + ") than the number of lists (" + listNo + "). Please fix it.");
            // handled delicately — user sees modal and can fix
        }
        profile.setListNames(parts);
        return profile;
    }



}
