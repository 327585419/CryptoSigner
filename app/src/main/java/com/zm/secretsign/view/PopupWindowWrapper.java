package com.zm.secretsign.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhou.library.utils.LogUtil;
import com.zm.secretsign.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 弹出层
 *
 * @author golden(huaguoting @ gmail.com)
 * @since 2018-04-08 16:28
 */
public class PopupWindowWrapper extends PopupWindow {

    @BindView(R.id.recycler)
    RecyclerView recycler;

    private String defaultType;

    public PopupWindowWrapper(Context context, List<String> data, String defaultType, final View.OnClickListener listener) {
        super(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_pop, null);
        setContentView(contentView);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
//        setFocusable(false);
//        setClippingEnabled(false);
//        setIgnoreCheekPress();
//        setWidth(width);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        ButterKnife.bind(this, contentView);

        RecyclerAdapter adapter = new RecyclerAdapter();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                setDefaultType((String) adapter.getItem(position));
//                LogUtil.e("点击pop:" + getDefaultType());
                adapter.notifyDataSetChanged();
                dismiss();
                listener.onClick(view);
            }
        });

        setDefaultType(defaultType);
        recycler.setLayoutManager(new LinearLayoutManager(context));
        recycler.setAdapter(adapter);
        adapter.setNewData(data);


    }

    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(String defaultType) {
        if (!TextUtils.isEmpty(this.defaultType) && this.defaultType.equals(defaultType)) {
            return;
        }
        this.defaultType = defaultType;
    }


    public class RecyclerAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public RecyclerAdapter() {
            super(R.layout.item_filter);
        }

        @Override
        protected void convert(BaseViewHolder holder, String item) {
            holder.setText(R.id.tv_name, item.toUpperCase())
                    .setTextColor(R.id.tv_name, ContextCompat.getColor(mContext, defaultType.equals(item) ?
                            R.color.colorPrimary : R.color.colorDark));
        }
    }

}
