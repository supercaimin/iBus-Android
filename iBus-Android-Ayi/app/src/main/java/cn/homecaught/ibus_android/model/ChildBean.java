package cn.homecaught.ibus_android.model;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by a1 on 2017/3/22.
 */

public class ChildBean implements Cloneable {
    private String id;
    private String firstName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFastName() {
        return fastName;
    }

    public void setFastName(String fastName) {
        this.fastName = fastName;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getUserOnBus() {
        return userOnBus;
    }

    public void setUserOnBus(String userOnBus) {
        this.userOnBus = userOnBus;
    }

    private LineBean pickUpStop;

    public LineBean getPickOffStop() {
        return pickOffStop;
    }

    public void setPickOffStop(LineBean pickOffStop) {
        this.pickOffStop = pickOffStop;
    }

    public LineBean getPickUpStop() {
        return pickUpStop;
    }

    public void setPickUpStop(LineBean pickUpStop) {
        this.pickUpStop = pickUpStop;
    }

    private LineBean pickOffStop;

    public UserBean getGuardian() {
        return guardian;
    }

    public void setGuardian(UserBean guardian) {
        this.guardian = guardian;
    }

    private String fastName;
    private String head;
    private String grade;
    private String SN;
    private String userOnBus;

    private UserBean guardian;
    public ChildBean clone()
    {
        ChildBean o=null;
        try
        {
            o=(ChildBean)super.clone();//Object 中的clone()识别出你要复制的是哪一个对象。
        }
        catch(CloneNotSupportedException e)
        {
            System.out.println(e.toString());
        }
        return o;
    }

    public ChildBean(JSONObject jsonObject){
        if (jsonObject == null) return;
        try {
            id = jsonObject.getString("id");
            fastName = jsonObject.getString("child_last_name");
            firstName = jsonObject.getString("child_first_name");
            head = jsonObject.getString("child_head");
            userOnBus = jsonObject.getString("user_on_bus");
            grade = jsonObject.getString("child_grade");
            SN = jsonObject.getString("child_sn");
            if (jsonObject.has("child_on_site_data"))
                if(jsonObject.get("child_on_site_data") instanceof  JSONObject){
                    pickUpStop = new LineBean(jsonObject.getJSONObject("child_on_site_data"));
                }
            if (jsonObject.has("child_off_site_data"))
                if(jsonObject.get("child_off_site_data") instanceof  JSONObject){
                    pickOffStop = new LineBean(jsonObject.getJSONObject("child_off_site_data"));
                }

            if (jsonObject.has("child_guardians"))
                if(jsonObject.get("child_guardians") instanceof JSONArray){
                    if (jsonObject.getJSONArray("child_guardians").length() != 0)
                    guardian = new UserBean(jsonObject.getJSONArray("child_guardians").getJSONObject(0));
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
