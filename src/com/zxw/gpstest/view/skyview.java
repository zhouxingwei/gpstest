package com.zxw.gpstest.view;

import com.zxw.gpstest.activity.gpstestActivity;
import com.zxw.gpstest.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color ;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;

public class skyview extends View {

    private static final int TEXT_OFFSET = 8;
    private static final int BASELINE_OFFSET = 5;
    private static final double ROW_DEVIDER = 5.0;
    private static final float PERCENT = 100.0F;
    private static final int ONE_QUARTER = 25;
    private static final int TWO_QUARTER = 50;
    private static final int THREE_QUARTER = 75;
    private static final float TEXT_SIZE = 10.0f;
    private static final float THIN_LINE_STOKE_WIDTH = 0.5f;
    private static final int DIVIDER_MIN = 15;
    private static final int DIVIDER_MAX = 32;
    private static final int DIVIDER_1 = 20;
    private static final int DIVIDER_2 = 25;
    private static final int DIVIDER_3 = 30;
    private Paint mLinePaint = null;
    private Paint mTextPaint = null;
    private Paint mBackground = null;

    private gpstestActivity mProvider = null;
    private int mSatellites = 0;
    private int[] mPrns = new int[32];
    private float[] mSnrs = new float[32];
    private float[] mElevation =  new float[32];
    private float[] mAzimuth = new float[32];
    private int[] mUsedInFixMask = new int[32];
    private float[] mX = new float[32];
    private float[] mY = new float[32];

    public skyview(Context context) {
        this(context, null);
    }

    public skyview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public skyview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Resources res = getResources();
        if (null != res) {
            mLinePaint = new Paint();
            mLinePaint.setColor(0x802196f3);
            mLinePaint.setAntiAlias(true);
            mLinePaint.setStyle(Style.STROKE);
            mLinePaint.setStrokeWidth(3.0f);
           
            mTextPaint = new Paint();
            mTextPaint.setColor(0xFFFFFFFF);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setStyle(Style.STROKE);
            mTextPaint.setTextSize(20.0f);
            mTextPaint.setStrokeWidth(2.0f);

            mBackground = new Paint();
            mBackground.setColor(0xFF4444DD);   //0x904444DD
        }
    }

    public void setSkyDataProvider(gpstestActivity provider) {
        mProvider = provider;
    }

    private void computeXY() {
        for (int i = 0; i < mSatellites; ++i) {
            double theta = -(mAzimuth[i] - 90);
            double rad = theta * Math.PI / 180;
            mX[i] = (float) Math.cos(rad);
            mY[i] = -(float) Math.sin(rad);

            mElevation[i] = 90 - mElevation[i];
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {  
        float centerY = getHeight() / 2;
        float centerX = getWidth() / 2;
        int radius;
		double scale,as; 
        if (centerX > centerY) {
            radius = (int) (getHeight()/2) - 12;
        } else {
            radius = (int) (getWidth()/2) - 12;
        }
		scale = radius / 90.0;

        canvas.drawPaint(mBackground);
        canvas.drawCircle(centerX, centerY, radius, mLinePaint);
        canvas.drawCircle(centerX, centerY, radius * 0.75f, mLinePaint);
        canvas.drawCircle(centerX, centerY, radius >> 1, mLinePaint);
        canvas.drawCircle(centerX, centerY, radius >> 2, mLinePaint);
        canvas.drawLine(centerX, centerY - (radius >> 2), centerX, centerY
                - radius, mLinePaint);
        canvas.drawLine(centerX, centerY + (radius >> 2), centerX, centerY
                + radius, mLinePaint);
        canvas.drawLine(centerX - (radius >> 2), centerY, centerX - radius,
                centerY, mLinePaint);
        canvas.drawLine(centerX + (radius >> 2), centerY, centerX + radius,
                centerY, mLinePaint);
        
        if (mProvider != null) {
            mSatellites = mProvider.getSatelliteSignal(mPrns, mSnrs, mElevation, mAzimuth);
            computeXY();
        }
        for (int i = 0; i < mSatellites; ++i) {
            if (mElevation[i] >= 90 || mAzimuth[i] < 0 || mPrns[i] <= 0) {
                continue;
            }
            as = mElevation[i] * scale;
            int x = (int) Math.round(centerX + mX[i] * as);
            int y = (int) Math.round(centerY + mY[i] * as);

            canvas.drawText(Integer.toString(mPrns[i]), x, y, mTextPaint);
    	}
        super.onDraw(canvas);
	}
}
