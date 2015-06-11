package com.synkron.pushforshawarma.connectors;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.synkron.pushforshawarma.Outlet;
import com.synkron.pushforshawarma.callbacks.AsyncTaskCallback;
import com.synkron.pushforshawarma.contentproviders.OutletsContentProvider;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.*;

public class OutletConnector extends PushForShawarmaConnector{
	private static final String TAG = "OutletConnector";
	ProgressDialog _loadingDialog;
	private static String API_OUTLETS_REPOSITORY_ENDPOINT = "http://104.131.13.155/pfs/outlets/";
	private AsyncTaskCallback _asyncTaskCallback;
	private ArrayList<Outlet> mOutlets;
	
	
	//this constructor will be modified to take a gps filter object
	//that will get outlets only within the region specified in the filter....
	public OutletConnector(Context context){
		_context = context;
		mOutlets = new ArrayList<Outlet>();
	}

	public void setAsyncCallback(AsyncTaskCallback asyncTaskCallBack) {
		_asyncTaskCallback = asyncTaskCallBack;
	}
	
	protected void onPreExecute(){
		  super.onPreExecute();
          // Showing progress dialog
		  
		  _loadingDialog = new ProgressDialog(_context);
		  _loadingDialog.setMax(100);
		  _loadingDialog.setProgress(0);
		  _loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		  _loadingDialog.setTitle("fetching outlets in your area...");
		  _loadingDialog.show();
	}
	
	@Override
	protected String doInBackground(String... args) {
			
		Log.i(TAG, "Fetch Outlet Task Started");
		String _Result = "";
		String sb = "";
		
	     try {
	    	 if(super.isNetworkAvaialble()){
	             URL url = new URL(API_OUTLETS_REPOSITORY_ENDPOINT);
	             HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	             connection.setReadTimeout(10000);
	             connection.setConnectTimeout(15000);
	             connection.setRequestMethod("GET");
	             connection.setDoInput(true);
	             connection.connect();
	             
	             InputStream is = connection.getInputStream();
	             BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8") );
	             String data = null;
	             
	             while ((data = reader.readLine()) != null) {
	                 _Result += data;
	             }
	             
	             Log.d(TAG, _Result);
	             
	             try {
	 				JSONObject obj = new JSONObject(_Result);
	 				JSONArray jsonArray = obj.getJSONArray("outlets");
	 				
	 				for(int i = 0; i < jsonArray.length(); i++){
	 						JSONObject innerObj = jsonArray.getJSONObject(i);
	 						System.out.println(innerObj.getString("Name"));
	 						
	 						sb = sb + " "+ innerObj.getString("Name");
	 						
	 						String name = innerObj.getString("Name");
	 						
	 						//TODO: icon should be a web resource not an app resource..
	 						String icon = innerObj.getString("Icon");
	 						String longitude = innerObj.getString("Longitude");
	 						String latitude = innerObj.getString("Latitude");
	 						
	 						Outlet mOutlet = new Outlet(name, icon, longitude, latitude);
	 						mOutlets.add(mOutlet); 						
	 				}
	 				sb = sb.trim();
	 				sb = sb.toLowerCase(Locale.US);
						
					//TODO: refactor into a service...
					//save outlet to content provider....
 					for(Outlet item : mOutlets){
 						ContentValues values = new ContentValues();
 						values.put(OutletsContentProvider.KEY_OUTLET_NAME, item.getName());
 						values.put(OutletsContentProvider.KEY_OUTLET_ICON, item.getIcon());
 						values.put(OutletsContentProvider.KEY_OUTLET_LONGITUDE, item.getLongitude());
 						values.put(OutletsContentProvider.KEY_OUTLET_LATITUDE, item.getLatitude());
 						
 						//this operation should be happen as a transaction...
 						_context.getContentResolver().insert(OutletsContentProvider.CONTENT_URI, values);
 					}
 					_context.getContentResolver().notifyChange(OutletsContentProvider.CONTENT_URI, null);

	 			}catch (JSONException e) {
	 				Log.e(TAG, e.getMessage());
	 			}
	    	 }else{
	    		 //throw new IOException("No Network Connection");
	    		 return "";
	    	 }
	         
	     } catch (Exception e) {
	         Log.e(TAG, e.getMessage());
	     }
	     
	     return String.valueOf(mOutlets.size());
	}
	
    @Override
    protected void onPostExecute(String result) {
    	_loadingDialog.dismiss();
    	
    	Log.i(TAG, "Fetch Outlets Task Completed");
    	
    	if(!result.equals("")){
	    	_asyncTaskCallback.OnTaskDone(mOutlets);
    	}else{
    		Toast.makeText((Context) _context, "empty server response [do you have a valid data connection?]", Toast.LENGTH_SHORT).show();
    	}
    }

}
