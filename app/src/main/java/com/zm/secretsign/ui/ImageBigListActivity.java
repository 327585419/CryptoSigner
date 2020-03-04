package com.zm.secretsign.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.zhou.library.utils.ScreenUtil;
import com.zm.secretsign.utils.BarcodeUtil;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseTitleActivity;
import com.zm.secretsign.view.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ImageBigListActivity extends BaseTitleActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    private String code;
    private int index;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        bindContentView(R.layout.activity_image_list);

        setTitle("二维码");
        if (savedInstanceState == null) {
            code = getIntent().getStringExtra("p0");
            index = getIntent().getIntExtra("p1", 0);
        } else {
            code = savedInstanceState.getString("p0");
            index = savedInstanceState.getInt("p1");
        }

        List<View> views = new ArrayList<>();
        //获取View对象
        LayoutInflater inflater = LayoutInflater.from(this);
        List<String> codes = JSON.parseArray(code, String.class);
        for (int i = 0; i < codes.size(); i++) {
            View view = inflater.inflate(R.layout.activity_image_big, null);
            ImageView ivCode = view.findViewById(R.id.iv_code);
            ivCode.setImageBitmap(BarcodeUtil.createQrcodeBitmap(codes.get(i), ScreenUtil.getScreenWidth()));
            views.add(view);
        }
        tvNumber.setText((index + 1) + "/" + codes.size());
        viewPager.setCurrentItem(index);

        ViewPagerAdapter adapter = new ViewPagerAdapter(views);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                tvNumber.setText((i + 1) + "/" + codes.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        hideProgress();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("p0", code);
        outState.putInt("p1", index);
        super.onSaveInstanceState(outState);
    }

}
