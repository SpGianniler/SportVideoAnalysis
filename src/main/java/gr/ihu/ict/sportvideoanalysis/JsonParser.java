package gr.ihu.ict.sportvideoanalysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

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
            Main.handleException(ex);
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
}

