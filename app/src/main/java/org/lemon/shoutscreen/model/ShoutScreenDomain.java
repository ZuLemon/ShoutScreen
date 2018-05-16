package org.lemon.shoutscreen.model;



/**
 * 上屏叫号实体
 */
public class ShoutScreenDomain {
    private Integer id;
    private String presId;
    private String patientId;
    private String patientName;
    private Integer status=0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPresId() {
        return presId;
    }

    public void setPresId(String presId) {
        this.presId = presId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
