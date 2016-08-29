package com.iot.basesvr.model;

import java.util.Date;

public class FriendUserUser {
    private Integer id;

    private String friendkey;

    private Boolean status;

    private Date updatetime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFriendkey() {
        return friendkey;
    }

    public void setFriendkey(String friendkey) {
        this.friendkey = friendkey == null ? null : friendkey.trim();
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}