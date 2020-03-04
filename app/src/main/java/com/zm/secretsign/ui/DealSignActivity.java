package com.zm.secretsign.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.zhou.library.bean.Event;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseFragment;
import com.zm.secretsign.ui.base.BaseTitleActivity;
import com.zm.secretsign.ui.fragment.DealSignFragment;
import com.zm.secretsign.utils.WebJsUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class DealSignActivity extends BaseTitleActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;

    private Integer[] titles = {
            R.string.simple_deal,
            R.string.high_deal
    };
    private List<BaseFragment> fragments;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        bindContentView(R.layout.activity_import_key);

        setUpTextLeftBackIcon(R.string.deal_sign);
        setDownTextRightIcon();

        fragments = new ArrayList<>();
        fragments.add(DealSignFragment.newInstance(1));
        fragments.add(DealSignFragment.newInstance(2));

        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                super.destroyItem(container, position, object);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return getString(titles[position]);
            }
        });


        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles == null ? 0 : titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.colorGray));
                colorTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                colorTransitionPagerTitleView.setText(titles[index]);
                colorTransitionPagerTitleView.setTextSize(16);
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                //设置indicator的宽度
                indicator.setLineWidth(getResources().getDimensionPixelSize(R.dimen.dp_24));
                indicator.setLineHeight(getResources().getDimensionPixelSize(R.dimen.dp_4));
                indicator.setColors(ContextCompat.getColor(mContext, R.color.colorPrimary));
                indicator.setRoundRadius(10);
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        switch (event.name) {
            case Constant.SET_PWD:
                goLogin();
                break;
            case WebJsUtil.PAGE_FINISHED:
                hideProgress();
                break;
        }
    }

    @Override
    protected void onPopItemClick(String popPosition) {
        super.onPopItemClick(popPosition);
        fragments.get(viewPager.getCurrentItem()).clearView();
    }
}
