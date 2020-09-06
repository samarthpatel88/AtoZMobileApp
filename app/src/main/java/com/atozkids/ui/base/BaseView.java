package com.atozkids.ui.base;


public interface BaseView {
    /**
     * show error message
     * @param msg  msg to display
     */
    void showErrorMsg(String msg);

    /**
     * show error message
     * @param msg  msg string resource id
     */
    void showErrorMsg(int msg);

    /**
     * show success message
     * @param msg  msg to display
     */
    void showSuccessMsg(String msg);

    /**
     * show error message
     * @param msg  msg string resource id
     */
    void showSuccessMsg(int msg);

    /**
     * show loading indicator
     */
    void showLoading();

    /**
     * hide loading indicator
     */
    void hideLoading();
}
