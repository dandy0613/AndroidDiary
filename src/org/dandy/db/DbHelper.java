package org.dandy.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	
	private static final String TAG = DbHelper.class.getSimpleName();

	public DbHelper(Context context){
		super(context, DiaryContract.DB_NAME, null, DiaryContract.DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = String.format("create table %s (%s integer primary key autoincrement, %s text, "
				+ "%s text, %s text, %s text)", DiaryContract.TABLE, DiaryContract.Column._ID,
				DiaryContract.Column.TITLE, DiaryContract.Column.CATEGORY, DiaryContract.Column.CONTENT,
				DiaryContract.Column.CREATED_AT);
		Log.d(TAG, "onCreate with sql: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists " + DiaryContract.TABLE);
		onCreate(db);
	}

}
