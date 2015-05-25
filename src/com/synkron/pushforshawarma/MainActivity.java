package com.synkron.pushforshawarma;

import io.fabric.sdk.android.Fabric;

import com.facebook.AppEventsLogger;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity{
	
	public Location location;
	TextView mAboutText, txtLocation;
	Button btnOpenMap;
	private SocialLoginFragment socialLoginFragment;

	// Note: Your consumer key and secret should be obfuscated in your source code before shipping.
	private static final String TWITTER_KEY = "rGla1MHPutBZ1YKg9ZY0kYMeO";
	private static final String TWITTER_SECRET = "q6JKcgZSQ8tR22WNSH02NLkcE0xAahfHezoI6K8VXuk0ZBxEnr";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
		Fabric.with(this, new Twitter(authConfig));
		
		if(savedInstanceState == null){
			socialLoginFragment = new SocialLoginFragment();
			getSupportFragmentManager().beginTransaction()
								.add(android.R.id.content, socialLoginFragment)
								.commit();
		}else{
			socialLoginFragment = (SocialLoginFragment) getSupportFragmentManager()
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	 
	    // Pass the activity result to the fragment, which will
	    // then pass the result to the login button.
	    if (socialLoginFragment != null) {
	    	socialLoginFragment.onActivityResult(requestCode, resultCode, data);
	    }
	}
}
