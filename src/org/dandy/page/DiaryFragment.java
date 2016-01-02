package org.dandy.page;

import org.dandy.db.DiaryContract;
import org.dandy.diary.R;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class DiaryFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_diary, null,
				new String[] { DiaryContract.Column.TITLE, DiaryContract.Column.CREATED_AT },
				new int[] { R.id.item_title, R.id.item_created_at }, 
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setEmptyText(getString(R.string.noDiary));
		setListAdapter(adapter);
		setListShown(false); 
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListAdapter().getItem(position);
		System.out.println(c.getColumnCount());
		Intent intent = new Intent(getActivity(), DetailActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("title", c.getString(c.getColumnIndex(DiaryContract.Column.TITLE)));
		intent.putExtra("category", c.getString(c.getColumnIndex(DiaryContract.Column.CATEGORY)));
		intent.putExtra("created_at", c.getString(c.getColumnIndex(DiaryContract.Column.CREATED_AT)));
		intent.putExtra("content", c.getString(c.getColumnIndex(DiaryContract.Column.CONTENT)));
		startActivity(intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), DiaryContract.CONTENT_URI, 
				new String[]{DiaryContract.Column._ID, DiaryContract.Column.TITLE, 
						DiaryContract.Column.CATEGORY,
						DiaryContract.Column.CREATED_AT, DiaryContract.Column.CONTENT}, 
				null, null, DiaryContract.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);  
        // The list should now be shown.  
        if (isResumed()) {  
            setListShown(true);  
        } else {  
            setListShownNoAnimation(true);  
        }  
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	
}
