package com.synkron.pushforshawarma.asynctasks;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.synkron.pushforshawarma.MapActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LocationUpdateTask extends AsyncTask<String, String, String>{

	Context _context;
	Location _location;
	String TAG = "LocationUpdateTask";
	MapActivity theActivity;
	
	public LocationUpdateTask(Context context, Location location){
		_context = context;
		_location = location;
		theActivity = (MapActivity) context;
	}
	
	@Override
	protected void onPreExecute(){
		//set the label to indicate location query in progress...
		theActivity.txtLocation.setText("Searching address...");
	}
	
	@Override
	protected String doInBackground(String... params) {
		String addressString = "No Address Information";
		
		if(_location != null){
			double lat = _location.getLatitude();
			double lng = _location.getLongitude();
			
			Geocoder geocoder = new Geocoder(_context, Locale.getDefault());		
			
			try{
				List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
				StringBuilder sb = new StringBuilder();
				if(addresses.size() > 0){
					Address address = addresses.get(0);
					sb.append(address.getAddressLine(0));
					sb.append(", "+address.getCountryName());
					addressString = sb.toString();
				}
			}catch(IOException ioe){
				Log.e(TAG, "updateWithNewLocation: Exception :" + ioe.getMessage());
				Toast.makeText(_context, "data services unavailable", Toast.LENGTH_SHORT).show();
			}
		}
		
		return addressString;
	}

	@Override
    protected void onPostExecute(String result) {
		//update location view label.....
		theActivity.txtLocation.setText(result);		
	}
}
