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
	//city����༭��
	private EditText mEtCity = null;
	//������������༭��
	private EditText mEtBusLine = null;
	//������ť����
	private Button mBtnSearch = null;
	private Button mBtnCancel = null;
	private Button mBtnCheck = null;
	private Button mBtnNear = null;
	
	//����������صĶ���
	private PoiSearch mPoiSearch = null;
	private List<PoiInfo> mPoiInfos;
	//��ʾ�б����
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
		//���ò����ļ�
		setContentView(R.layout.activity_home);
		initView();
		Log.d("PoiResult", "onCreate");	
		
	}
	
	private void initView(){
		datas = new ArrayList<String>();
		mEtCity = (EditText) findViewById(R.id.et_city_input);
		mEtBusLine = (EditText) findViewById(R.id.et_bus);
		mListView = (ListView) findViewById(R.id.lv_list);
		//�����б��е�ĳһ�����ݵĵ���¼�
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
		
		
		//ʵ������ť
		mBtnSearch = (Button) findViewById(R.id.btn_search);
		//��ȡ��ť�ĵ���¼�
		mBtnSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// ��ť���ʱ�ص�����
				handlePoiSearch();
				
			}

			
		});
		
		//ʵ������ѯ����
		mPoiSearch = PoiSearch.newInstance();
		mPoiInfos = new ArrayList<PoiInfo>();
		// ��Ӽ�������
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		
		mBusLineSearch = BusLineSearch.newInstance();
		mBusLineSearch.setOnGetBusLineSearchResultListener(this);
	}
	
	//���������¼�
	private void handlePoiSearch() {
		//�쳣�ж�
		if(mEtCity.getText().toString().isEmpty()){
			//�������δ���룬����ΪĬ��ֵ̫ԭ
			mEtCity.setText("̫ԭ");
			//Toast.makeText(this, "�����������", Toast.LENGTH_SHORT).show();
			
		}
		if (mEtBusLine.getText().toString().isEmpty()){
			//��ʾ�û������߲��ܿ�
			Toast.makeText(this, "�����빫������", Toast.LENGTH_SHORT).show();
			return;
			
		}
		//��������µĴ���,��ѯ�����ݳ��У��������Ų�ѯ
		searchPoiInfo();
		
		
	}
	
	private void searchPoiInfo(){
		//���ݹ������ͳ��� ��ѯ������·
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
		
		//�쳣�ж�,���û�д��󣬻�ȡ����������򵯳�Toast
		if (arg0.error == ERRORNO.NO_ERROR) {		
		//��ȡ���ؽ��
		mPoiInfos = arg0.getAllPoi();
		datas.clear();
		
		//���������
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
		Toast.makeText(this, "�޷���ȡ��·", Toast.LENGTH_SHORT).show();
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
		//��ȡ����վ����Ϣ
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
