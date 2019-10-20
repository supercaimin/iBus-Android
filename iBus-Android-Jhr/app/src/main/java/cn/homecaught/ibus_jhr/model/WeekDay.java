package cn.homecaught.ibus_jhr.model;

import com.bin.david.form.annotation.ColumnType;
import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

import java.util.List;

@SmartTable(name="Child's weekly Schedule")
public class WeekDay {
    @SmartColumn(id=1, name = "Date")
    private String name;

    @SmartColumn(type = ColumnType.ArrayChild)
    private List<Route> routes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
