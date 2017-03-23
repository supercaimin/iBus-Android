package cn.homecaught.ibus_jhr.model;

import org.json.JSONObject;

/**
 * Created by a1 on 16/6/26.
 */
public class UgrentBean {

    String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String name;

    public UgrentBean(JSONObject jsonObject){
        try {
            setId(jsonObject.getString("id"));
            setName(jsonObject.getString("option_title"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
