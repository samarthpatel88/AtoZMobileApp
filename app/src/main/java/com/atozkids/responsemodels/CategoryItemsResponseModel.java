package com.atozkids.responsemodels;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryItemsResponseModel implements Serializable
{

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

    public class Datum implements Serializable
    {

        @SerializedName("itemid")
        @Expose
        private Integer itemid;
        @SerializedName("itemname")
        @Expose
        private String itemname;
        @SerializedName("itemimage")
        @Expose
        private String itemimage;
        @SerializedName("captionimage")
        @Expose
        private String captionimage;
        @SerializedName("audiomale")
        @Expose
        private String audiomale;
        @SerializedName("audiofemale")
        @Expose
        private String audiofemale;
        @SerializedName("displayorder")
        @Expose
        private Integer displayorder;

        public Integer getItemid() {
            return itemid;
        }

        public void setItemid(Integer itemid) {
            this.itemid = itemid;
        }

        public String getItemname() {
            return itemname;
        }

        public void setItemname(String itemname) {
            this.itemname = itemname;
        }

        public String getItemimage() {
            return itemimage;
        }

        public void setItemimage(String itemimage) {
            this.itemimage = itemimage;
        }

        public String getCaptionimage() {
            return captionimage;
        }

        public void setCaptionimage(String captionimage) {
            this.captionimage = captionimage;
        }

        public String getAudiomale() {
            return audiomale;
        }

        public void setAudiomale(String audiomale) {
            this.audiomale = audiomale;
        }

        public String getAudiofemale() {
            return audiofemale;
        }

        public void setAudiofemale(String audiofemale) {
            this.audiofemale = audiofemale;
        }

        public Integer getDisplayorder() {
            return displayorder;
        }

        public void setDisplayorder(Integer displayorder) {
            this.displayorder = displayorder;
        }

    }
}
