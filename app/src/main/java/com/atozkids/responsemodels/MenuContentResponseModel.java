package com.atozkids.responsemodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class MenuContentResponseModel implements Serializable {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data implements Serializable {

        @SerializedName("menubar")
        @Expose
        private List<Menubar> menubar = null;
        @SerializedName("privacypolicy")
        @Expose
        private String privacypolicy;
        @SerializedName("contactus")
        @Expose
        private String contactus;
        @SerializedName("share")
        @Expose
        private String share;

        public List<Menubar> getMenubar() {
            return menubar;
        }

        public void setMenubar(List<Menubar> menubar) {
            this.menubar = menubar;
        }

        public String getPrivacypolicy() {
            return privacypolicy;
        }

        public void setPrivacypolicy(String privacypolicy) {
            this.privacypolicy = privacypolicy;
        }

        public String getContactus() {
            return contactus;
        }

        public void setContactus(String contactus) {
            this.contactus = contactus;
        }

        public String getShare() {
            return share;
        }

        public void setShare(String share) {
            this.share = share;
        }

        public class Menubar implements Serializable {

            @SerializedName("title")
            @Expose
            private String title;
            @SerializedName("menuimage")
            @Expose
            private String menuimage;
            @SerializedName("caption")
            @Expose
            private String caption;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getMenuimage() {
                return menuimage;
            }

            public void setMenuimage(String menuimage) {
                this.menuimage = menuimage;
            }

            public String getCaption() {
                return caption;
            }

            public void setCaption(String caption) {
                this.caption = caption;
            }

        }
    }

}
