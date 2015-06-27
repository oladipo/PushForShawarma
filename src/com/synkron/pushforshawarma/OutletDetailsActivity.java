package com.synkron.pushforshawarma;

import com.synkron.pushforshawarma.contentproviders.OutletsContentProvider;
import com.synkron.pushforshawarma.fragments.MenuListFragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class OutletDetailsActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private static String TAG = "OutletDetailsActivity";
	private String outletURI, mOutletName, mOutletCode;
	private MenuListFragment menuListFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	     
		//Get the intent that started this activity
	    Intent intent = getIntent();
		outletURI = intent.getStringExtra("OUTLET_URI");
		
	    Log.i(TAG, "Intent received with URI : " + outletURI);
	    
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
		setContentView(R.layout.activity_outlet_details);
		
		getSupportLoaderManager().initLoader(0, null, this);
		
		 // Check that the activity is using the layout version with
         // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_menus) != null) {
        	
        	// However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            
            menuListFragment = new MenuListFragment();
            menuListFragment.setArguments(intent.getExtras());
            
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_menus , menuListFragment)
            	.commit();
        }
		
	}
	
	@Override 
	protected void onResume(){
		super.onResume();
		
		getSupportLoaderManager().restartLoader(0, null, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.outlet_details, menu);
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
		String[] projection = new String[]{
				OutletsContentProvider.KEY_ID,
				OutletsContentProvider.KEY_OUTLET_CODE,
				OutletsContentProvider.KEY_OUTLET_NAME
		};
		
		CursorLoader loader = new CursorLoader(this, Uri.parse(outletURI), projection,null,null, null);
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		if(cursor != null && cursor.moveToFirst()){
			mOutletCode = cursor.getString(1);
			mOutletName = cursor.getString(2);
			setTitle(mOutletName);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}
