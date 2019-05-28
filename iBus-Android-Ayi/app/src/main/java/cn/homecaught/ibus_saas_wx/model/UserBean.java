package cn.homecaught.ibus_saas_wx.model;

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
    private String userRealName;


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
            else {
                userRealName = userFirstName + " " + userLastName;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
