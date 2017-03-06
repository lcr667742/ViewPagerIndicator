package com.lee.kevin.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Lee
 * on 2017/3/2.
 */

public class CirclePagerIndicator extends View implements PageIndicator {

    private float mRadius;
    private float mIndicatorRadius;
    private float mIndicatorSpace;

    private final Paint mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;

    private int mCurrentPage;
    private int mFollowPage;
    private float mPageOffset;
    private boolean mCenterHorizontal;
    private boolean mIsFollowing;


    public CirclePagerIndicator(Context context) {
        super(context);
    }

    public CirclePagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CirclePagerIndicator);
        mRadius = a.getDimension(R.styleable.CirclePagerIndicator_circle_radius, 10);
        mIndicatorRadius = a.getDimension(R.styleable.CirclePagerIndicator_circle_current_radius, 10);
        mIndicatorSpace = a.getDimension(R.styleable.CirclePagerIndicator_circle_space, 20);

        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(a.getColor(R.styleable.CirclePagerIndicator_circle_color, 0x0000ff));
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setColor(a.getColor(R.styleable.CirclePagerIndicator_circle_stroke_color, 0x000000));
        mPaintStroke.setStrokeWidth(a.getDimension(R.styleable.CirclePagerIndicator_circle_stroke_width, 1));
        mPaintIndicator.setStyle(Paint.Style.FILL);
        mPaintIndicator.setColor(a.getColor(R.styleable.CirclePagerIndicator_circle_fill_color, 0x0000ff));

        mCenterHorizontal = a.getBoolean(R.styleable.CirclePagerIndicator_circle_centerHorizontal, true);
        mIsFollowing = a.getBoolean(R.styleable.CirclePagerIndicator_circle_following, true);

        if (mIndicatorRadius < mRadius)
            mIndicatorRadius = mRadius;
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewPager == null) return;

        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) return;

        if (mCurrentPage >= count) {
            setCurrentItem(count);
            return;
        }


        int width = getWidth();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();

        final float circleAndSpace = 2 * mRadius + mIndicatorSpace;//圆的直径+两圆之间的间隔
        final float yOffset = paddingTop + mRadius;//竖直方向圆心偏移量
        float xOffset = paddingLeft + mRadius;//水平方向圆心偏移量

        //采用水平居中对齐
        if (mCenterHorizontal) {
            xOffset += ((width - paddingLeft - paddingRight) - (count * circleAndSpace - mIndicatorSpace)) / 2.0f;
        }

        float cX;
        float cY;

        float strokeRadius = mRadius;

        //如果绘制外圆
        if (mPaintStroke.getStrokeWidth() > 0) {

            strokeRadius -= mPaintStroke.getStrokeWidth() / 2.0f;
        }

        //绘制所有圆点
        for (int i = 0; i < count; i++) {
            cX = xOffset + (i * circleAndSpace);//计算下个圆绘制起点偏移量
            cY = yOffset;

            //绘制圆
            if (mPaintFill.getAlpha() > 0) {
                canvas.drawCircle(cX, cY, strokeRadius, mPaintFill);
            }

            //绘制外圆
            if (strokeRadius != mRadius) {
                canvas.drawCircle(cX, cY, mRadius, mPaintStroke);
            }
        }

        float cx = (!mIsFollowing ? mFollowPage : mCurrentPage) * circleAndSpace;

        //指示器选择缓慢移动
        if (mIsFollowing) {
            cx += mPageOffset * circleAndSpace;
        }

        cX = xOffset + cx;
        cY = yOffset;
        canvas.drawCircle(cX, cY, mIndicatorRadius, mPaintIndicator);

    }

    @Override
    public void bindViewPager(ViewPager viewPager) {

        if (viewPager == null) {
            throw new IllegalStateException("ViewPager must not be null");
        }

        if (viewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager must have a adapter");
        }

        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
        invalidate();

    }

    @Override
    public void setCurrentItem(int item) {

        if (mViewPager == null) {
            throw new IllegalStateException("indicator has not bind ViewPager");
        }

        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        invalidate();
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void notifyDataSetChanged() {
        invalidate();

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentPage = position;
        mPageOffset = positionOffset;

        if (mIsFollowing) {
            invalidate();
        }
        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        mFollowPage = position;
        invalidate();
        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int width;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || mViewPager == null) {

            width = specSize;
        } else {
            final int count = mViewPager.getAdapter().getCount();
            width = (int) (getPaddingLeft() + getPaddingRight() + (count * 2 * mRadius) + (count - 1) * mIndicatorSpace);

            if (specMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, specSize);
            }
        }
        return width;
    }

    private int measureHeight(int measureSpec) {
        int height;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            height = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            if (specMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, specSize);
            }
        }

        return height;
    }
}
