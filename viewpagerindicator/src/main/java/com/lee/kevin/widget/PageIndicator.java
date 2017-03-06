package com.lee.kevin.widget;

import android.support.v4.view.ViewPager;


/**
 * Created by Lee on 2017/3/2.
 */

public interface PageIndicator extends ViewPager.OnPageChangeListener {

    void bindViewPager(ViewPager viewPager);

    void setCurrentItem(int item);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

    void notifyDataSetChanged();
}
