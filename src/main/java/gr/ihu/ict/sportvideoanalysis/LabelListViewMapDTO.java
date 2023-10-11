package gr.ihu.ict.sportvideoanalysis;

import javafx.scene.control.ListView;

import java.util.Map;

public class LabelListViewMapDTO {
    private Map<String, ListView<String>> LabelListViewMapDTO;

    public LabelListViewMapDTO(Map<String, ListView<String>> labelListViewMapDTO) {
        LabelListViewMapDTO = labelListViewMapDTO;
    }

    public LabelListViewMapDTO() {
        LabelListViewMapDTO = null;
    }

    public Map<String, ListView<String>> getLabelListViewMapDTO() {
        return LabelListViewMapDTO;
    }

    public void setLabelListViewMapDTO(Map<String, ListView<String>> labelListViewMapDTO) {
        LabelListViewMapDTO = labelListViewMapDTO;
    }
}
