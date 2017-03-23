package cn.homecaught.ibus_saas.model;

import org.json.JSONObject;

/**
 * Created by a1 on 2017/3/21.
 */

public class SchoolBean {
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolDomain() {
        return schoolDomain;
    }

    public void setSchoolDomain(String schoolDomain) {
        this.schoolDomain = schoolDomain;
    }

    public String getSchoolLogo() {
        return schoolLogo;
    }

    public void setSchoolLogo(String schoolLogo) {
        this.schoolLogo = schoolLogo;
    }

    String schoolName;
    String schoolDomain;
    String schoolLogo;

    public SchoolBean(JSONObject jsonObject){
        try {
            setId(jsonObject.getString("id"));
            setSchoolName(jsonObject.getString("school_name"));
            setSchoolDomain(jsonObject.getString("school_domain"));
            setSchoolLogo(jsonObject.getString("school_logo"));

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
