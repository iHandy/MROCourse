package com.ucoz.handyby.mrocourse.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.ucoz.handyby.mrocourse.R;
import com.ucoz.handyby.mrocourse.processors.GystMember;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class RowsGystogramView extends View {
    //private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 1; // TODO: use a default from R.dimen...
    //private Drawable mExampleDrawable;

    private float mGystWidth = R.dimen.gyst_width;

    private Paint mPaint;
    /*private float mTextWidth;
    private float mTextHeight;*/

    private ArrayList<GystMember> mGystogram = null;

    public RowsGystogramView(Context context) {
        super(context);
        init(null, 0);
    }

    public RowsGystogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RowsGystogramView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        /*final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RowsGystogramView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.RowsGystogramView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.RowsGystogramView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.RowsGystogramView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.RowsGystogramView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.RowsGystogramView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();
        */

        // Set up a default TextPaint object
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        //mPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mPaint.setStrokeWidth(1);
        mPaint.setColor(mExampleColor);
        //mTextWidth = mPaint.measureText(mExampleString);

        //Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        //mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        /*canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }*/


        if (mGystogram != null) {
            float max = Integer.MIN_VALUE;
            for (GystMember gystMember : mGystogram) {
                if (gystMember.count > max) {
                    max = gystMember.count;
                }
            }
            int pixelSize = getWidth();//getResources().getDimensionPixelSize(R.dimen.gyst_width);
            int coef = (int) (max / pixelSize);
            if (coef == 0)
            {
                coef = 1;
            }
            int y = 0;
            for (GystMember gystMember : mGystogram) {
                int value = gystMember.count;
                canvas.drawLine(0, y, value / coef, y, mPaint);
                y++;
            }
        }
    }


    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    public void setGystogram(ArrayList<GystMember> gystogram) {
        this.mGystogram = gystogram;
        invalidate();
    }
}
