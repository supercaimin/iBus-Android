package cn.homecaught.ibus_jhr.model;

import com.bin.david.form.annotation.SmartColumn;

public class Route {
    @SmartColumn(id=2,name = "Route")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusNum() {
        return busNum;
    }

    public void setBusNum(String busNum) {
        this.busNum = busNum;
    }

    public String getBusLines() {
        return busLines;
    }

    public void setBusLines(String busLines) {
        this.busLines = busLines;
    }

    public String getCompound() {
        return compound;
    }

    public void setCompound(String compound) {
        this.compound = compound;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @SmartColumn(id=3,name = "Bus Num")
    private String busNum;
    @SmartColumn(id=4,name = "Bus Lines")
    private String busLines;
    @SmartColumn(id=5,name = "Compound")
    private String compound;
    @SmartColumn(id=6,name = "Time")
    private String time;

    //Bus Num./车号	Bus Lines/路线	Compound/站点	Time/时间

}
