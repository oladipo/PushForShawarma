package com.synkron.pushforshawarma.broadcastreceivers;

import com.synkron.pushforshawarma.services.OutletsUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OutletsUpdateAlarmReceiver extends BroadcastReceiver{
	private static String TAG = "OutletsUpdateAlarmReceiver";
	public static final String ACTION_UPDATE_OUTLETS_ALARM = "com.synkron.pushforshawarma.ACTION_UPDATE_OUTLETS_ALARM";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Broadcast Intent Received..");
		
		//Start Outlets Update Service..
		Intent serviceIntent = new Intent(context, OutletsUpdateService.class);
		context.startService(serviceIntent);
		
		Log.i(TAG, "Broadcast Intent Processed");
	}

}
