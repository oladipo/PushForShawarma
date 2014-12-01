package com.synkron.pushforshawarma;

import io.fabric.sdk.android.Fabric;

import java.util.Arrays;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.plus.Plus;

import android.support.v4.app.*;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


public class FaceBookFragment extends Fragment implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener{
	TextView mAboutText, txtLocation;
	Button btnOpenMap;
	private static final int RC_SIGN_IN = 0;
	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;
	private boolean mIntentInProgress;
	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;	
	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;
	  
	private UiLifecycleHelper uiHelper;
	private static final String TAG = "MainFragment";
	private TwitterLoginButton loginButton;
	
	// Note: Your consumer key and secret should be obfuscated in your source code before shipping.
	private static final String TWITTER_KEY = "rGla1MHPutBZ1YKg9ZY0kYMeO";
	private static final String TWITTER_SECRET = "q6JKcgZSQ8tR22WNSH02NLkcE0xAahfHezoI6K8VXuk0ZBxEnr";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
		View view = inflater.inflate(R.layout.activity_main, container, false);
		
		final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
		Fabric.with(getActivity(), new Twitter(authConfig));
		
		
		mAboutText = (TextView) view.findViewById(R.id.strMessage);
		mAboutText.setText(Html.fromHtml(getString(R.string.welcomeMessage)));
		mAboutText.setMovementMethod(LinkMovementMethod.getInstance());
		
        mAboutText.setLinkTextColor(getResources().getColor(R.color.holo_light_blue));
     
		btnOpenMap = (Button)view.findViewById(R.id.button1);
		btnOpenMap.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
					openMap();
			}
		});  
		
		LoginButton authButton = (LoginButton) view.findViewById(R.id.btnFacebook);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("user_likes", "user_status","public_profile"));
		
		
		SignInButton btnGooglePlus = (SignInButton) view.findViewById(R.id.btnGooglePlus);
		btnGooglePlus.setOnClickListener(this);
		
		//twitter fabric stuff
		loginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
		loginButton.setCallback(new Callback<TwitterSession>() {
			@Override
			public void success(Result<TwitterSession> result) {
				// Do something with result, which provides a TwitterSession for making API calls
			}

			@Override
			public void failure(TwitterException exception) {
				// Do something on failure
			}
		});
		
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //facebook stuff
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    //google stuff
	    mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
						        .addConnectionCallbacks(this)
						        .addOnConnectionFailedListener(this)
						        .addApi(Plus.API)
						        //.addScope(Plus.SCOPE_PLUS_LOGIN)
						        .build();
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        //navigate to map screen..
	        openMap();
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	    }
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	public void onResume() {
	    super.onResume();
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }
	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	    
	    if (requestCode == RC_SIGN_IN) {
	    	 
	    	if (resultCode != getActivity().RESULT_OK) {
	    	      mSignInClicked = false;
	    	 }
	    	 
	        mIntentInProgress = false;

	        if (!mGoogleApiClient.isConnecting()) {
	          mGoogleApiClient.connect();
	     }
	   }
	   
	    loginButton.onActivityResult(requestCode, resultCode, data);
	}
	
	
	public void onConnectionSuspended(int cause) {
		  mGoogleApiClient.connect();
	}
	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}
	
	@Override
	public void onStart() {
	    super.onStart();
	    mGoogleApiClient.connect();
	}
	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
	    if (!mGoogleApiClient.isConnecting()) {
	        mSignInClicked = true;
	        resolveSignInError();
	    }
	}
	@Override
	public void onStop() {
	    super.onStop();
	
	    if (mGoogleApiClient.isConnected()) {
	      mGoogleApiClient.disconnect();
	    }
	  }
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	private void openMap() {
		MainActivity activity = (MainActivity)getActivity();
		activity.startMap();
	}
	
	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
	        try {
	            mIntentInProgress = true;
	            mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
	            
	        } catch (SendIntentException e) {
	            mIntentInProgress = false;
	            mGoogleApiClient.connect();
	        }catch(Exception ex){
	        	Log.e(TAG, ex.getMessage());
	        }
	    }
	}

	public void onConnectionFailed(ConnectionResult result) {
		 if (!result.hasResolution()) {
		        GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(),0).show();
		        return;
		    }
		 
		    if (!mIntentInProgress) {
		        // Store the ConnectionResult for later usage
		        mConnectionResult = result;
		 
		        if (mSignInClicked) {
		            // The user has already clicked 'sign-in' so we attempt to
		            // resolve all
		            // errors until the user is signed in, or they cancel.
		            resolveSignInError();
		        }
		    }
	}
	
	public void onConnected(Bundle connectionHint) {
	    mSignInClicked = false;
	    Toast.makeText(getActivity(), "User is connected!", Toast.LENGTH_LONG).show();
	    //openMap();
	}

	@Override
	public void onClick(View v) {
		 switch (v.getId()) {
	        case R.id.btnGooglePlus:
	            // Signin button clicked
	            signInWithGplus();
	            break;
	     }
	}
}
