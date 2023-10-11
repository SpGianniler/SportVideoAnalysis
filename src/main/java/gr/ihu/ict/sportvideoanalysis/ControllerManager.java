package gr.ihu.ict.sportvideoanalysis;

public class ControllerManager {
    private VideoScreenController videoScreenController;
    private DatabaseController databaseController;

    private LabelListViewMapDTO labelListViewMapDTO;

    public void setVideoScreenController(VideoScreenController controller) {
        this.videoScreenController = controller;
    }

    public void setDatabaseController(DatabaseController controller) {
        this.databaseController = controller;
    }

    public LabelListViewMapDTO getLabelListViewMapDTO() {
        return labelListViewMapDTO;
    }

    public void setLabelListViewMapDTO(LabelListViewMapDTO labelListViewMapDTO) {
        this.labelListViewMapDTO = labelListViewMapDTO;
    }
}
