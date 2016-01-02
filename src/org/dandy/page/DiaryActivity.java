package org.dandy.page;

import org.dandy.base.BaseActivity;
import org.dandy.diary.R;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class DiaryActivity extends BaseActivity {
	
	private TextView action;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary);
		if(savedInstanceState == null){
			getFragmentManager().beginTransaction()
				.replace(R.id.fragment_diary, new DiaryFragment()).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void findView() {
		// Inflate your custom layout
	    final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
	            R.layout.action_bar,null);
	    // Set up your ActionBar
	    final ActionBar actionBar = getActionBar();
	    actionBar.setDisplayShowHomeEnabled(true);
	    actionBar.setDisplayShowTitleEnabled(true);
	    actionBar.setDisplayShowCustomEnabled(true);
	    actionBar.setCustomView(actionBarLayout);
	}

	@Override
	protected void initView() {
		final TextView bar_title = (TextView)findViewById(R.id.bar_title);
	    action = (TextView)findViewById(R.id.bar_action);
	    action.setText(getString(R.string.write_diary));
	    bar_title.setText(getString(R.string.my_diary));
	}

	@Override
	protected void setOnClick() {
		action.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DiaryActivity.this, WriteActivity.class);
				intent.putExtra("flag", WriteActivity.DIARY_INSERT);
				startActivity(intent);
			}
		});
	}
}
