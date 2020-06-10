package com.donglj.buscheckdemo;

import android.nfc.Tag;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends Activity implements AnimationListener{
	private static final String TAG = "MainActivity";
		
	
	private ImageView mIvStartApp;   //����ؼ�����
	private Animation mAnimation;    //��������

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //���ز����ļ�
        setContentView(R.layout.activity_main);
        //��ʼ������
        initView();
    }
    
    private void initView(){
    	//��ʼ������
    	mIvStartApp = (ImageView) findViewById(R.id.iv_anni);
    	//���ض���
    	mAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_start_app);
    	//���ü������������¼�
    	mAnimation.setAnimationListener(this);
    	
    	//���ö���
    	mIvStartApp.setAnimation(mAnimation);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		//������Թ���
		Log.d(TAG, "start");
		//�������������¼���������ʱ����Ǩ��
		Intent intent = new Intent(MainActivity.this, HomeActivity.class);
		startActivity(intent);
		//ֹͣ��ǰActivity
		MainActivity.this.finish();
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "reapt");
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "end");
	}
    
}
