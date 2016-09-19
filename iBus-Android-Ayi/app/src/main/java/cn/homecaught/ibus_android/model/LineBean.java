package cn.homecaught.ibus_android.model;

import org.json.JSONObject;

/**
 * Created by a1 on 16/6/27.
 */
public class LineBean {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineSite() {
        return lineSite;
    }

    public void setLineSite(String lineSite) {
        this.lineSite = lineSite;
    }

    public String getLineDistance() {
        return lineDistance;
    }

    public void setLineDistance(String lineDistance) {
        this.lineDistance = lineDistance;
    }

    private String id;
    private String lineName;
    private String lineSite;
    private String lineDistance;

    public LineBean(JSONObject jsonObject){
        try {
            id = jsonObject.getString("id");
            lineName = jsonObject.getString("line_name");
            lineSite = jsonObject.getString("line_site");
            lineDistance = jsonObject.getString("line_distance");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
