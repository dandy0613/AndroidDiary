package org.dandy.page;

import org.dandy.base.BaseActivity;
import org.dandy.db.DiaryContract;
import org.dandy.diary.R;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends BaseActivity {

	public final static String TAG = "DetailActivity";
	
	private TextView category, created_at, content, action, bar_title;
	private Button delete;
	private long id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
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
		category = (TextView)findViewById(R.id.detail_category);
		created_at = (TextView)findViewById(R.id.detail_created_at);
		content = (TextView)findViewById(R.id.detail_content);
		delete = (Button)findViewById(R.id.delete_diary);
	}

	@Override
	protected void initView() {
		bar_title = (TextView)findViewById(R.id.bar_title);
	    action = (TextView)findViewById(R.id.bar_action);
	    action.setText(getString(R.string.edit));
	    bar_title.setText(getString(R.string.my_diary));
		Intent intent = getIntent();
		id = intent.getLongExtra("id", -1);
	    bar_title.setText(intent.getStringExtra("title"));
	    bar_title.setEllipsize(TextUtils.TruncateAt.END);
		category.setText(intent.getStringExtra("category"));
		created_at.setText(intent.getStringExtra("created_at"));
		content.setText(intent.getStringExtra("content"));
	}

	@Override
	protected void setOnClick() {
		action.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DetailActivity.this, WriteActivity.class);
				intent.putExtra("flag", WriteActivity.DIARY_UPDATE);
				intent.putExtra("id", id);
				intent.putExtra("title", bar_title.getText());
				intent.putExtra("category", category.getText());
				intent.putExtra("created_at", created_at.getText());
				intent.putExtra("content", content.getText());
				startActivity(intent);
			}
		});
		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
				builder.setTitle(getString(R.string.delete));
				builder.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Uri uri = ContentUris.withAppendedId(DiaryContract.CONTENT_URI, id);
						getContentResolver().delete(uri, null, null);
						finish();
					}
				});
				builder.setNegativeButton(getString(R.string.cancel), null);
				builder.create().show();
			}
		});
	}
}
