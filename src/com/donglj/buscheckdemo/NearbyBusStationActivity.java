package com.donglj.buscheckdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.PoiInfo.POITYPE;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

public class NearbyBusStationActivity extends Activity implements OnGetPoiSearchResultListener{
	
	private ListView mNearbyListView;
	private List<String> mStationList;
	private PoiSearch mPoiSearch;
	List<PoiInfo> mPoiInfoList;
	private ArrayAdapter<String> mAdapter;
	
	private LocationClient mLocationClient;
	private LatLng mCurrentLatLng;
	private boolean isActive;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("test", "onCreate");
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext()); 
		setContentView(R.layout.nearby_bus_station);
		mNearbyListView = (ListView) findViewById(R.id.nearby_station_list);
		mPoiSearch = PoiSearch.newInstance();
		mStationList = new ArrayList<String>();
		mPoiInfoList = new ArrayList<PoiInfo>();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mAdapter = new ArrayAdapter<String>(this, R.layout.station_list_item, mStationList);
		mNearbyListView.setAdapter(mAdapter);

	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		isActive = true;
		super.onResume();
		searchStation();
	}

	
	@Override
	protected void onPause() {
		isActive = false;
		if(mPoiSearch != null){
			mPoiSearch.destroy();
		}
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		super.onPause();
	}


	@Override
	protected void onDestroy() {
		if(mPoiSearch != null){
			mPoiSearch.destroy();
		}
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		super.onDestroy();
	}


	@Override
	protected void onStop() {
		if(mPoiSearch != null){
			mPoiSearch.destroy();
		}
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		super.onStop();
	}


	private void searchStation(){
		Log.e("test", "searchStation");
		getCurrentLocation();
	}


	private void getCurrentLocation(){
		mLocationClient = new LocationClient(getApplicationContext());
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving);
		option.setOpenGps(true);
		option.setCoorType("bd09ll");                           //设置坐标类型为bd09ll 
        option.setProdName("BusCheckDemo");                      //设置产品线名称 
        option.setScanSpan(5000);                               //定时定位，每隔5秒钟定位一次。 
        mLocationClient.setLocOption(option); 
        mLocationClient.registerLocationListener(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation arg0) {
				if (!isActive) {
					return;
				}
				mCurrentLatLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
				Log.e("test", "Receive:mCurrentLatLng;" + arg0.getLatitude());
				Log.e("test", "mCurrentLatLng;" + arg0.getLongitude());
		        if (mCurrentLatLng == null) {
		        	Log.e("test", "mCurrentLatLng=null");
		        	mCurrentLatLng = new LatLng(31.33539206, 120.770669);
				}
				PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
				
				nearbySearchOption.location(mCurrentLatLng);
				nearbySearchOption.radius(1000);
				nearbySearchOption.keyword("公交");
				mPoiSearch.searchNearby(nearbySearchOption);
			}
		});
        mLocationClient.start();
        if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
		}else {
			Log.e("test", "not start");
		}
	}
	
	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		Log.e("test", "onGetPoiResult");
		if (!isActive) {
			return;
		}
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(NearbyBusStationActivity.this, "未找到结果", Toast.LENGTH_LONG)
			.show();
			return;
		}

		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mStationList.clear();
			mPoiInfoList.clear();
			mPoiInfoList = result.getAllPoi();
			for (int i = 0; i < mPoiInfoList.size(); i++) {
				Log.e("text", "xxxxx:" + i);
				Log.e("text", mPoiInfoList.get(i).name);
				Log.e("text", mPoiInfoList.get(i).address);
				if (mPoiInfoList.get(i).type == POITYPE.BUS_STATION) {
					mStationList.add(mPoiInfoList.get(i).name + "\n" + "(" + mPoiInfoList.get(i).address +")" );
				}

			}
			mAdapter.notifyDataSetChanged();
			mNearbyListView.invalidate();
			return;
		}
		
	}

}
