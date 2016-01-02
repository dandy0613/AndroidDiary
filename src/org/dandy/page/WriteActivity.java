package org.dandy.page;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.dandy.base.BaseActivity;
import org.dandy.db.DiaryContract;
import org.dandy.diary.R;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WriteActivity extends BaseActivity {

	private EditText title, content;
	private TextView category, action;
	private String[] categories;
	private SharedPreferences sharePrefs;
	private Editor editor;
	private int flag;
	private long diary_id;

	public final static int DIARY_INSERT = 0;
	public final static int DIARY_UPDATE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				ex.printStackTrace();
			}
		});
		sharePrefs = getSharedPreferences("diary", Context.MODE_PRIVATE);
		editor = sharePrefs.edit();
		setContentView(R.layout.activity_write);
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
		final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.action_bar, null);
		// Set up your ActionBar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(actionBarLayout);
		title = (EditText) findViewById(R.id.write_title);
		content = (EditText) findViewById(R.id.write_content);
		category = (TextView) findViewById(R.id.write_category);
	}

	@Override
	protected void initView() {
		final TextView bar_title = (TextView) findViewById(R.id.bar_title);
		action = (TextView) findViewById(R.id.bar_action);
		action.setText(getString(R.string.save));
		bar_title.setText(getString(R.string.write_diary));
		Intent intent = getIntent();
		flag = intent.getIntExtra("flag", DIARY_INSERT);
		if (flag == DIARY_UPDATE) {
			title.setText(intent.getStringExtra("title"));
			category.setText(intent.getStringExtra("category"));
			content.setText(intent.getStringExtra("content"));
			diary_id = intent.getLongExtra("id", -1);
		}
	}

	@Override
	protected void setOnClick() {
		category.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = getLayoutInflater();
				LinearLayout add_category_dialog = (LinearLayout) inflater.inflate(R.layout.add_category_dialog, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
				final EditText add_category = (EditText) add_category_dialog.findViewById(R.id.add_category);
				int count = sharePrefs.getInt("category_size", 0);
				if (count > 0) {
					categories = new String[count];
					for (int i = 0; i < count; i++) {
						categories[i] = sharePrefs.getString("category_" + i, "");
					}
				}
				builder.setTitle(R.string.category);
				builder.setItems(categories, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						category.setText(categories[which]);
					}
				});
				builder.setView(add_category_dialog);
				builder.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int count = sharePrefs.getInt("category_size", 0);
						count++;
						editor.putInt("category_size", count);
						editor.putString("category_" + (count - 1), add_category.getText().toString());
						editor.commit();
						category.setText(add_category.getText().toString());
						
					}
				});
				builder.setNegativeButton(getString(R.string.cancel), null);
				builder.create().show();
			}
		});
		action.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(content.getText())) {
					Toast.makeText(WriteActivity.this, WriteActivity.this.getResources().getText(R.string.emptyContent),
							Toast.LENGTH_SHORT).show();
				} else {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
					Date date = new Date(System.currentTimeMillis());
					String created_at = format.format(date);
					ContentValues values = new ContentValues();
					values.put(DiaryContract.Column.TITLE, title.getText().toString());
					values.put(DiaryContract.Column.CATEGORY, TextUtils.isEmpty(category.getText())? 
							null:category.getText().toString());
					values.put(DiaryContract.Column.CONTENT, content.getText().toString());
					values.put(DiaryContract.Column.CREATED_AT, created_at);
					System.out.println("flag: " + flag);
					if (flag == DIARY_INSERT) {
						getContentResolver().insert(DiaryContract.CONTENT_URI, values);
					} else if (flag == DIARY_UPDATE) {
						Uri uri = ContentUris.withAppendedId(DiaryContract.CONTENT_URI, diary_id);
						getContentResolver().update(uri, values, null, null);
					}
					Intent intent = new Intent(WriteActivity.this, DiaryActivity.class);
					startActivity(intent);
				}
			}
		});
	}

}
