package com.synkron.pushforshawarma;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.*;
import com.interswitchng.techquest.vervepayment.VervePayment;
import com.synkron.pushforshawarma.TouchableWrapper.UpdateMapAfterUserInteraction;
import com.synkron.pushforshawarma.asynctasks.LocationUpdateTask;

import com.synkron.pushforshawarma.contentproviders.OutletsContentProvider;
import com.synkron.pushforshawarma.OutletListingsActivity;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.*;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class MapActivity extends ActionBarActivity implements UpdateMapAfterUserInteraction, LoaderManager.LoaderCallbacks<Cursor>
{

	private GoogleMap googleMap;
	private HashMap<Marker, CustomMarker> mMarkersHashMap;
	private Location location;
	public TextView txtLocation;
	Marker cameraMarker;
	Button btnBringShawarma, btnGoToMyLocation;
	GroundOverlayOptions userPosition;
	UiSettings uiSettings;
	private String TAG = "MapActivity";
	public TouchableWrapper mTouchableWrapper;
	LatLng mapCenter;
	
	//hold a reference to all instances of sharwarma joints.
	private ArrayList<CustomMarker> mMarkersArray = new ArrayList<CustomMarker>();
	
	private static final float CAMERA_ZOOM_LEVEL = 15L;
	//size of the area to get outlet information...
	private static final float USER_DATA_INTERVAL = 400L;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
		
		mTouchableWrapper = new TouchableWrapper(this);
		
		mTouchableWrapper.addView(getLayoutInflater().inflate(R.layout.fragment_map, null));
		
		setContentView(mTouchableWrapper);
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		try{
			initializeMap();
			btnBringShawarma = (Button)findViewById(R.id.btnPush);
			btnGoToMyLocation = (Button)findViewById(R.id.goToMyLocation);
			
			btnGoToMyLocation.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
				
					if(location != null){
						//set the center of the map to user's current location..
						Log.i(TAG, "Setting map to camera centre: Lat "+mapCenter.latitude+" Long: "+mapCenter.longitude);
						
						googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
							location.getLongitude()),CAMERA_ZOOM_LEVEL));
					
						Log.i(TAG, "camera zoom level set to :"+ CAMERA_ZOOM_LEVEL);
					
						updateWithNewLocation(location);
					}else{
						Toast.makeText(getApplicationContext(), "Location data not available, turn on location services to continue", Toast.LENGTH_SHORT).show();
					}
				}
			});
			if(btnBringShawarma != null){
				
				btnBringShawarma.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						//get current location coordinates
						showOutlets();
					}
					
				});
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void showOutlets(){
		Intent intent = new Intent(this, OutletListingsActivity.class);
		startActivity(intent);
	}
	@Override
	public void onResume(){
		super.onResume();
		initializeMap();
	}
	
	private void payWithVerve() {
		Intent intent = new Intent(this, VervePayment.class);
		
		intent.putExtra("clientId", "IKIA02D8BD536D77361591ED7369176CCD5513292C46");
		intent.putExtra("clientSecret", "SBQE0AHhHfE8JiMXMbnCXa9Dm565t090vt+sWBFq++0=");
		intent.putExtra("customerId", "TXN-01-0030");
		intent.putExtra("paymentCode", "0480852992");
		intent.putExtra("amount", "20000");
		intent.putExtra("isTestPayment", true);
		
		startActivityForResult(intent, 400);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK && requestCode == 400) {
	    // get data from intent
	        int txnStatus = data.getIntExtra("qtTransactionStatus", 0);
	                //txnStatus is either 0 if transaction failed or 1 if transaction was successful

	    }
	}
	private void updateWithNewLocation(Location location) {
		// run in background thread...
		LocationUpdateTask locationUpdateTask = new LocationUpdateTask(this, location);
		locationUpdateTask.execute();
	}

	@SuppressLint("NewApi") 
	private void initializeMap() {
		if (googleMap == null){
			
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			
			uiSettings = googleMap.getUiSettings();
			uiSettings.setTiltGesturesEnabled(false);
			uiSettings.setMyLocationButtonEnabled(false);
			//googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
			googleMap.setMyLocationEnabled(true);
			
			mapCenter = googleMap.getCameraPosition().target;
			
			txtLocation = (TextView)findViewById(R.id.mViewLocation);
			getSupportLoaderManager().initLoader(0, null, this);
			
	        // Initialize the HashMap for Markers and MyMarker object
	        mMarkersHashMap = new HashMap<Marker, CustomMarker>();
	        
	        location = googleMap.getMyLocation();
	        
	        if(location == null){
	        	//get last known location
		        location = getCurrentLocation();	        	
	        }
	        
	        if (location != null){
	        	
		        updateWithNewLocation(location);
		        
	        	googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), CAMERA_ZOOM_LEVEL));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                						.target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
						                .zoom(CAMERA_ZOOM_LEVEL)
						                .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }else{
            	Toast.makeText(getApplicationContext(), "location information unavailable", Toast.LENGTH_SHORT).show();
            	updateWithNewLocation(location);
            }

			googleMap.setOnMarkerClickListener(new OnMarkerClickListener(){

				@Override
				public boolean onMarkerClick(Marker marker) {
					marker.showInfoWindow();
					
					return true;
				}
			});
			
			googleMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener(){

				@Override
				public void onMyLocationChange(Location arg0) {
					location = arg0;
					
					//updateWithNewLocation(arg0);
				}
				
			});
			             
			googleMap.setOnCameraChangeListener(new OnCameraChangeListener(){

				@Override
				public void onCameraChange(CameraPosition position) {
		            //updateWithNewLocation(location); 
					
					//check if user has moved out the specified data retrieval 
					//distance interval for outlets.. location - USER_DATA_INTERVAL
					
					// get next outlet data chunk..
				}
				
			});
			if(googleMap == null){
				Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id) {
			//initialize intent to launch user profile activity..
		case R.id.action_user_profile:
			launchUserProfile();
			return true;
		default:
			return super.onOptionsItemSelected(item);	
		}
		
	}
	
	private void launchUserProfile() {
		try{
			Intent intent = new Intent(this , UserProfileActivity.class);
			startActivity(intent);
			
		}catch(Exception Ex){
			Log.d(TAG, Ex.getMessage());
			Toast toast = Toast.makeText(this, "Error Lauching User Profile :" + Ex.getMessage(), 
					Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private void plotMarkers(ArrayList<CustomMarker> markers)
	{
	    if(markers.size() > 0)
	    {
	        for (CustomMarker myMarker : markers)
	        {

	            // Create user marker with custom icon and other options
	            MarkerOptions markerOption = new MarkerOptions().flat(true)
	            		//.draggable(true)
	            		.position(new LatLng(myMarker.getmLatitude(), myMarker.getmLongitude()));
	            
	            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_new));
	            

	            Marker currentMarker = googleMap.addMarker(markerOption);
	            mMarkersHashMap.put(currentMarker, myMarker);

	            googleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
	        }
	    }
	}
	
	 private int manageMarkerIcon(String markerIcon){
	            return R.drawable.logo_01_shawarma;
	 }
	 
	 public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
		 
			public MarkerInfoWindowAdapter(){
		     
			}

		     @Override
		     public View getInfoWindow(Marker marker)
		     {
		         return null;
		     }

		     @Override
		     public View getInfoContents(Marker marker)
		     {
		         View v  = getLayoutInflater().inflate(R.layout.infowindow_layout, null);

		         CustomMarker myMarker = mMarkersHashMap.get(marker);

		         ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

		         TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);

		         markerIcon.setImageResource(manageMarkerIcon(myMarker.getmIcon()));

		         markerLabel.setText(myMarker.getmLabel());
		         
		         TextView descLabel = (TextView)v.findViewById(R.id.another_label);
		         descLabel.setText("Address: 67 Adeniyi Jones Ave, Ikeja Phone:0814 788 8746 Hours: Open today · 8:00 am – 6:00 pm");

		         return v;
		     }
		}

	 private Location getCurrentLocation(){
		 	LocationManager locationManager;
			String svcName = Context.LOCATION_SERVICE;
			
			locationManager = (LocationManager) getSystemService(svcName);
			
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false); 
			criteria.setSpeedRequired(false);
			criteria.setCostAllowed(true);
			
			String provider = locationManager.getBestProvider(criteria, true);
			
			if(!isGPSAvailable(locationManager)){
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		        dialog.setMessage("Location services not enabled");
		        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                //this will navigate user to the device location settings screen
		                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		                startActivity(intent);
		            }
		        });
		        
		        AlertDialog alert = dialog.create();
		        alert.show();
		        
		        location = null;
		        
			}else{
				location = locationManager.getLastKnownLocation(provider);
			}
			
			return location;
	 }

	@Override
	public void onUpdateMapAfterUserInteraction() {
		Log.i(TAG, "onUpdateMapAfterUserInteraction has been called.");
		goToMapCentre();
	}

	public void goToMapCentre(){
		
		if(googleMap != null){
			mapCenter = googleMap.getCameraPosition().target;
			
			Location theLocation = new Location("panLocation");
			theLocation.setLatitude(mapCenter.latitude);
			theLocation.setLongitude(mapCenter.longitude);
			
			updateWithNewLocation(theLocation);
			
		}else{
			
			Log.i(TAG, "onUpdateMapAfterUserInteraction: googleMap is null");
			updateWithNewLocation(location);
		}
	}
	private boolean isGPSAvailable(LocationManager locationManager ) {
	    
		Log.i(TAG, "Checking Location Services Availability with checkGPSStatus");
		
	    boolean gps_enabled = false;
	    boolean network_enabled = false;
	    
	    if( locationManager == null ) {
	        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    }
	    
	    try{
	    	gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	        
	    }catch (Exception ex){
	    	Log.e(TAG, "isGPSAvailable exception : "+ ex.getMessage());
	    }
	    
	    try{
	    	network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	    	
	    }catch (Exception ex){
	    	Log.e(TAG, "isGPSAvailable exception : "+ ex.getMessage());
	    }
	    
	    if (!gps_enabled && !network_enabled ){
	    	
	    	return false;
	    }
	    
	    return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Log.i(TAG, "onCreateLoader running...");
		
		String[] projection = new String[]{
				OutletsContentProvider.KEY_ID,
				OutletsContentProvider.KEY_OUTLET_ICON,
				OutletsContentProvider.KEY_OUTLET_NAME,
				OutletsContentProvider.KEY_OUTLET_LONGITUDE,
				OutletsContentProvider.KEY_OUTLET_LATITUDE
		};
		
		CursorLoader loader = new CursorLoader(this, OutletsContentProvider.CONTENT_URI, projection,null,null, null);
		
		Log.i(TAG, "onCreateLoader complete");
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.i(TAG, "onLoadFinished called..");
		
		int outletCount = 0;
		mMarkersArray.clear();
		
		//number of outlets in the database..
		outletCount = cursor.getCount();
		
		if(outletCount > 0){
			cursor.moveToFirst();
			
			for(int i = 0; i < outletCount ; i++){
				//draw markers on the map...
				Outlet outlet = new Outlet(cursor.getString(cursor.getColumnIndex(OutletsContentProvider.KEY_OUTLET_NAME)),
						cursor.getString(cursor.getColumnIndex(OutletsContentProvider.KEY_OUTLET_ICON)),
						cursor.getString(cursor.getColumnIndex(OutletsContentProvider.KEY_OUTLET_LATITUDE)),
						cursor.getString(cursor.getColumnIndex(OutletsContentProvider.KEY_OUTLET_LONGITUDE))
				);
				
				mMarkersArray.add(new CustomMarker(outlet.getName(), 
	        			outlet.getIcon(), 
	        			Double.parseDouble(outlet.getLatitude()), 
	        			Double.parseDouble(outlet.getLongitude()))
	        	);
				
				cursor.moveToNext();
			}
			
	        plotMarkers(mMarkersArray);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}
