package cn.homecaught.ibus_jhr.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by a1 on 16/6/27.
 */
public class LineBean {

    public  static final int CHILD_LINE_NORMAL = 0;
    public  static final int CHILD_LINE_UP = 1;
    public  static final int CHILD_LINE_OFF = 2;

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
    private String arrivedTime;
    private int childUpOff;

    public JSONArray getStops() {
        return stops;
    }

    public void setStops(JSONArray stops) {
        this.stops = stops;
    }

    private JSONArray stops;

    public String getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(String arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public int getChildUpOff() {
        return childUpOff;
    }

    public void setChildUpOff(int childUpOff) {
        this.childUpOff = childUpOff;
    }

    public LineBean(JSONObject jsonObject){
        try {
            id = jsonObject.getString("id");
            if (jsonObject.has("site_name"))
                lineName = jsonObject.getString("site_name");
            if (jsonObject.has("line_name"))
                lineName = jsonObject.getString("line_name");
            if (jsonObject.has("line_sites")) {
                stops = jsonObject.getJSONArray("line_sites");
            }
            if (jsonObject.has("site_en_name")) {
                lineSite = jsonObject.getString("site_en_name");
            }

            if (jsonObject.has("site_distance")) {
                lineDistance = jsonObject.getString("site_distance");
            }
            if (jsonObject.has("arrived_time")) {
                arrivedTime = jsonObject.getString("arrived_time");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
