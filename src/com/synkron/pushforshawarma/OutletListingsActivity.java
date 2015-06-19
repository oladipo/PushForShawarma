package com.synkron.pushforshawarma;

import com.synkron.pushforshawarma.adapters.OutletsCursorAdapter;
import com.synkron.pushforshawarma.contentproviders.OutletsContentProvider;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;

public class OutletListingsActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{

	private static String TAG = "OutletListingsActivity";
	private ProgressBar _progressBar;
	private ListView mOutletsListView;
	
	private OutletsCursorAdapter _cursorAdapter;	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outlet_listings);
		
		mOutletsListView = (ListView) findViewById(R.id.outlets_list);
		mOutletsListView.setTextFilterEnabled(true);
		
		_progressBar = (ProgressBar) findViewById(R.id.progressOutlets);
		
		//create an empty adapter we will use to display loaded data....
		_cursorAdapter = new OutletsCursorAdapter(this, null, CursorAdapter.NO_SELECTION);
		
		getSupportLoaderManager().initLoader(0, null, this);
		
		//set listview adapter to a cursor adapter...
		mOutletsListView.setAdapter(_cursorAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.outlet_listings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Log.i(TAG, "onCreateLoader running...");
		
		String[] projection = new String[]{
				OutletsContentProvider.KEY_ID,
				OutletsContentProvider.KEY_OUTLET_ICON,
				OutletsContentProvider.KEY_OUTLET_NAME,
				OutletsContentProvider.KEY_OUTLET_LONGITUDE,
				OutletsContentProvider.KEY_OUTLET_LATITUDE,
				OutletsContentProvider.KEY_OUTLET_ADDRESS,
				OutletsContentProvider.KEY_OUTLET_PHONE,
				OutletsContentProvider.KEY_OUTLET_EMAIL				
		};
		
		CursorLoader loader = new CursorLoader(this, OutletsContentProvider.CONTENT_URI, projection,null,null, null);
		
		Log.i(TAG, "onCreateLoader complete");
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.i(TAG, "onLoadFinished called..");
		_cursorAdapter.swapCursor(cursor);
		_progressBar.setVisibility(ProgressBar.GONE);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		Log.i(TAG, "onLoaderReset called..");
		_cursorAdapter.swapCursor(null);
	}
}
