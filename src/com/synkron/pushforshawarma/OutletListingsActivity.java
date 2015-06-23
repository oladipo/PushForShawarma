package com.synkron.pushforshawarma;

import com.synkron.pushforshawarma.adapters.OutletsCursorAdapter;
import com.synkron.pushforshawarma.broadcastreceivers.OutletsUpdateAlarmReceiver;
import com.synkron.pushforshawarma.contentproviders.OutletsContentProvider;
import com.synkron.pushforshawarma.utils.AppConstants;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.AdapterView.OnItemClickListener;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;

public class OutletListingsActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>,
	OnRefreshListener{

	private static String TAG = "OutletListingsActivity";
	private ListView mOutletsListView;
	private SwipeRefreshLayout mRefreshLayout;	
	private OutletsCursorAdapter _cursorAdapter;	
	private TextView mLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outlet_listings);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		mOutletsListView = (ListView) findViewById(R.id.outlets_list);
		mOutletsListView.setTextFilterEnabled(true);
		
		mLocation = (TextView) findViewById(R.id.userAddress);
		mLocation.setText(bundle.getString("USER_LOCATION_ADDRESS"));
		
		mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
		mRefreshLayout.setOnRefreshListener(this);
		mRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
		
		//create an empty adapter we will use to display loaded data....
		_cursorAdapter = new OutletsCursorAdapter(this, null, CursorAdapter.NO_SELECTION);
		
		getSupportLoaderManager().initLoader(0, null, this);
		
		//set listview adapter to a cursor adapter...
		mOutletsListView.setAdapter(_cursorAdapter);
		
		mOutletsListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showOutletDetails(id);
			}
			
		});
	}

	public void showOutletDetails(long outletID){
		//fire implicit intent 
		Intent intent = new Intent(AppConstants.ACTION_VIEW_OUTLET);
		
		intent.putExtra("OUTLET_URI", 
			Uri.withAppendedPath(OutletsContentProvider.CONTENT_URI, String.valueOf(outletID)).toString());
		
		intent.addCategory(Intent.CATEGORY_DEFAULT); 

		Log.i(TAG, "Intent sent with action :" + intent.getAction());
		
		PackageManager pm = getPackageManager();
		ComponentName cn = intent.resolveActivity(pm);
		
		if(cn != null){
			startActivity(intent);
		}else{
			Log.w(TAG, "No Activity Found to Handle Acttion : " + intent.getAction());
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();      
        inflater.inflate(R.menu.outlet_listings, menu);
        
		//activates the searchable activity....
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()) );
        
        return true;
		//return super.onCreateOptionsMenu(menu);
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
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		Log.i(TAG, "onLoaderReset called..");
		_cursorAdapter.swapCursor(null);
	}

	@Override
	public void onRefresh() {
		//fire stockUpdate Intent
	    Intent alarmIntent = new Intent(OutletsUpdateAlarmReceiver.ACTION_UPDATE_OUTLETS_ALARM);
	    this.sendBroadcast(alarmIntent);
	    
	    new Handler().postDelayed(new Runnable() {
	        @Override public void run() {
	    	    mRefreshLayout.setRefreshing(false);
	        }
	    }, 5000);
	}
}
