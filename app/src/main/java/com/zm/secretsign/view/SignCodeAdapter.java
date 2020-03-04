package com.zm.secretsign.view;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhou.library.utils.ScreenUtil;
import com.zm.secretsign.utils.BarcodeUtil;
import com.zm.secretsign.R;

public class SignCodeAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public SignCodeAdapter() {
        super(R.layout.item_code);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

        helper.addOnClickListener(R.id.iv_code)
                .setText(R.id.tv_sort, String.valueOf(helper.getLayoutPosition() + 1))
                .setText(R.id.tv_count, "/" + getItemCount())
                .setImageBitmap(R.id.iv_code, BarcodeUtil.createQrcodeBitmap(item, ScreenUtil.getScreenWidth() / 4));
    }
}