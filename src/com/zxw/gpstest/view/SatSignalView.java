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
import android.location.Location;

public class SatSignalView extends View {

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
    private Paint mThinLinePaint = null;
    private Paint mBarPaintUsed = null;
    private Paint mBarPaintUnused = null;
    private Paint mBarPaintNoFix = null;
    private Paint mBarOutlinePaint = null;
    private Paint mTextPaint = null;
    private Paint mBackground = null;
	private double mlat,mlon;
    private gpstestActivity mProvider = null;
    private int mSatellites = 0;
    private int[] mPrns = new int[32];
    private float[] mSnrs = new float[32];
    private int[] mUsedInFixMask = new int[32];

    public SatSignalView(Context context) {
        this(context, null);
    }

    public SatSignalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SatSignalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Resources res = getResources();
        if (null != res) {
            mLinePaint = new Paint();
            mLinePaint.setColor(0x802196f3);
            mLinePaint.setAntiAlias(true);
            mLinePaint.setStyle(Style.STROKE);
            mLinePaint.setStrokeWidth(3.0f);

            mThinLinePaint = new Paint(mLinePaint);
            mThinLinePaint.setStrokeWidth(2.0f);

            mBarPaintUsed = new Paint();
            mBarPaintUsed.setColor(0x802196f3);
            mBarPaintUsed.setAntiAlias(true);
            mBarPaintUsed.setStyle(Style.FILL);
            mBarPaintUsed.setStrokeWidth(2.0f);

            mBarPaintUnused = new Paint(mBarPaintUsed);
            mBarPaintUnused.setColor(0x80e3f2fd);

            mBarPaintNoFix = new Paint(mBarPaintUsed);
            mBarPaintNoFix.setStyle(Style.STROKE);

            mBarOutlinePaint = new Paint();
            mBarOutlinePaint.setColor(0x803f51b5);
            mBarOutlinePaint.setAntiAlias(true);
            mBarOutlinePaint.setStyle(Style.STROKE);
            mBarOutlinePaint.setStrokeWidth(1.0f);

            mTextPaint = new Paint();
            mTextPaint.setColor(0xFFFFFFFF);
			mTextPaint.setTextSize(15.0f);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setStyle(Style.STROKE);
            mTextPaint.setStrokeWidth(1.5f);

            mBackground = new Paint();
            mBackground.setColor(0x7003a9f4);  //FF4444DD
        }
    }

    public void setDataProvider(gpstestActivity provider) {
        mProvider = provider;
    }

	public void setLocation(Location location)
	{
		mlat=location.getLatitude();
		mlon=location.getLongitude();
	}
    @Override
    protected void onDraw(Canvas canvas) {
        
        final int width = getWidth();
        final int height = getHeight();
        final float rowHeight = (float) Math.floor(height / ROW_DEVIDER);
        final float baseline = height - rowHeight + BASELINE_OFFSET;
        final float maxHeight = rowHeight * 5;
        final float scale = maxHeight / PERCENT;
		Log.v("zxw/GPSTEST", "width: "+width+" height: "+height+" baseline: "+baseline);
        if (null != mProvider) {
            mSatellites = mProvider.getSatelliteSignal(mPrns, mSnrs, null,null);
            for (int i = 0; i < mSatellites; i++) {
                if (mSnrs[i] < 0) {
                    mSnrs[i] = 0;
                }
            }
        }
        int devide = DIVIDER_MIN;
        if (mSatellites > DIVIDER_MAX) {
            devide = mSatellites;
        } else if (mSatellites > DIVIDER_3) {
            devide = DIVIDER_MAX;
        } else if (mSatellites > DIVIDER_2) {
            devide = DIVIDER_3;
        } else if (mSatellites > DIVIDER_1) {
            devide = DIVIDER_2;
        } else if (mSatellites > DIVIDER_MIN) {
            devide = DIVIDER_1;
        }
        final float slotWidth = (float) Math.floor(width / devide);
        final float barWidth = slotWidth / PERCENT * THREE_QUARTER;
        final float fill = slotWidth - barWidth;
        float margin = (width - slotWidth * devide) / 2;

		canvas.drawPaint(mBackground);
		Log.v("zxw/GPSTEST", "on draw funtion "+mSatellites);
		//canvas.drawText("good text",100, 200,  mTextPaint);
 
        canvas.drawLine(0, baseline, width, baseline, mLinePaint);
        float y = baseline - (PERCENT * scale);
        canvas.drawLine(0, y, getWidth(), y, mThinLinePaint);
        y = baseline - (TWO_QUARTER * scale);
        canvas.drawLine(0, y, getWidth(), y, mThinLinePaint);
        y = baseline - (ONE_QUARTER * scale);
        canvas.drawLine(0, y, getWidth(), y, mThinLinePaint);
        y = baseline - (THREE_QUARTER * scale);
        canvas.drawLine(0, y, getWidth(), y, mThinLinePaint);
        int drawn = 0;
        for (int i = 0; i < mSatellites; i++) {
            if (0 >= mPrns[i]) {
                continue;
            }
            final float left = margin + (drawn * slotWidth) + fill / 2;
            final float top = baseline - (mSnrs[i] * scale);
            final float right = left + barWidth;
            final float center = left + barWidth / 2;
			//String locval = String.format("%.5f", mlat);
			//canvas.drawText(locval, 80, 10, mTextPaint);
			//locval = String.format("%.5f", mlon);
			//canvas.drawText(locval, 200, 10, mTextPaint);
            canvas.drawRect(left, top, right, baseline, mBarPaintUsed);
            canvas.drawRect(left, top, right, baseline, mBarOutlinePaint);
            String tmp = String.format("%2.0f", mSnrs[i]);
            canvas.drawText(tmp, center, top - fill, mTextPaint);
            canvas.drawText(Integer.toString(mPrns[i]), center, baseline + TEXT_OFFSET + fill, mTextPaint);
            drawn += 1;
        }
		super.onDraw(canvas);
    }

}
