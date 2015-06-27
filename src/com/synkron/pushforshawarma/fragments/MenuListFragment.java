package com.synkron.pushforshawarma.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.AdapterView.OnItemClickListener;

import com.synkron.pushforshawarma.*;
import com.synkron.pushforshawarma.adapters.MenusCursorAdapter;

import com.synkron.pushforshawarma.broadcastreceivers.OutletsUpdateAlarmReceiver;
import com.synkron.pushforshawarma.contentproviders.MenusContentProvider;
import com.synkron.pushforshawarma.contentproviders.OutletsContentProvider;
import com.synkron.pushforshawarma.utils.AppConstants;

public class MenuListFragment extends Fragment implements OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor>{
	
	private static final String TAG = "MenuListFragment";
    private ListView mMenusListView;
    private SwipeRefreshLayout mRefreshLayout;
    private MenusCursorAdapter _cursorAdapter;
    private String outletURI, outletCode;
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle bundle = getArguments();
    	outletURI = bundle.getString("OUTLET_URI");
    	outletCode = bundle.getString("OUTLET_CODE");
    	
    	Log.i(TAG, "MenuListFragment Instantiated with aruguments : "+ outletURI);
		
    	View rootView = inflater.inflate(R.layout.fragment_menu_list, container, false);
    	
    	mMenusListView = (ListView) rootView.findViewById(R.id.menu_list);
        mMenusListView.setTextFilterEnabled(true);
    	
    	mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
    	mRefreshLayout.setOnRefreshListener(this);
    	
    	mRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);
    	
    	//create an empty adapter we will use to display loaded data....
    	_cursorAdapter = new MenusCursorAdapter(getActivity(), null, CursorAdapter.NO_SELECTION);
    	
    	getLoaderManager().initLoader(0, null, this);
    	
    	//set listview adapter to a cursor adapter...
    	mMenusListView.setAdapter(_cursorAdapter);
    	
    	mMenusListView.setOnItemClickListener(new OnItemClickListener(){

    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			showMenuDetails(id);
    		}
    		
    	});
    	
        // Inflate the layout for this fragment
    	return rootView;
    }
	
	private void showMenuDetails(long id) {
		//fire implicit intent 
		Intent intent = new Intent(AppConstants.ACTION_VIEW_MENU_ITEM);
		intent.addCategory(Intent.CATEGORY_DEFAULT); 
	
		Log.i(TAG, "Intent sent with action :" + intent.getAction());
		
		PackageManager pm = getActivity().getPackageManager();
		ComponentName cn = intent.resolveActivity(pm);
		
		if(cn != null){
			startActivity(intent);
		}else{
			Log.w(TAG, "No Activity Found to Handle Acttion : " + intent.getAction());
		}
	}

	@Override
	public void onRefresh() {
		//fire stockUpdate Intent
	    Intent alarmIntent = new Intent(OutletsUpdateAlarmReceiver.ACTION_UPDATE_OUTLETS_ALARM);
	    getActivity().sendBroadcast(alarmIntent);
	    
	    new Handler().postDelayed(new Runnable() {
	        @Override public void run() {
	    	    mRefreshLayout.setRefreshing(false);
	        }
	    }, 5000);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		Log.i(TAG, "onCreateLoader running...");
		
		String[] projection = new String[]{
				MenusContentProvider.KEY_ID,
				MenusContentProvider.KEY_MENU_CODE,
				MenusContentProvider.KEY_MENU_NAME,
				MenusContentProvider.KEY_MENU_DESCRIPTION,
				MenusContentProvider.KEY_MENU_PRICE,
				MenusContentProvider.KEY_OUTLET_CODE			
		};
		
		Log.i(TAG, "requesting MenusContentProvider with URI : "+ 
				Uri.withAppendedPath(MenusContentProvider.CONTENT_URI, outletCode));
		
		CursorLoader loader = new CursorLoader(getActivity(), 
				Uri.withAppendedPath(MenusContentProvider.CONTENT_URI, outletCode), 
				projection,null,null, null);
		
		Log.i(TAG, "onCreateLoader complete");
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.i(TAG, "onLoadFinished called..");
		_cursorAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		Log.i(TAG, "onLoaderReset called..");
		_cursorAdapter.swapCursor(null);
		
	}
}
