package ericconnect.sitehr;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Date;


/**
 * Person
 * <p>
 *
 *
 */
public class Person {

    @SerializedName("id")
    @Expose
    private String id = "unknown";
    @SerializedName("dpmt")
    @Expose
    private String dpmt = "Unknown Department";
    @SerializedName("name")
    @Expose
    private String name = "unknown";
    @SerializedName("birth")
    @Expose
    private Date birth = new Date();
    @SerializedName("phone")
    @Expose
    private String phone = "Unknown";
    @SerializedName("status")
    @Expose
    private Integer status = 0;
    @SerializedName("img_url")
    @Expose
    private String imgUrl = "http://127.0.0.1/";
    @SerializedName("signature")
    @Expose
    private String signature = "unknown"; //md5(id, name, dpmt, md5salt);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDpmt() {
        return this.dpmt;
    }

    public void setDpmt(String dpmt) {
        this.dpmt = dpmt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirth() {
        return this.birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSignature() { return signature;}

    public void setSignature(String signature) { this.signature = signature;}

}