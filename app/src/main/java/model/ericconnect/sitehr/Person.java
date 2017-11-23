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
    private String id;
    @SerializedName("dpmt")
    @Expose
    private String dpmt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("birth")
    @Expose
    private Date birth;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("img_url")
    @Expose
    private String imgUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDpmt() {
        return dpmt;
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
        return birth;
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

}