package cn.homecaught.ibus_saas.model;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by a1 on 2017/3/22.
 */

public class ChildBean implements Cloneable {
    private String id;
    private String firstName;
    private boolean isLeave;
    private String leaveType;

    public boolean isLeave() {
        return isLeave;
    }

    public void setLeave(boolean leave) {
        isLeave = leave;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    private StopBean pickUpStop;

    public StopBean getPickOffStop() {
        return pickOffStop;
    }

    public void setPickOffStop(StopBean pickOffStop) {
        this.pickOffStop = pickOffStop;
    }

    public StopBean getPickUpStop() {
        return pickUpStop;
    }

    public void setPickUpStop(StopBean pickUpStop) {
        this.pickUpStop = pickUpStop;
    }

    private StopBean pickOffStop;

    public UserBean getGuardian() {
        return guardian;
    }

    public void setGuardian(UserBean guardian) {
        this.guardian = guardian;
    }

    private String lastName;
    private String head;
    private String grade;
    private String SN;
    private String userOnBus;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private String nickName;

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
            lastName = jsonObject.getString("child_last_name");
            firstName = jsonObject.getString("child_first_name");
            head = jsonObject.getString("child_head");
            userOnBus = jsonObject.getString("user_on_bus");
            grade = jsonObject.getString("child_grade");
            nickName = jsonObject.getString("child_nick_name");
            SN = jsonObject.getString("child_sn");
            isLeave = jsonObject.getBoolean("is_leave");
            leaveType = jsonObject.getString("leave_type");

            if (jsonObject.has("child_on_site_data"))
                if(jsonObject.get("child_on_site_data") instanceof  JSONObject){
                    pickUpStop = new StopBean(jsonObject.getJSONObject("child_on_site_data"));
                }
            if (jsonObject.has("child_off_site_data"))
                if(jsonObject.get("child_off_site_data") instanceof  JSONObject){
                    pickOffStop = new StopBean(jsonObject.getJSONObject("child_off_site_data"));
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
