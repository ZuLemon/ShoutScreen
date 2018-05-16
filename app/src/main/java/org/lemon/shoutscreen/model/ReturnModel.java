package org.lemon.shoutscreen.model;

/**
 * Created by Guang on 2017/1/10.
 */

/**
 * 服务器返回对象
 */
public class ReturnModel {
    private Boolean success = false;
    private Object object = null;
    private String exception = "";

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}