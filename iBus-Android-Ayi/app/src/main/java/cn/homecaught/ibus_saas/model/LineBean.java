package cn.homecaught.ibus_saas.model;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by a1 on 2017/3/30.
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

    private String id;
    private String lineName;
    private String lineType;

    public JSONArray getSites() {
        return sites;
    }

    public void setSites(JSONArray sites) {
        this.sites = sites;
    }

    private JSONArray sites;

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public LineBean(JSONObject jsonObject){
        try {
            id = jsonObject.getString("id");
            lineName = jsonObject.getString("line_name");
            lineType = jsonObject.getString("line_type");
            sites =jsonObject.getJSONArray("line_sites");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
