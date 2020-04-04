package org.campus.partner.pojo.bo;

/**
 * 
 * 短信参数
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class SmsVars {

    private String startLocation;
    private String endLocation;

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }
}
