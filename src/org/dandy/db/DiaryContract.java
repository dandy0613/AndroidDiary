package org.dandy.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class DiaryContract {
	public static final String DB_NAME = "diary.db";
	public static final int DB_VERSION = 1;
	public static final String TABLE = "diary";
	
	public static final String AUTHORITY = "org.dandy.db.DiaryProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY + "/"+TABLE);
	public static final int POST_ITEM = 1;
	public static final int POST_DIR = 2;
	public static final String POST_TYPE_ITEM = "vnd.android.cursor.item/"
			+ "vnd.org.dandy.db.diary";
	public static final String POST_TYPE_DIR = "vnd.android.cursor.dir/"
			+ "vnd.org.dandy.db.diary";
	
	public static final String DEFAULT_SORT = Column.CREATED_AT + " DESC";
	
	public class Column{
		public static final String _ID = BaseColumns._ID;
		public static final String TITLE = "title";
		public static final String CATEGORY = "category";
		public static final String CONTENT = "content";
		public static final String CREATED_AT = "created_at";
	}
}
