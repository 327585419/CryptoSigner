package com.zm.secretsign.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private List<View> views;

    public ViewPagerAdapter(List<View> views) {
        this.views = views;
    }

    //重新4个方法
//getCount()返回List<View>的size:
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return views.size();
    }

    //instantiateItem()：将当前view添加到ViewGroup中，并返回当前View
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    //destroyItem()：删除当前的View;
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    //isViewFromObject判断当前的View 和 我们想要的Object(值为View) 是否一样;返回 trun / false;
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return (arg0 == arg1);
    }

}