package cn.homecaught.ibus_saas.model;

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
    public LineBean(JSONObject jsonObject){
        try {
            id = jsonObject.getString("id");
            lineName = jsonObject.getString("line_name");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
