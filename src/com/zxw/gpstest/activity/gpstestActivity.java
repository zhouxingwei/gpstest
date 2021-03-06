package com.zxw.gpstest.activity;
import com.zxw.gpstest.R;
import com.zxw.gpstest.view.SatSignalView;
import com.zxw.gpstest.view.skyview;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.NmeaListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;    //for sensor
import android.content.Context;
import android.widget.EditText;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.os.Bundle;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.ImageView;
import android.os.Message;
import java.lang.Math;
import java.lang.Integer;
import java.lang.Float;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;
import android.content.SharedPreferences;
import android.widget.LinearLayout;
import android.app.ActionBar;
import android.view.Window;
import android.view.WindowManager;
import android.support.v4.view.PagerAdapter;   
import android.support.v4.view.ViewPager;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import android.view.LayoutInflater;
import android.widget.Toast;

public class gpstestActivity extends Activity{
	private static final String TAG = "zxw/GPSTEST";
	private static final int MAX_SATELLITES_NUMBER = 32;
	private View view1, view2, view3;  
    private List<View> viewList;// view数组  
    private ViewPager viewPager; // 对应的viewPager  
  	private Button gpsopen;
	private TextView workStatus;
	private LocationManager mlocationmanage;
    private List<String> titleList;  
	private int gpsStatus;
	private SatSignalView mSignalView;
	private skyview mSkyView;
	private int mSatellites;
    private int[] mPrns = new int[MAX_SATELLITES_NUMBER];
    private float[] mSnrs = new float[MAX_SATELLITES_NUMBER];
    private float[] mElevation = new float[MAX_SATELLITES_NUMBER];
    private float[] mAzimuth = new float[MAX_SATELLITES_NUMBER];


    public final LocationListener mLocListener = new LocationListener() {
        // @Override
        public void onLocationChanged(Location location) {
			Log.v(TAG, "location is geted");
			String lat,lon;
			lat = String.format("%.4f", location.getLatitude());
			lon = String.format("%.4f", location.getLongitude());
			workStatus.setTextSize(15);	
			workStatus.setText(lat+','+lon);
			//mSignalView.setLocation(location);

		}
        public void onProviderDisabled(String provider) {
            Log.v(TAG, "Enter onProviderDisabled function");

        }
        // @Override
        public void onProviderEnabled(String provider) {
            Log.v(TAG, "Enter onProviderEnabled function");

        }

        // @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.v(TAG, "Enter onStatusChanged function");
        }
	};
    public final GpsStatus.Listener mGpsListener = new GpsStatus.Listener() {

        private void onFirstFix(int ttff) {
			Log.d(TAG, "firstfix: "+ttff);
		}
		@Override
		public void onGpsStatusChanged(int event) {
			
           Log.v(TAG, "Enter onGpsStatusChanged function");
            GpsStatus status = mlocationmanage.getGpsStatus(null);
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d(TAG, "Time stamp[GPS_EVENT_STARTED]->" + new Date().getTime());
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d(TAG, "GPS_EVENT_STOPPED");
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
					//workStatus.setTextSize(20);		
					//workStatus.setText("fixed");
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.d(TAG, "GPS_EVENT_SATELLITE_STATUS is receive!!");
					setSatData(status.getSatellites());
                    break;
                default:
                    break;
            }
		}
	};

    public void updateSignalView() {
        Log.v(TAG, "Enter setSatelliteStatus function");
        //mSatelliteView.postInvalidate();
        mSignalView.postInvalidate();
    }
    public void updateskyView()
	{
        Log.v(TAG, "Enter updateskyview function");
        mSkyView.postInvalidate();
	}
    public void setSatData(Iterable<GpsSatellite> list) {
        Log.v(TAG, "Enter setSatelliteStatus function");
        if (null != list) {
            synchronized (this) {
				int index = 0;
				for (int i = 0; i < MAX_SATELLITES_NUMBER; i++) {
					mPrns[i] = 0;
					mSnrs[i] = 0;
					mElevation[i] = 0;
					mAzimuth[i] = 0;
				}
                
                for (GpsSatellite sate : list) {
                    mPrns[index] = sate.getPrn();
                    mSnrs[index] = sate.getSnr();
                    mElevation[index] = sate.getElevation();
                    mAzimuth[index] = sate.getAzimuth();
                    index++;
                }
                mSatellites = index;
            }
        }
        Log.v(TAG,"sv num: " + mSatellites);

		updateskyView();
		updateSignalView();
    }

    public int getSatelliteSignal(int[] prns, float[] snrs, float[] elevations,float[] azimuths) {
        synchronized (this) {
            if (prns != null) {
                System.arraycopy(mPrns, 0, prns, 0, mSatellites);
            }
            if (snrs != null) {
                System.arraycopy(mSnrs, 0, snrs, 0, mSatellites);
            }
            if (azimuths != null) {
                System.arraycopy(mAzimuth, 0, azimuths, 0, mSatellites);
            }
            if (elevations != null) {
                System.arraycopy(mElevation, 0, elevations, 0, mSatellites);
            }

            return mSatellites;
        }
    }

	private void gps_initConfig()
	{
		mlocationmanage.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener);
		mlocationmanage.addGpsStatusListener(mGpsListener);
	}
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        setContentView(R.layout.main);  
  		mlocationmanage = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        viewPager = (ViewPager) findViewById(R.id.pager);  
        LayoutInflater inflater = getLayoutInflater();  
		 
        view1 = inflater.inflate(R.layout.test1, null);  
        view2 = inflater.inflate(R.layout.test2, null);  
  		gpsopen = (Button)view1.findViewById(R.id.switch_gps);
		workStatus = (TextView)view1.findViewById(R.id.accu);
		mSignalView = (SatSignalView)view1.findViewById(R.id.signal);
		mSkyView = (skyview)view1.findViewById(R.id.sky);
		mSignalView.setDataProvider(this); 
		mSkyView.setSkyDataProvider(this);     //set data provider,this should implement by interface in future
		gpsStatus = 0;
		gpsopen.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view)
			{

				//Toast.makeText(gpstestActivity.this,"open gps",Toast.LENGTH_LONG).show();
        		if(mlocationmanage.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
					if(gpsStatus == 1)
					{
						//Toast.makeText(gpstestActivity.this, "GPS开关已打开", Toast.LENGTH_SHORT).show();
						onStop();	
						return;
					}
					gpsStatus = 1;
					workStatus.setText("working");
					gpsopen.setText("GPS-ON");
				    Toast.makeText(gpstestActivity.this, "GPS模块正常", Toast.LENGTH_SHORT).show();	
					gps_initConfig();
					
				}
				else{
					Toast.makeText(gpstestActivity.this, "请开启gps开关", Toast.LENGTH_SHORT).show();	
					Intent settinglocation = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(settinglocation,2);
					gpsStatus = 0;
					workStatus.setText("working");
				}
			}
			});
        viewList = new ArrayList<View>();      // 将要分页显示的View装入数组中  
        viewList.add(view1);  
        viewList.add(view2);  
  
        titleList = new ArrayList<String>();   // 每个页面的Title数据  
        titleList.add("GPS-VIEW");  
        titleList.add("CONFIG");  

  
        PagerAdapter pagerAdapter = new PagerAdapter() {  
  
            @Override  
            public boolean isViewFromObject(View arg0, Object arg1) {  
                // TODO Auto-generated method stub  
                return arg0 == arg1;  
            }  
  
            @Override  
            public int getCount() {  
                // TODO Auto-generated method stub  
                return viewList.size();  
            }  
  
            @Override  
            public void destroyItem(ViewGroup container, int position,  
                    Object object) {  
                // TODO Auto-generated method stub  
                container.removeView(viewList.get(position));  
            }  
  
            @Override  
            public Object instantiateItem(ViewGroup container, int position) {  
                // TODO Auto-generated method stub  
                container.addView(viewList.get(position));  
  
                return viewList.get(position);  
            }  
  
            @Override  
            public CharSequence getPageTitle(int position) {  
                  
                return titleList.get(position);  
            }  
        };  
  
        viewPager.setAdapter(pagerAdapter);  
  
    }  
      @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Enter onStop function");
		if(gpsStatus == 1)
		{
		    mlocationmanage.removeUpdates(mLocListener);
		    Log.v(TAG, "removeUpdates(mLocListener)");
		    mlocationmanage.removeGpsStatusListener(mGpsListener);
			gpsStatus = 0;
			workStatus.setText("not work");
			gpsopen.setText("GPS-OFF");
		}

    }
}
