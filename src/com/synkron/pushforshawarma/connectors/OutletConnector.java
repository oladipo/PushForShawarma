package com.synkron.pushforshawarma.connectors;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class OutletConnector extends PushForShawarmaConnector{
	private static final String TAG = "OutletConnector";
	ProgressDialog _loadingDialog;
	String API_OUTLETS_REPOSITORY_ENDPOINT = "http://synkron.cloudapp.net/pfs/outlets/";
	
	public OutletConnector(Context context){
		_context = context;
	}
	
	protected void onPreExecute(){
		  super.onPreExecute();
          // Showing progress dialog
		  
		  _loadingDialog = new ProgressDialog(_context);
		  _loadingDialog.setMax(100);
		  _loadingDialog.setProgress(0);
		  _loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		  _loadingDialog.setTitle("fetching outlets...");
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
	 				}
	 				sb = sb.trim();
	 				sb = sb.toLowerCase(Locale.US);
	 				
	 				if(sb.equals("true")){
	 					//Save Customer Id to Persistent Store Instance..
	 				}
	 			}catch (JSONException e) {
	 				// TODO Auto-generated catch block
	 				Log.e(TAG, e.getMessage());
	 			}
	    	 }else{
	    		 //throw new IOException("No Network Connection");
	    	 }
	         
	     } catch (Exception e) {
	         Log.e(TAG, e.getMessage());
	     }
	     return sb;
	}
	
    @Override
    protected void onPostExecute(String result) {
    	_loadingDialog.dismiss();
    	
    	Log.i(TAG, "Fetch Outlets Task Completed");
    	
    	if(!result.equals("")){
	    	
    	}else{
    		Toast.makeText((Context) _context, "empty server response [do you have a valid data connection?]", Toast.LENGTH_SHORT).show();
    	}
    }
}
