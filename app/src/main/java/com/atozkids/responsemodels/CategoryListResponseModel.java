package com.atozkids.responsemodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CategoryListResponseModel implements Serializable {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public class Datum implements Serializable {

        @SerializedName("categoryid")
        @Expose
        private Integer categoryid;
        @SerializedName("categoryname")
        @Expose
        private String categoryname;
        @SerializedName("caption")
        @Expose
        private String caption;
        @SerializedName("categoryicon")
        @Expose
        private String categoryicon;
        @SerializedName("displayorder")
        @Expose
        private Integer displayorder;

        public Integer getCategoryid() {
            return categoryid;
        }

        public void setCategoryid(Integer categoryid) {
            this.categoryid = categoryid;
        }

        public String getCategoryname() {
            return categoryname;
        }

        public void setCategoryname(String categoryname) {
            this.categoryname = categoryname;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getCategoryicon() {
            return categoryicon;
        }

        public void setCategoryicon(String categoryicon) {
            this.categoryicon = categoryicon;
        }

        public Integer getDisplayorder() {
            return displayorder;
        }

        public void setDisplayorder(Integer displayorder) {
            this.displayorder = displayorder;
        }

    }
}

