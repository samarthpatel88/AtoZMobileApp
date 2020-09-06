package com.atozkids.responsemodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class QuizResponseModel implements Serializable {

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

        @SerializedName("QuestionItemImage")
        @Expose
        private String questionItemImage;
        @SerializedName("QuestionItemName")
        @Expose
        private String questionItemName;
        @SerializedName("QuestionItemId")
        @Expose
        private Integer questionItemId;
        @SerializedName("QuestionText")
        @Expose
        private String questionText;
        @SerializedName("Options")
        @Expose
        private List<Option> options = null;

        public String getQuestionItemImage() {
            return questionItemImage;
        }

        public void setQuestionItemImage(String questionItemImage) {
            this.questionItemImage = questionItemImage;
        }

        public String getQuestionItemName() {
            return questionItemName;
        }

        public void setQuestionItemName(String questionItemName) {
            this.questionItemName = questionItemName;
        }

        public Integer getQuestionItemId() {
            return questionItemId;
        }

        public void setQuestionItemId(Integer questionItemId) {
            this.questionItemId = questionItemId;
        }

        public String getQuestionText() {
            return questionText;
        }

        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }

        public List<Option> getOptions() {
            return options;
        }

        public void setOptions(List<Option> options) {
            this.options = options;
        }

        public class Option implements Serializable {

            @SerializedName("ItemImage")
            @Expose
            private String itemImage;
            @SerializedName("ItemName")
            @Expose
            private String itemName;
            @SerializedName("ItemId")
            @Expose
            private Integer itemId;
            @SerializedName("isCorrect")
            @Expose
            private Boolean isCorrect;

            public String getItemImage() {
                return itemImage;
            }

            public void setItemImage(String itemImage) {
                this.itemImage = itemImage;
            }

            public String getItemName() {
                return itemName;
            }

            public void setItemName(String itemName) {
                this.itemName = itemName;
            }

            public Integer getItemId() {
                return itemId;
            }

            public void setItemId(Integer itemId) {
                this.itemId = itemId;
            }

            public Boolean getIsCorrect() {
                return isCorrect;
            }

            public void setIsCorrect(Boolean isCorrect) {
                this.isCorrect = isCorrect;
            }

        }

    }
}
