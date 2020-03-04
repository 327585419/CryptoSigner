package com.zm.secretsign.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/8/14 14:08
 */
@Entity
public class AddressKey {
    //必须Long long会报id重复
    @Id(autoincrement = true)
    private Long id;
    private Long saveTime;
    private Long order;

    private String coinType;
    private String tag;
    private String address;
    private String key;

    @Generated(hash = 991451497)
    public AddressKey(Long id, Long saveTime, Long order, String coinType,
            String tag, String address, String key) {
        this.id = id;
        this.saveTime = saveTime;
        this.order = order;
        this.coinType = coinType;
        this.tag = tag;
        this.address = address;
        this.key = key;
    }

    @Generated(hash = 637071331)
    public AddressKey() {
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSaveTime() {
        return this.saveTime;
    }

    public void setSaveTime(Long saveTime) {
        this.saveTime = saveTime;
    }

    public Long getOrder() {
        return this.order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public String getCoinType() {
        return this.coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }


}
