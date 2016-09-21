package cn.homecaught.ibus.model;

import org.json.JSONObject;

/**
 * Created by a1 on 16/9/21.
 */
public class BusBean {
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public UserBean getDriver() {
        return driver;
    }

    public void setDriver(UserBean driver) {
        this.driver = driver;
    }

    public UserBean getAyi() {
        return ayi;
    }

    public void setAyi(UserBean ayi) {
        this.ayi = ayi;
    }

    public UserBean getManager() {
        return manager;
    }

    public void setManager(UserBean manager) {
        this.manager = manager;
    }

    String busNumber;
    UserBean driver;
    UserBean ayi;
    UserBean manager;

    public BusBean(JSONObject jsonObject){
        try {
            id = jsonObject.getString("id");
            busNumber = jsonObject.getString("bus_number");
            driver = new UserBean(jsonObject.getJSONObject("bus_driver_data"));
            ayi = new UserBean(jsonObject.getJSONObject("bus_aunt_data"));
            manager = new UserBean(jsonObject.getJSONObject("bus_manager_data"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
