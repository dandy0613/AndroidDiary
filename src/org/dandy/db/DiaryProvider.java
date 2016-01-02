package org.dandy.db;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class DiaryProvider extends ContentProvider {
	
	private static final String TAG = DiaryContract.class.getSimpleName();
	private DbHelper dbHelper;
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static{
		sURIMatcher.addURI(DiaryContract.AUTHORITY, 
				DiaryContract.TABLE, DiaryContract.POST_DIR);
		sURIMatcher.addURI(DiaryContract.AUTHORITY,
				DiaryContract.TABLE + "/#", DiaryContract.POST_ITEM);
	}

	@Override
	public boolean onCreate() {
		Log.d(TAG, "onCreated");
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				ex.printStackTrace();
			}
		});
		dbHelper = new DbHelper(this.getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, 
			String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(DiaryContract.TABLE);
		
		switch(sURIMatcher.match(uri)){
		case DiaryContract.POST_DIR:
			break;
		case DiaryContract.POST_ITEM:
			qb.appendWhere(DiaryContract.Column._ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Illegal uri: " + uri);
		}
		
		String orderBy = (TextUtils.isEmpty(sortOrder)? DiaryContract.DEFAULT_SORT : sortOrder);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		
		Log.d(TAG, "queried uri: "+uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch(sURIMatcher.match(uri)){
		case DiaryContract.POST_DIR:
			Log.d(TAG, "gotType: " + DiaryContract.POST_TYPE_DIR);
			return DiaryContract.POST_TYPE_DIR;
		case DiaryContract.POST_ITEM:
			Log.d(TAG, "gotType: " + DiaryContract.POST_TYPE_ITEM);
			return DiaryContract.POST_TYPE_ITEM;
		default:
			throw new IllegalArgumentException("Illegal uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		System.out.println(uri);
		Uri ret = null;
		if(sURIMatcher.match(uri) != DiaryContract.POST_DIR)
			throw new IllegalArgumentException("Illegal uri: " + uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId = db.insertWithOnConflict(DiaryContract.TABLE, null,
				values, SQLiteDatabase.CONFLICT_IGNORE);
		System.out.println("insert: "+ rowId);
		if(rowId != -1){
			//long id = values.getAsLong(DiaryContract.Column._ID);
			ret = ContentUris.withAppendedId(uri, rowId);
			Log.d(TAG, "insert uri: "+uri);
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return ret;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String where;
		
		switch(sURIMatcher.match(uri)){
		case DiaryContract.POST_DIR:
			where = (selection == null)? "1": selection;
			break;
		case DiaryContract.POST_ITEM:
			long id = ContentUris.parseId(uri);
			where = DiaryContract.Column._ID + "=" + id 
					+ (TextUtils.isEmpty(selection)?"":" and ( " + selection + " )");
			break;
		default:
			throw new IllegalArgumentException("Illegal uri: " + uri);
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int ret = db.delete(DiaryContract.TABLE, where, selectionArgs);
		
		if(ret > 0)getContext().getContentResolver().notifyChange(uri, null);
		Log.d(TAG, "deleted records: " + ret);
		return ret;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		String where;
		
		switch(sURIMatcher.match(uri)){
		case DiaryContract.POST_DIR:
			where = selection;
			break;
		case DiaryContract.POST_ITEM:
			long id = ContentUris.parseId(uri);
			where = DiaryContract.Column._ID + "=" + id 
					+ (TextUtils.isEmpty(selection)?"":" and ( " + selection + " )");
			break;
		default:
			throw new IllegalArgumentException("Illegal uri: " + uri);
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int ret = db.update(DiaryContract.TABLE, values, where, selectionArgs);
		
		if(ret > 0)getContext().getContentResolver().notifyChange(uri, null);
		Log.d(TAG, "updated records: " + ret);
		return ret;
	}

}
