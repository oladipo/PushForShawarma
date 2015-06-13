package com.synkron.pushforshawarma.broadcastreceivers;

import com.synkron.pushforshawarma.services.OutletsUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceBootCompleteReceiver extends BroadcastReceiver{
	private static String TAG = "DeviceBootCompleteReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
        	Log.i(TAG, "Boot Complete Intent Processing...");
        	
        	//start outlet update service..
    		Intent serviceIntent = new Intent(context, OutletsUpdateService.class);
    		context.startService(serviceIntent);
        }
	}

}
