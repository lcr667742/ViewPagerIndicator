package com.lee.kevin.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Lee on 2017/3/6.
 */

public class LinePagerIndicator extends View implements PageIndicator {

    private final Paint mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;

    private int mCurrentPage;
    private int mFollowPage;
    private float mPageOffset;

    private boolean mCenterHorizontal;
    private boolean mIsFollowing;
    private float mLineWidth;
    private float mSpace;


    public LinePagerIndicator(Context context) {
        super(context);
    }

    public LinePagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LinePagerIndicator);
        mCenterHorizontal = a.getBoolean(R.styleable.LinePagerIndicator_line_centerHorizontal, true);
        mLineWidth = a.getDimension(R.styleable.LinePagerIndicator_line_width, 10);
        mSpace = a.getDimension(R.styleable.LinePagerIndicator_line_space, 8);
        float mLineHeight = a.getDimension(R.styleable.LinePagerIndicator_line_height, 2);
        float mIndicatorHeight = a.getDimension(R.styleable.LinePagerIndicator_line_current_height, 2);
        mIsFollowing = a.getBoolean(R.styleable.LinePagerIndicator_line_following, true);
        mPaintFill.setColor(a.getColor(R.styleable.LinePagerIndicator_line_fill_color, 0x0000ff));
        mPaintIndicator.setColor(a.getColor(R.styleable.LinePagerIndicator_line_current_color, 0x00ffff));
        a.recycle();

        mPaintIndicator.setStrokeWidth(mIndicatorHeight > mLineHeight ? mIndicatorHeight : mLineHeight);
        mPaintFill.setStrokeWidth(mLineHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewPager == null) {
            return;
        }

        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            return;
        }

        if (mCurrentPage >= count) {
            setCurrentItem(count - 1);
        }

        final float lineWidthAndSpace = mLineWidth + mSpace;
        final float indicatorWidth = (count * lineWidthAndSpace) - mSpace;//指示器宽度

        final float paddingTop = getPaddingTop();
        final float paddingLeft = getPaddingLeft();
        final float paddingRight = getPaddingRight();

        float yOffset = paddingTop + ((getHeight() - paddingTop - getPaddingBottom()) / 2.0f);//绘制线的中心竖直方向偏移量
        float xOffset = paddingLeft;////绘制线的中心水平方向偏移量

        //如果采用水平居中对齐的水平偏移量
        if (mCenterHorizontal) {
            xOffset += (getWidth() - paddingLeft - paddingRight - indicatorWidth) / 2.0f;
        }

        float startX;
        float stopX;

        for (int i = 0; i < count; i++) {
            startX = xOffset + (i * lineWidthAndSpace);//计算下条直线绘制起点偏移量
            stopX = startX + mLineWidth;
            canvas.drawLine(startX, yOffset, stopX, yOffset, mPaintFill);
        }

        float currentSpace = (!mIsFollowing ? mFollowPage : mCurrentPage) * lineWidthAndSpace;

        if (mIsFollowing) {
            currentSpace += mPageOffset * lineWidthAndSpace;
        }

        startX = xOffset + currentSpace;
        stopX = startX + mLineWidth;
        canvas.drawLine(startX, yOffset, stopX, yOffset, mPaintIndicator);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        float width;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
            width = specSize;
        } else {
            final int count = mViewPager.getAdapter().getCount();
            width = getPaddingLeft() + getPaddingRight() + (count * mLineWidth) + ((count - 1) * mSpace);
            if (specMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, specSize);
            }
        }
        return (int) Math.ceil(width);
    }

    private int measureHeight(int measureSpec) {
        float height;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
            height = specSize;
        } else {
            height = mPaintIndicator.getStrokeWidth() + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, specSize);
            }
        }
        return (int) Math.ceil(height);
    }

    @Override
    public void bindViewPager(ViewPager viewPager) {
        if (null == viewPager) {
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
}
