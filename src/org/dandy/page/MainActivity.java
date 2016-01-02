package org.dandy.page;

import java.util.Locale;

import org.dandy.base.BaseActivity;
import org.dandy.diary.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	private EditText password;
	private TextView done;
	private ImageView clear;
	private SharedPreferences sharePrefs;
	private Editor editor;
	private int confirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharePrefs = getSharedPreferences("diary", Context.MODE_PRIVATE);
		editor = sharePrefs.edit();
		super.setContentView(R.layout.activity_main);
	}

	@Override
	protected void findView() {
		password = (EditText) findViewById(R.id.main_password);
		done = (TextView) findViewById(R.id.main_done);
		clear = (ImageView) findViewById(R.id.main_clearInput);
	}

	@Override
	protected void initView() {
		confirm = sharePrefs.getInt("confirm", 0);
		int lang = sharePrefs.getInt("language_pref", 1);
		Configuration config = getResources().getConfiguration();//获取系统的配置  
		switch(lang){
		case 0:
			config.locale = Locale.SIMPLIFIED_CHINESE;
			getResources().updateConfiguration(config, getResources().getDisplayMetrics());
			break;
		case 1:
			config.locale = Locale.ENGLISH;
			getResources().updateConfiguration(config, getResources().getDisplayMetrics());
			break;
		}
		switch(confirm){
			case 0:
				password.setHint(getResources().getText(R.string.firstPwd));
				break;
			case 1:
				password.setHint(getResources().getText(R.string.secondPwd));
				break;
			case 2:
				password.setHint(getResources().getText(R.string.inputPwd));
				break;
		}
	}

	@Override
	protected void setOnClick() {
		done.setClickable(false);
		done.setOnClickListener(new doneOnClickListener());
		password.addTextChangedListener(new TextWatcher() {
			CharSequence temp;
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s;
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}
			@SuppressLint({ "ResourceAsColor", "NewApi" })
			@Override
			public void afterTextChanged(Editable s) {
				if(temp.length()==0){
					clear.setClickable(false);
					clear.setImageResource(R.drawable.clear_unable);
				}
				else{
					clear.setClickable(true);
					clear.setImageResource(R.drawable.clear_enable);
					clear.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							password.setText("");
						}
					});
				}
				if(temp.length()==6){
					//The password should be consist of 6 numbers
					done.setClickable(true);
					done.setBackgroundResource(android.R.color.holo_green_light);
				}
				else{
					done.setClickable(false);
					done.setBackgroundResource(android.R.color.darker_gray);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu) {
		getMenuInflater().inflate(R.menu.language, menu); 
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
			case R.id.switch_language:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setItems(R.array.language, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Configuration config = getResources().getConfiguration();//获取系统的配置  
						editor.putInt("language_pref", which);
						editor.commit();
						initView();
					}
				});
				builder.create().show();
		}
		return super.onOptionsItemSelected(item);
	}


	class doneOnClickListener implements OnClickListener{
		@SuppressLint("ShowToast")
		@Override
		public void onClick(View v) {
			confirm = sharePrefs.getInt("confirm", 0);
			if (TextUtils.isEmpty(password.getText())) {
				//The password should not be empty
				Toast.makeText(MainActivity.this, 
						MainActivity.this.getResources().getText(R.string.emptyPwd),
						Toast.LENGTH_SHORT).show();
			} else {
				if (confirm == 0) {
					//First time input the password
					editor.putInt("confirm", 1);
					editor.putString("password", password.getText().toString());
					editor.commit();
					password.setText("");
					password.setHint(getResources().getText(R.string.secondPwd));
				} else if (confirm == 1) {
					//Second time input the password
					String pwd = sharePrefs.getString("password", null);
					if(TextUtils.isEmpty(pwd) || !TextUtils.equals(pwd, password.getText())){
						editor.putInt("confirm", 0);
						editor.putString("password", null);
						editor.commit();
						Toast.makeText(MainActivity.this, 
								MainActivity.this.getResources().getText(R.string.notMatchedPwd),
								Toast.LENGTH_SHORT).show();
					}
					else{
						editor.putInt("confirm", 2);
						editor.commit();
						Toast.makeText(MainActivity.this, 
								MainActivity.this.getResources().getText(R.string.matchedPwd),
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
						startActivity(intent);
					}
				}else if (confirm == 2) {
					//check the password
					String pwd = sharePrefs.getString("password", null);
					if(TextUtils.isEmpty(pwd) || !TextUtils.equals(pwd, password.getText())){
						Toast.makeText(MainActivity.this, 
								MainActivity.this.getResources().getText(R.string.wrongPwd),
								Toast.LENGTH_SHORT).show();
						password.setText("");
					}
					else{
						Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
						startActivity(intent);
						finish();
					}
				}
			}
		}
	}
}
