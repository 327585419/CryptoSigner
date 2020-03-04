package com.zm.secretsign.view;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zm.secretsign.R;
import com.zm.secretsign.bean.DealSignItem;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<DealSignItem, BaseViewHolder> {

    private int type;//1简单交易  2高级交易


    public MultipleItemQuickAdapter(Context context, int type) {
        this(context, null);
        this.type = type;
    }

    public MultipleItemQuickAdapter(Context context, List<DealSignItem> data) {
        super(data);
        addItemType(DealSignItem.INPUT, R.layout.item_sign_input);
        addItemType(DealSignItem.OUTPUT, R.layout.item_sign_output);
        addItemType(DealSignItem.MSG, R.layout.item_sign_output);
    }

    public int getItemTypeCount(int type) {
        int count = 0;
        for (int i = 0; i < getItemCount(); i++) {
            if (type == getItemViewType(i)) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected void convert(BaseViewHolder helper, DealSignItem item) {
        helper.setText(R.id.tv_type, item.getItemType() == DealSignItem.INPUT ? "交易输入" : "交易输出")
                .setImageResource(R.id.iv_arrow, item.expand ? R.drawable.ic_arrow_up_gray : R.drawable.xiafan)
                .addOnClickListener(R.id.ll_title, R.id.iv_delete)
                .setGone(R.id.ll_info_output, item.expand)
                .setText(R.id.tv_sort, "#" + item.seq);

//        DecimalFormat nf = new DecimalFormat(item.amount);
//        nf.setGroupingUsed(false);

        String amount = item.amount;
        switch (helper.getItemViewType()) {
            case DealSignItem.INPUT:
                helper
//                        .setText(R.id.tv_sort, "#" + (helper.getAdapterPosition() + 1))
                        .setText(R.id.tv0, "交易ID：" + item.txId)
                        .setText(R.id.tv1, "交易输出索引：" + item.index)
                        .setText(R.id.tv2, "交易输出金额：" + amount)
                        .setText(R.id.tv3, "交易地址：" + item.address)
                        .setText(R.id.tv4, "地址标签：" + item.addressTag)
                        .setGone(R.id.ll_info_input, item.expand);
                break;
            case DealSignItem.OUTPUT:
                helper
//                        .setText(R.id.tv_sort, "#" + (helper.getAdapterPosition() - getItemTypeCount(DealSignItem.INPUT) + 1))
                        .setText(R.id.tv0, "交易输出地址：" + item.address)
                        .setText(R.id.tv1, "金额：" + amount)
                        .addOnClickListener(R.id.tv1);
                break;
            default:
                break;
        }
    }

}