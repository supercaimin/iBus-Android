package cn.homecaught.ibus_saas_wx.model;

import org.json.JSONObject;

/**
 * Created by a1 on 16/6/27.
 */
public class StopBean {

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

    public StopBean(JSONObject jsonObject){
        try {
            id = jsonObject.getString("id");
            lineName = jsonObject.getString("site_name");
            lineSite = jsonObject.getString("site_en_name");
            //lineDistance = jsonObject.getString("line_distance");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
