package com.lee.kevin.indicatorproject;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lee.kevin.widget.CirclePagerIndicator;
import com.lee.kevin.widget.LinePagerIndicator;

public class MainActivity extends AppCompatActivity {

    private CirclePagerIndicator mCirclePagerIndicator;
    private LinePagerIndicator mLinePagerIndicator;
    private ViewPager mViewPager;

    private int[] images = {R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a1, R.drawable.a2, R.drawable.a3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mCirclePagerIndicator = (CirclePagerIndicator) findViewById(R.id.circleIndicator);
        mLinePagerIndicator = (LinePagerIndicator) findViewById(R.id.lineIndicator);
        mViewPager.setAdapter(new ViewpagerAdapter());
        mCirclePagerIndicator.bindViewPager(mViewPager);
        mLinePagerIndicator.bindViewPager(mViewPager);
    }

    class ViewpagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setImageResource(images[position]);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == object;
        }
    }
}
