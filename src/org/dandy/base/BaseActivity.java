package org.dandy.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public abstract class BaseActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		findView();
		initView();
		setOnClick();
	}

	/**
	 * 获取布局控件
	 */
	protected abstract void findView();

	/**
	 * 初始化View的一些数据
	 */
	protected abstract void initView();

	/**
	 * 设置点击监听
	 */
	protected abstract void setOnClick();
}
