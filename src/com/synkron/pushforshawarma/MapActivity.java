package com.synkron.pushforshawarma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.*;
import com.interswitchng.techquest.vervepayment.VervePayment;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.*;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class MapActivity extends ActionBarActivity {

	private GoogleMap googleMap;
	private HashMap<Marker, CustomMarker> mMarkersHashMap;
	private Location location;
	TextView txtLocation;
	Marker cameraMarker;
	Button btnBringShawarma;
	GroundOverlayOptions userPosition;
	UiSettings uiSettings;
	
	//hold a reference to all instances of sharwarma joints.
	private ArrayList<CustomMarker> mMarkersArray = new ArrayList<CustomMarker>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
		
		
		setContentView(R.layout.fragment_map);
		try{
			initializeMap();
			btnBringShawarma = (Button)findViewById(R.id.btnPush);
			
			if(btnBringShawarma != null){
				
				btnBringShawarma.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						//get current location coordinates
						
						//display order confirmation scrren showing selected address and confirm button
						
						//display option to pay with verve or pay on delivery
						
						//create order request via http api
						
						//display order delivery progress screen...
						
						//display order collected confirmation screen
						
						payWithVerve();
						
					}
					
				});
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
		// TODO Auto-generated method stub
		String status = "No Location Found";
		String addressString = "No Address Information";
		
		if(location != null){
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());		
			
			try{
				List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
				StringBuilder sb = new StringBuilder();
				if(addresses.size() > 0){
					Address address = addresses.get(0);
					
					/*for(int i=0; i < address.getMaxAddressLineIndex(); i++){
						sb.append(address.getAddressLine(i)).append("\n");
						sb.append(address.getCountryName());
					}*/
					sb.append(address.getAddressLine(0)).append("\n");
					sb.append(address.getCountryName());
					
					addressString = sb.toString();
				}
			}catch(IOException ioe){
				
			}
			txtLocation.setText("");
			txtLocation.setText("YOUR CURRENT ADDRESS: "+"\n"+addressString);
		}else{
			txtLocation.setText("");
			txtLocation.setText(status);
		}
	}

	@SuppressLint("NewApi") 
	private void initializeMap() {
		// TODO Auto-generated method stub
		if (googleMap == null){
			
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			float maxzoom = googleMap.getMaxZoomLevel();
			uiSettings = googleMap.getUiSettings();
			
			uiSettings.setTiltGesturesEnabled(false);
			
			//googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
			//googleMap.setMyLocationEnabled(true);
			
	        // Initialize the HashMap for Markers and MyMarker object
	        mMarkersHashMap = new HashMap<Marker, CustomMarker>();

	        mMarkersArray.add(new CustomMarker("01 Shawarma", "marker", Double.parseDouble("6.657284"), Double.parseDouble("3.323408")));
	        mMarkersArray.add(new CustomMarker("01 Shawarma", "marker", Double.parseDouble("6.612574"), Double.parseDouble("3.345402")));
	        
	        location = getCurrentLocation();
	        txtLocation = (TextView)findViewById(R.id.mViewLocation);
	        
	        updateWithNewLocation(location);
	        
	        if (location != null)
            {
	        	googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), maxzoom));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                						.target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
						                .zoom(maxzoom)
						                .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }

			plotMarkers(mMarkersArray);
			
			
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
					updateWithNewLocation(arg0);
				}
				
			});
			
			userPosition = new GroundOverlayOptions()
            					.image(BitmapDescriptorFactory.fromResource(R.drawable.pegman))
            					.position(new LatLng(location.getLatitude(), location.getLongitude()), 150f);
			
			googleMap.addGroundOverlay(userPosition);
			             
			googleMap.setOnCameraChangeListener(new OnCameraChangeListener(){

				@Override
				public void onCameraChange(CameraPosition position) {
					//remove the previous marker
		/*			if(cameraMarker != null)
						cameraMarker.remove();
					
					//add a custom marker on the current position
		            MarkerOptions markerOption = new MarkerOptions().position(position.target);
		            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.pegman));
		            
		            cameraMarker = googleMap.addMarker(markerOption);
		            */
		            updateWithNewLocation(location); 
					userPosition.position(position.target, 150f);
					
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		initializeMap();
	}
	
	//add all sharwarma joints to the map
	//the coordinates are retrieved via json from a web service....
	private void plotMarkers(ArrayList<CustomMarker> markers)
	{
	    if(markers.size() > 0)
	    {
	        for (CustomMarker myMarker : markers)
	        {

	            // Create user marker with custom icon and other options
	            MarkerOptions markerOption = new MarkerOptions().position(new LatLng(myMarker.getmLatitude(), myMarker.getmLongitude()));
	            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_new));
	            

	            Marker currentMarker = googleMap.addMarker(markerOption);
	            mMarkersHashMap.put(currentMarker, myMarker);

	            googleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
	        }
	    }
	}
	
	 private int manageMarkerIcon(String markerIcon){
	            return R.drawable.marker_new;
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

			return location = locationManager.getLastKnownLocation(provider);
	 }
}
