package com.donglj.buscheckdemo;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class MapShowActivity extends Activity {

	private MapView mMapView;
	private BaiduMap mBaiduMap;

	private LocationClient mLocationClient;
	private LatLng mCurrentLatLng;
	private BitmapDescriptor mCurrentMarker;
	boolean mIsFirstLoc = true;// 是否首次定位

	private boolean isActive;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.bus_map);
		mMapView = (MapView) findViewById(R.id.map_view);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_show, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mMapView) {
			mMapView.onDestroy();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		isActive = false;
		if (null != mMapView) {
			mMapView.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isActive = true;
		locatCurrentPosition();
		if (null != mMapView) {
			mMapView.onResume();
		}

	}

	public void locatCurrentPosition() {
		mLocationClient = new LocationClient(getApplicationContext());
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving);
		option.setOpenGps(true);
		option.setCoorType("bd09ll"); // 设置坐标类型为bd09ll
		option.setProdName("BusCheckDemo"); // 设置产品线名称
		option.setScanSpan(5000); // 定时定位，每隔5秒钟定位一次。
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				if (!isActive) {
					return;
				}
				// map view 销毁后不在处理新接收的位置
				if (location == null || mMapView == null)
					return;
				
				MyLocationData locData = new MyLocationData.Builder()
						.accuracy(location.getRadius())
						// 此处设置开发者获取到的方向信息，顺时针0-360
						.direction(100).latitude(location.getLatitude())
						.longitude(location.getLongitude()).build();
				
//				MyLocationData locData = new MyLocationData.Builder()
//						.accuracy(location.getRadius())
//						// 此处设置开发者获取到的方向信息，顺时针0-360
//						.direction(100).latitude(31.33539206)
//						.longitude(120.770669).build();
				mBaiduMap.setMyLocationData(locData);
				if (mIsFirstLoc) {
					mIsFirstLoc = false;
					LatLng ll = new LatLng(location.getLatitude(), location
							.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
					mBaiduMap.animateMapStatus(u);
				}
			}
		});
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
		} else {
			Log.e("test", "not start");
		}

	}
}
