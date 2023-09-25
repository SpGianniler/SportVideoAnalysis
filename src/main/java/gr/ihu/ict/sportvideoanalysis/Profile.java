package gr.ihu.ict.sportvideoanalysis;

import java.util.ArrayList;

public class Profile {

    private String profName;
    private int listNo;
    ArrayList<String> listNames = new ArrayList<>();

    public Profile(String profName, int listNo, ArrayList<String> listNames) {
        this.profName = profName;
        this.listNo = listNo;
        this.listNames = listNames;
    }

    public Profile() {
        this.profName = null;
        this.listNo = 0;
        this.listNames = null;
    }

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public int getListNo() {
        return listNo;
    }

    public void setListNo(int listNo) {
        this.listNo = listNo;
    }

    public ArrayList<String> getListNames() {
        return listNames;
    }

    public void setListNames(ArrayList<String> listNames) {
        this.listNames = listNames;
    }
}
