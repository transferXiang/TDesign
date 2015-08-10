package com.baidumap.tdesign;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyOrientationListener implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Context mContex;
	private float mLastX;
	
	
	public MyOrientationListener(Context mContex) {
		super();
		this.mContex = mContex;
	}

	@SuppressWarnings("deprecation")
	public void Start(){
		mSensorManager = (SensorManager) mContex.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager != null){
			// 获得方向传感器
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		
		// 注册方向传感器
		if(mSensor != null){
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	public void Stop(){
		// 注销方向传感器
		if(mSensorManager != null){
			mSensorManager.unregisterListener(this);
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO 自动生成的方法存根

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			// 获得X轴的坐标
			float x = event.values[SensorManager.DATA_X];
			
			// 如果方向发生变化则调用接口
			if (Math.abs(x - mLastX) > 0.1f){
				if (mOnOrientationListener != null){
					mOnOrientationListener.onOrientationChanged(x);
				}
			}
			
			mLastX = x;
		}

	}
	
	private OnOrientationListener mOnOrientationListener;
	
	public void setOnOrientationListener(
			OnOrientationListener mOnOrientationListener) {
		this.mOnOrientationListener = mOnOrientationListener;
	}

	public interface OnOrientationListener{
		void onOrientationChanged(float x);
	}

}
