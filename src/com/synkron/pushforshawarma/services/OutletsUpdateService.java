package com.synkron.pushforshawarma.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.synkron.pushforshawarma.Outlet;
import com.synkron.pushforshawarma.OutletListingsActivity;
import com.synkron.pushforshawarma.broadcastreceivers.DeviceBootCompleteReceiver;
import com.synkron.pushforshawarma.broadcastreceivers.OutletsUpdateAlarmReceiver;
import com.synkron.pushforshawarma.connectors.PushForShawarmaConnector;
import com.synkron.pushforshawarma.contentproviders.OutletsContentProvider;

import com.synkron.pushforshawarma.R;
import android.app.AlarmManager;
import android.app.IntentService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

//The IntentService stops the service after all 
//start requests have been handled, so you never have to call stopSelf().

public class OutletsUpdateService extends IntentService{
	private static String TAG = "OutletsUpdateService";
	private PushForShawarmaConnector _connector;
	private Context context;
	private ArrayList<Outlet> mOutlets;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	
	public OutletsUpdateService(String name) {
		super(name);
	}

	public OutletsUpdateService() {
		super(TAG);
		mOutlets = new ArrayList<Outlet>();
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		Log.i(TAG, "Service Started..");
		
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		String ALARM_ACTION = OutletsUpdateAlarmReceiver.ACTION_UPDATE_OUTLETS_ALARM;
		
		//Initialise pending intent to be fired when alarm is triggered
		Intent intentToFire = new Intent(ALARM_ACTION);
		pendingIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		//configure inexact repeating alarm..
		Log.i(TAG,"onHandleIntent event triggered for "+ TAG);
		
		context = getApplicationContext();

		int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;

		//long triggerAtMillis = SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY;
		long triggerAtMillis = SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES;
		
		Log.i(TAG,"Outlets Update Alarm set to trigger at "+ triggerAtMillis);
		
		alarmManager.setInexactRepeating(alarmType, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
		
		ComponentName receiver = new ComponentName(context, DeviceBootCompleteReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
		
		updateOutletsDatabase();
		
		//UI Notification to show service ran at specified interval..
		notifyMe();
	}

	private void updateOutletsDatabase(){
		Log.i(TAG, "UpdateOutletsDatabase initiated..");
		
		String _Result = "";
		
		_connector = new PushForShawarmaConnector(context);
		
      	 if(_connector.isNetworkAvailable()){
    		try {
	             URL url = new URL(PushForShawarmaConnector.API_OUTLETS_REPOSITORY_ENDPOINT);
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
	 						
	 						String name = innerObj.getString("Name");
	 						
	 						//TODO: icon should be a web resource not an app resource..
	 						String icon = innerObj.getString("Icon");
	 						String longitude = innerObj.getString("Longitude");
	 						String latitude = innerObj.getString("Latitude");
	 						
	 						Outlet mOutlet = new Outlet(name, icon, longitude, latitude);
	 						mOutlets.add(mOutlet); 						
	 				}

 					if(!mOutlets.isEmpty()){
	 						for(Outlet item : mOutlets){
	 					
	 						ContentValues values = new ContentValues();
	 						values.put(OutletsContentProvider.KEY_OUTLET_NAME, item.getName());
	 						values.put(OutletsContentProvider.KEY_OUTLET_ICON, item.getIcon());
	 						values.put(OutletsContentProvider.KEY_OUTLET_LONGITUDE, item.getLongitude());
	 						values.put(OutletsContentProvider.KEY_OUTLET_LATITUDE, item.getLatitude());
	 						
	 						//this operation should be happen as a transaction...
	 						context.getContentResolver().insert(OutletsContentProvider.CONTENT_URI, values);
	 					}
 					}
 					
	    		}catch(Exception Ex){
	    			Log.e(TAG, "Exception: "+ Ex.getMessage());
	    		}
    		}catch(Exception Ex){
    			Log.e(TAG, "Exception: "+ Ex.getMessage());
    		}
      	 }
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		
		Log.i(TAG, "Android System Removing Outlet Update Service");
	}
	
	public void notifyMe(){
		Intent intent = new Intent(this, OutletListingsActivity.class);
		PendingIntent theIntent = PendingIntent.getActivity(this, 0, intent, 0);
		
		NotificationCompat.Builder builder = new  NotificationCompat.Builder(this);
		builder.setAutoCancel(true)
				.setTicker("Outlet Update Service")
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(theIntent)
				.setContentText("Outlet Update Service has been called..");

		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
		int NOTIFICATION_REF = 1;
		
		notificationManager.notify(NOTIFICATION_REF, builder.build());
	}
}
