package com.zm.secretsign.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/8/15 11:16
 */
@Entity
public class Password {

    //必须Long long会报id重复
    @Id(autoincrement = true)
    private Long id;
    private String encryStr;
    private Long saveTime;
    private String sha256;

    @Generated(hash = 974248259)
    public Password(Long id, String encryStr, Long saveTime, String sha256) {
        this.id = id;
        this.encryStr = encryStr;
        this.saveTime = saveTime;
        this.sha256 = sha256;
    }

    @Generated(hash = 565943725)
    public Password() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEncryStr() {
        return this.encryStr;
    }

    public void setEncryStr(String encryStr) {
        this.encryStr = encryStr;
    }

    public Long getSaveTime() {
        return this.saveTime;
    }

    public void setSaveTime(Long saveTime) {
        this.saveTime = saveTime;
    }

    public String getSha256() {
        return this.sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }
}
