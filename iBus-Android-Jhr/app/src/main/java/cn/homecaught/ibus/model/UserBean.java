package cn.homecaught.ibus.model;

import org.json.JSONObject;

/**
 * Created by a1 on 16/6/22.
 */
public class UserBean  implements Cloneable  {
    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String userMobile;
    private String userFirstName;
    private String userLastName;
    private String userHead;
    private String userToken;
    private String userRole;
    private String userRealName;
    private String userSN;
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


    public String getUserSN() {
        return userSN;
    }

    public void setUserSN(String userSN) {
        this.userSN = userSN;
    }


    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }

    public String getUserOnBus() {
        return userOnBus;
    }

    public void setUserOnBus(String userOnBus) {
        this.userOnBus = userOnBus;
    }

    private String userGrade;
    private String userOnBus;

    public UserBean getGuardian() {
        return guardian;
    }

    public void setGuardian(UserBean guardian) {
        this.guardian = guardian;
    }

    private UserBean guardian;

    public UserBean clone()
    {
        UserBean o=null;
        try
        {
            o=(UserBean)super.clone();//Object 中的clone()识别出你要复制的是哪一个对象。
        }
        catch(CloneNotSupportedException e)
        {
            System.out.println(e.toString());
        }
        return o;
    }

    public UserBean(JSONObject jsonObject){
        if (jsonObject == null) return;
        try {
            id = jsonObject.getString("id");
            userMobile = jsonObject.getString("user_mobile");
            userFirstName = jsonObject.getString("user_first_name");
            userLastName = jsonObject.getString("user_last_name");
            userHead = jsonObject.getString("user_head");
            if (jsonObject.has("user_token"))
                userToken = jsonObject.getString("user_token");
            if (jsonObject.has("user_real_name"))
                userRealName = jsonObject.getString("user_real_name");
            if (jsonObject.has("user_sn"))
                userSN = jsonObject.getString("user_sn");
            if (jsonObject.has("user_grade"))
                userGrade = jsonObject.getString("user_grade");
            if (jsonObject.has("user_role"))
                userRole = jsonObject.getString("user_role");
            if (jsonObject.has("user_on_bus"))
                userOnBus = jsonObject.getString("user_on_bus");

            if (jsonObject.has("user_guardian_data"))
                if(jsonObject.get("user_guardian_data") instanceof JSONObject){
                    guardian = new UserBean(jsonObject.getJSONObject("user_guardian_data"));
                }
            if (jsonObject.has("user_on_line_data"))
                if(jsonObject.get("user_on_line_data") instanceof  JSONObject){
                    pickUpStop = new LineBean(jsonObject.getJSONObject("user_on_line_data"));
                }
            if (jsonObject.has("user_off_line_data"))
                if(jsonObject.get("user_off_line_data") instanceof  JSONObject){
                    pickOffStop = new LineBean(jsonObject.getJSONObject("user_off_line_data"));
                }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
