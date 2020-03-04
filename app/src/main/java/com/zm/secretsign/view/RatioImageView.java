package com.zm.secretsign.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.zm.secretsign.R;


public class RatioImageView extends AppCompatImageView {

    private float mWidthRatio = -1; // 宽度 = 高度*mWidthRatio
    private float mHeightRatio = -1; // 高度 = 宽度*mHeightRatio

    public RatioImageView(Context context) {
        super(context);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RatioView);
            mHeightRatio = typedArray.getFloat(R.styleable.RatioView_height_to_width_ratio, mHeightRatio);
            mWidthRatio = typedArray.getFloat(R.styleable.RatioView_width_to_height_ratio, mWidthRatio);
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHeightRatio > 0 && mWidthRatio > 0) {
            throw new RuntimeException("高度和宽度不能同时设置百分比！！");
        }
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (mWidthRatio > 0) { // 高度已知，根据比例，设置宽度
            width = (int) (height * mWidthRatio);
        } else if (mHeightRatio > 0) { // 宽度已知，根据比例，设置高度
            height = (int) (width * mHeightRatio);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
    }

    public void setHeightRatio(float mHeightRatio) {
        this.mHeightRatio = mHeightRatio;
    }

    public float getHeightRatio() {
        return mHeightRatio;
    }

    public void setWidthRatio(float mWidthRatio) {
        this.mWidthRatio = mWidthRatio;
    }

    public float getWidthRatio() {
        return mWidthRatio;
    }
}
