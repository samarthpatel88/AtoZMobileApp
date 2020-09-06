package com.atozkids.responsemodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RhymesResponseModel implements Serializable {

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

        @SerializedName("rhymesid")
        @Expose
        private Integer rhymesid;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("audio")
        @Expose
        private String audio;
        @SerializedName("displayorder")
        @Expose
        private Integer displayorder;

        public Integer getRhymesid() {
            return rhymesid;
        }

        public void setRhymesid(Integer rhymesid) {
            this.rhymesid = rhymesid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getAudio() {
            return audio;
        }

        public void setAudio(String audio) {
            this.audio = audio;
        }

        public Integer getDisplayorder() {
            return displayorder;
        }

        public void setDisplayorder(Integer displayorder) {
            this.displayorder = displayorder;
        }

    }
}
