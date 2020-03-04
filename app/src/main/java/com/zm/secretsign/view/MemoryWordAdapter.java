package com.zm.secretsign.view;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zm.secretsign.R;

public class MemoryWordAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public MemoryWordAdapter() {
        super(R.layout.item_word);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_name, item)
                .setGone(R.id.iv_delete, !TextUtils.isEmpty(item))
                .addOnClickListener(R.id.iv_delete);
    }
}