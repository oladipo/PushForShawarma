package com.synkron.pushforshawarma;

import com.synkron.pushforshawarma.broadcastreceivers.OutletsUpdateAlarmReceiver;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class MainApplication extends Application{
	
	private static String TAG = "MainApplication";
	
	@Override
	public void onCreate(){
		super.onCreate();

		//fire broadcast intent to start alarm for outlets update service..
		Intent alarmIntent = new Intent(OutletsUpdateAlarmReceiver.ACTION_UPDATE_OUTLETS_ALARM);
		this.sendBroadcast(alarmIntent);
		
		Log.i(TAG, "Broadcast Intent with Action :[" + OutletsUpdateAlarmReceiver.ACTION_UPDATE_OUTLETS_ALARM +"] broadcasted");
	}
}
