package com.baidumap.tdesign;


import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext()); 
        
        setContentView(R.layout.activity_main);
        
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
			
		default:
			Log.d("MainActivity.onOptionsItemSelected", "unknown item type");
			break;
		}
    	return super.onOptionsItemSelected(item);
    }
}
