package com.zm.secretsign.bean;


import com.chad.library.adapter.base.entity.MultiItemEntity;

public class DealSignItem implements MultiItemEntity {
    public static final int INPUT = 1;
    public static final int OUTPUT = 2;
    public static final int MSG = 3;

    //////二维码内容 start/////////////////////////////////////////
    private int dealType;//交易类型 1输入   2输入出  3拓展信息

    public String txId;//交易ID
    public String index;//交易索引
    public String amount;//金额
    public String address;//交易地址

    public int seq;//排序

//    {
//        "dealType":3,
//            "msg":"xxxxx".
//        "msgtype":1,
//            "fee":10000
//    }

    public String msg;
    public String msgtype;
    public String fee;

    //////二维码内容 end/////////////////////////////////////////


    public void setDealType(int dealType) {
        this.dealType = dealType;
    }

    //    public int sort;//扫描顺序
    public boolean expand = true;
    public String key;//私钥(解密之后的)
    public String addressTag;//地址标签

    @Override
    public int getItemType() {
        return dealType;
    }
}