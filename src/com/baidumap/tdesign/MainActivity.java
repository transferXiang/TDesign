package com.baidumap.tdesign;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

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
	
	Context context;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext()); 
        
        context = this;
        
        setContentView(R.layout.activity_main);
        
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        
        mLocaltionClient = new LocationClient(this);
        mLocationListener = new MyLocationListenr();
        mLocaltionClient.registerLocationListener(mLocationListener);
        
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000); //1s��ɨ��һ��
        mLocaltionClient.setLocOption(option);
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
    	// ������λ
    	mBaiduMap.setMyLocationEnabled(true);
    	if(!mLocaltionClient.isStarted()){
    		mLocaltionClient.start();
    	}
    	
    	super.onStart();
    }
    
    @Override
    protected void onStop() {
    	// ֹͣ��λ
    	if(mLocaltionClient.isStarted()){
    		mLocaltionClient.stop();
    	}
    	mBaiduMap.setMyLocationEnabled(false);
    	super.onStop();
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
			.accuracy(location.getRadius())//
			.latitude(location.getLatitude())//
			.longitude(location.getLongitude())//
			.build();
			mBaiduMap.setMyLocationData(data);
			
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();
			
			// ��һ�ν���Ӧ��ʱ�Ž��ж�λ
			if(isFirstIn){
				toMyLocation();
				
				isFirstIn = false;
				Toast.makeText(context, location.getAddrStr(), Toast.LENGTH_SHORT).show();
			}
		}
    	
    }
}
