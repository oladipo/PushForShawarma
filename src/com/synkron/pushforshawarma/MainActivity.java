package com.synkron.pushforshawarma;

import com.facebook.AppEventsLogger;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity{
	
	public Location location;
	TextView mAboutText, txtLocation;
	Button btnOpenMap;
	private FaceBookFragment faceBookFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState == null){
			faceBookFragment = new FaceBookFragment();
			getSupportFragmentManager().beginTransaction()
								.add(android.R.id.content, faceBookFragment)
								.commit();
		}else{
			faceBookFragment = (FaceBookFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
		}
		
		//setContentView(R.layout.activity_main);
		setTitle(R.string.app_name);	
				
	}
	
	public void startMap(){
		try{
			Intent intent = new Intent(this , MapActivity.class);
			startActivity(intent);
		}catch(Exception Ex){
			System.out.println(Ex.getMessage());
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	protected void onResume(){
		super.onResume();
		
		AppEventsLogger.activateApp(this);
	}
	@Override
	protected void onPause(){
		super.onPause();
		
		AppEventsLogger.deactivateApp(this);
	}
	
	
}
