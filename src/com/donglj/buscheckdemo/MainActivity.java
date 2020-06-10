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
		
	
	private ImageView mIvStartApp;   //滑块控件对象
	private Animation mAnimation;    //动画对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载布局文件
        setContentView(R.layout.activity_main);
        //初始化操作
        initView();
    }
    
    private void initView(){
    	//初始化画面
    	mIvStartApp = (ImageView) findViewById(R.id.iv_anni);
    	//加载动画
    	mAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_start_app);
    	//设置监听动画结束事件
    	mAnimation.setAnimationListener(this);
    	
    	//设置动画
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
		//代码调试工具
		Log.d(TAG, "start");
		//监听动画结束事件，当结束时画面迁移
		Intent intent = new Intent(MainActivity.this, HomeActivity.class);
		startActivity(intent);
		//停止当前Activity
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
