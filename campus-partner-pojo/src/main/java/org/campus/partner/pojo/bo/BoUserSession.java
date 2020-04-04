package org.campus.partner.pojo.bo;

public class BoUserSession {

    private String sessionId;
    private String openId;
    private Long lastAccessedDatetime;
    private Long maxInactiveInterval;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getLastAccessedDatetime() {
        return lastAccessedDatetime;
    }

    public void setLastAccessedDatetime(Long lastAccessedDatetime) {
        this.lastAccessedDatetime = lastAccessedDatetime;
    }

    public Long getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(Long maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

}
