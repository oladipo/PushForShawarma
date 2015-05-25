package com.synkron.pushforshawarma;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import com.synkron.pushforshawarma.SocialLoginFragment;
import com.twitter.sdk.android.Twitter;

public class UserProfileActivity extends ActionBarActivity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener{

	private static final String TAG = "UserProfileActivity";
	Button mSignOut;
	GoogleApiClient mApiClient;
	int gSessionId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		mSignOut = (Button) findViewById(R.id.btnSignOut);
	    
		mSignOut.setOnClickListener(this);
		
		mApiClient = SocialLoginFragment.mGoogleApiClient;
		
//		mApiClient.connect();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_profile, menu);
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
	public void onClick(View v) {
		switch (v.getId()) {
	        case R.id.btnSignOut:
	        	//gSessionId = mApiClient.getSessionId();
	        	//sign out from google user profile and return to main login screen...
	        	
	        	if(mApiClient != null){
		        	if (mApiClient.isConnected()) {
		        	   // Prior to disconnecting, run clearDefaultAccount().
		        	      Plus.AccountApi.clearDefaultAccount(mApiClient);
		        	      Plus.AccountApi.revokeAccessAndDisconnect(mApiClient)
		        	          .setResultCallback(new ResultCallback<Status>() {
				        	        public void onResult(Status status) {
				        	          // mGoogleApiClient is now disconnected and access has been revoked.
				        	          // Trigger app logic to comply with the developer policies
				        	        	Log.i(TAG, "User Access Revoked!");
				        	        }
		        	      });
		        	      mApiClient.disconnect();
		        	     // mApiClient.connect()
		        	      gSessionId = mApiClient.getSessionId();
		        	      
		        	    }
	        	}
	        	//signout from facebook profile
      	      	SocialLoginFragment.callFacebookLogout(this);
      	      	
      	      	//signout from twitter session...
      	      CookieSyncManager.createInstance(this);
              CookieManager cookieManager = CookieManager.getInstance();
              cookieManager.removeSessionCookie();
              Twitter.getSessionManager().clearActiveSession();
              Twitter.logOut();
      	      	Intent intent = new Intent(this, MainActivity.class);
      	      	startActivity(intent);
      	      
	            break;
	        default:
	        	
	        	break;
        }
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "we are connected");
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
