package gr.ihu.ict.sportvideoanalysis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gr.ihu.ict.sportvideoanalysis.Main.isValidFile;

public class JsonParser {
    private JsonParser() {
        //empty private constructor
    }

    public static Profile importFromJson(String jsonFileName) {
        try {
            // Create an ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the JSON file and convert it to a Profile object
            File jsonFile = new File(jsonFileName);
            return objectMapper.readValue(jsonFile, Profile.class);
        } catch (IOException ex) {
//            Main.handleException(ex);
            ex.printStackTrace();
            return null;
        }
    }

    public static void exportToJson(Profile profile, String filepath, String name){
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.writeValue(new File(filepath +"\\"+ name +".json"), profile);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void loadAndPopulateOne(ListView<String> listView){
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        List<String> allowedFileTypes = new ArrayList<>();
        allowedFileTypes.add("json");
        if(isValidFile(selectedFile,allowedFileTypes)){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> data = objectMapper.readValue(selectedFile, new TypeReference<List<String>>() {});
                listView.getItems().setAll(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadAndPopulateDataFromSingleJSON(File selectedFile, Map<String, ListView<String>> labelToListViewMap) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<String>> dataMap = objectMapper.readValue(selectedFile, new TypeReference<Map<String, List<String>>>() {});

            // Iterate through the label names and associated ListViews
            for (Map.Entry<String, ListView<String>> entry : labelToListViewMap.entrySet()) {
                String labelName = entry.getKey();
                ListView<String> listView = entry.getValue();

                // Check if the label name exists in the JSON data map
                if (dataMap.containsKey(labelName)) {
                    List<String> data = dataMap.get(labelName);
                    listView.getItems().setAll(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadAndPopulateAll(Map<String, ListView<String>> labelToListViewMap){
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        List<String> allowedFileTypes = new ArrayList<>();
        allowedFileTypes.add("json");
        File selectedFile = fileChooser.showOpenDialog(null);
        if(isValidFile(selectedFile,allowedFileTypes)){
            loadAndPopulateDataFromSingleJSON(selectedFile, labelToListViewMap);
        }

    }
}

