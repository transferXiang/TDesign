package com.baidumap.tdesign;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidumap.tdesign.MyOrientationListener.OnOrientationListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	
	private LocationClient mLocaltionClient;
	private MyLocationListenr mLocationListener;
	private boolean isFirstIn = true;
	
	private double mLatitude;
	private double mLongtitude;
	
	private BitmapDescriptor mIconLocation;
	private MyOrientationListener mMyOrientationListener;
	private float mCurrentX;
	
	private Context mContex;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext()); 
        
        mContex = this;
        
        setContentView(R.layout.activity_main);
        
        initView();
        
        initLocation();  
    }

	private void initLocation() {
		mLocaltionClient = new LocationClient(this);
        mLocationListener = new MyLocationListenr();
        mLocaltionClient.registerLocationListener(mLocationListener);
        
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000); //1s��ɨ��һ��
        mLocaltionClient.setLocOption(option);
        
        // �Զ���ͼ��
        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.gps_arrow);
        
        // ���listener���������귢���仯
        mMyOrientationListener = new MyOrientationListener(mContex);
        mMyOrientationListener.setOnOrientationListener(new OnOrientationListener() {
			
			@Override
			public void onOrientationChanged(float x) {
				mCurrentX = x;
			}
		});
	}

	private void initView() {
		mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mMapView.onDestroy();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mMapView.onPause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mMapView.onResume();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	// ������λ
    	mBaiduMap.setMyLocationEnabled(true);
    	if(!mLocaltionClient.isStarted()){
    		mLocaltionClient.start();
    	}
    	
    	// �������򴫸���
    	mMyOrientationListener.Start();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	
    	// ֹͣ��λ
    	if(mLocaltionClient.isStarted()){
    		mLocaltionClient.stop();
    	}
    	mBaiduMap.setMyLocationEnabled(false);
    	
    	// �رշ��򴫸���
    	mMyOrientationListener.Stop();
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.id_map_common:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;

		case R.id.id_map_satellite:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
			
		case R.id.id_map_traffic:
			boolean enable = mBaiduMap.isTrafficEnabled();
			mBaiduMap.setTrafficEnabled(!enable);
			item.setTitle(enable ? R.string.map_traffic_off : R.string.map_traffic_on);
			break;
			
		case R.id.id_map_location:
			toMyLocation();
			break;
			
		default:
			Log.d("MainActivity.onOptionsItemSelected", "unknown item type");
			break;
		}
    	return super.onOptionsItemSelected(item);
    }

    /**
     * �ƶ����ҵ�λ��
     */
	private void toMyLocation() {
		LatLng latLng = new LatLng(mLatitude, mLongtitude);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
	}
    
    
    private class MyLocationListenr implements BDLocationListener
    {

		@Override
		public void onReceiveLocation(BDLocation location) {
			MyLocationData data = new MyLocationData.Builder()//
			.direction(mCurrentX)//
			.accuracy(location.getRadius())//
			.latitude(location.getLatitude())//
			.longitude(location.getLongitude())//
			.build();
			mBaiduMap.setMyLocationData(data);
			
			// ���ö�λ���Զ���ͼ��
			MyLocationConfiguration config = new MyLocationConfiguration(LocationMode.NORMAL, true, mIconLocation);
			mBaiduMap.setMyLocationConfigeration(config);
			
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();
			
			// ��һ�ν���Ӧ��ʱ�Ž��ж�λ
			if(isFirstIn){
				toMyLocation();
				
				isFirstIn = false;
				Toast.makeText(mContex, location.getAddrStr(), Toast.LENGTH_SHORT).show();
			}
		}
    	
    }
}
