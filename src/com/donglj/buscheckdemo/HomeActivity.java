package com.donglj.buscheckdemo;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineResult.BusStation;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.PoiInfo.POITYPE;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObservable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

    public class HomeActivity extends Activity implements OnGetPoiSearchResultListener,OnItemClickListener,OnGetBusLineSearchResultListener{
	//city输入编辑框
	private EditText mEtCity = null;
	//公交车号输入编辑框
	private EditText mEtBusLine = null;
	//搜索按钮对象
	private Button mBtnSearch = null;
	private Button mBtnCancel = null;
	private Button mBtnCheck = null;
	private Button mBtnNear = null;
	
	//搜索功能相关的对象
	private PoiSearch mPoiSearch = null;
	private List<PoiInfo> mPoiInfos;
	//显示列表对象
	private ListView mListView;
	private ListAdapter mAdapter;
	
	private BusLineSearch mBusLineSearch;
	
	List<String> datas;
	
	private boolean mIsShowBusStation = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub		
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		//设置布局文件
		setContentView(R.layout.activity_home);
		initView();
		Log.d("PoiResult", "onCreate");	
		
	}
	
	private void initView(){
		datas = new ArrayList<String>();
		mEtCity = (EditText) findViewById(R.id.et_city_input);
		mEtBusLine = (EditText) findViewById(R.id.et_bus);
		mListView = (ListView) findViewById(R.id.lv_list);
		//监听列表中的某一项数据的点击事件
		mListView.setOnItemClickListener(this);
		
		mBtnCheck = (Button) findViewById(R.id.btn_check);
		mBtnCheck.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intentChk = new Intent();
				intentChk.setClass(HomeActivity.this, MapShowActivity.class);
				startActivity(intentChk);
			}
		});
		
		mBtnNear = (Button) findViewById(R.id.btn_near);
		mBtnNear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intentChk = new Intent();
				intentChk.setClass(HomeActivity.this, NearbyBusStationActivity.class);
				startActivity(intentChk);
			}
		});
		
		mBtnCancel = (Button) findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mEtBusLine.setText("");
				mEtCity.setText("");
			}
		});
		
		
		//实例化按钮
		mBtnSearch = (Button) findViewById(R.id.btn_search);
		//获取按钮的点击事件
		mBtnSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// 按钮点击时回调方法
				handlePoiSearch();
				
			}

			
		});
		
		//实例化查询对象
		mPoiSearch = PoiSearch.newInstance();
		mPoiInfos = new ArrayList<PoiInfo>();
		// 添加监听世家
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		
		mBusLineSearch = BusLineSearch.newInstance();
		mBusLineSearch.setOnGetBusLineSearchResultListener(this);
	}
	
	//处理搜索事件
	private void handlePoiSearch() {
		//异常判断
		if(mEtCity.getText().toString().isEmpty()){
			//如果城市未输入，设置为默认值太原
			mEtCity.setText("太原");
			//Toast.makeText(this, "请输入城市名", Toast.LENGTH_SHORT).show();
			
		}
		if (mEtBusLine.getText().toString().isEmpty()){
			//提示用户，工具不能空
			Toast.makeText(this, "请输入公交车号", Toast.LENGTH_SHORT).show();
			return;
			
		}
		//正常情况下的处理,查询，根据城市，公交车号查询
		searchPoiInfo();
		
		
	}
	
	private void searchPoiInfo(){
		//根据公交车和城市 查询公交线路
		PoiCitySearchOption option = new PoiCitySearchOption();
		option.city(mEtCity.getText().toString());
		option.keyword(mEtBusLine.getText().toString());
		
		
		mPoiSearch.searchInCity(option);
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiResult(PoiResult arg0) {
		mIsShowBusStation = false;
		
		//异常判断,如果没有错误，获取结果集，否则弹出Toast
		if (arg0.error == ERRORNO.NO_ERROR) {		
		//获取返回结果
		mPoiInfos = arg0.getAllPoi();
		datas.clear();
		
		//遍历结果集
		for (int i = 0; i < mPoiInfos.size(); i++) {
			Log.d("PoiResult", mPoiInfos.get(i).name);	
			if (mPoiInfos.get(i).type == POITYPE.BUS_LINE || mPoiInfos.get(i).type == POITYPE.SUBWAY_LINE ) {
				datas.add(mPoiInfos.get(i).name);
			}
		}
		mAdapter = new ArrayAdapter<String>(HomeActivity.this,R.layout.list_item_home,datas);
		mListView.setAdapter(mAdapter);
		
	  }
		else {
		Toast.makeText(this, "无法获取线路", Toast.LENGTH_SHORT).show();
	}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(!mIsShowBusStation){
		
		BusLineSearchOption option = new BusLineSearchOption();
		option.city(mEtCity.getText().toString());
		option.uid(mPoiInfos.get(arg2).uid);
		mBusLineSearch.searchBusLine(option);
		}	
	}

	@Override
	public void onGetBusLineResult(BusLineResult arg0) {
		mIsShowBusStation = true;
		//List<String> titles = new ArrayList<String>();
		
		datas.clear();
		//获取所有站点信息
		List<BusStation> stations = arg0.getStations();
		for (int i = 0; i < stations.size(); i++) {
			Log.d("BusStationResult",stations.get(i).getTitle());
			datas.add(stations.get(i).getTitle());
		}
		//mAdapter.notify();
		ListAdapter adapter = new ArrayAdapter<String>(HomeActivity.this, R.layout.list_item_home,datas);
		mListView.setAdapter(adapter);
		
		
		
	}

}
