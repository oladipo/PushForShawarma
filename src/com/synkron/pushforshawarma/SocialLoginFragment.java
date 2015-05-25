package com.synkron.pushforshawarma;

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
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;

import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


public class SocialLoginFragment extends Fragment implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener{
	TextView mAboutText, txtLocation;
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
	public static GoogleApiClient mGoogleApiClient;
	  
	private UiLifecycleHelper uiHelper;
	private static final String TAG = "SocialLoginFragment";
	private TwitterLoginButton twitterButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
		View view = inflater.inflate(R.layout.activity_main, container, false);
		
		mAboutText = (TextView) view.findViewById(R.id.strMessage);
		mAboutText.setText(Html.fromHtml(getString(R.string.welcomeMessage)));
		mAboutText.setMovementMethod(LinkMovementMethod.getInstance());
		
        mAboutText.setLinkTextColor(getResources().getColor(R.color.holo_light_blue));
     
		LoginButton facebookButton = (LoginButton) view.findViewById(R.id.btnFacebook);
		facebookButton.setFragment(this);
		facebookButton.setReadPermissions(Arrays.asList("user_likes", "user_status","public_profile"));
		
	    // Callback registration
		SignInButton btnGooglePlus = (SignInButton) view.findViewById(R.id.btnGooglePlus);
		btnGooglePlus.setOnClickListener(this);
		
		//twitter fabric stuff
		twitterButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
		twitterButton.setCallback(new Callback<TwitterSession>() {
			@Override
			public void success(Result<TwitterSession> result) {
				// Do something with result, which provides a TwitterSession for making API calls
				String twitterName = result.data.getUserName();
				double twitterID = result.data.getUserId();
				
				//store values in shared preferences...
				TwitterAuthClient authClient = new TwitterAuthClient();
				TwitterSession session = Twitter.getSessionManager().getActiveSession();
				
				//requires that twitter whitelist the app..app is yet to be whitelisted...
				/*authClient.requestEmail(session, new Callback() {
				    @Override
				    public void success(Result result) {
				        // Do something with the result, which provides
				        // the email address
				    	String email = result.toString();
				    }
				 
				    @Override
				    public void failure(TwitterException exception) {
				      // Do something on failure
				    	Toast.makeText(getActivity(), "Exception Occured: "+ exception.getMessage() , Toast.LENGTH_SHORT).show();
				    }
				}); */
				
				openMap();
			}

			@Override
			public void failure(TwitterException exception) {
				// Do something on failure
				Toast.makeText(getActivity(), "Exception Occured: "+ exception.getMessage() , Toast.LENGTH_SHORT).show();
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
	
	/*Facebook button callBack */
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        //navigate to map screen..
	        openMap();
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	    }
	}
	
	/*Facebook button callBack */
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	/**
	 * Logout From Facebook 
	 */
	public static void callFacebookLogout(Context context) {
	    Session session = Session.getActiveSession();
	    if (session != null) {

	        if (!session.isClosed()) {
	            session.closeAndClearTokenInformation();
	            //clear your preferences if saved
	        }
	    } else {

	        session = new Session(context);
	        Session.setActiveSession(session);
	        session.closeAndClearTokenInformation();
	            //clear your preferences if saved
	    }
	}
	@Override
	public void onResume() {
	    super.onResume();
	    //facebook stuff
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

	        if (!mGoogleApiClient.isConnected()) {
	          mGoogleApiClient.reconnect();
	     }
	   }
	   
	    twitterButton.onActivityResult(requestCode, resultCode, data);
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
	    Log.i(TAG, "onStart method called....");
	    
	    if(mGoogleApiClient.isConnected()){
	 	   // mGoogleApiClient.connect();	    	
	    	Log.i(TAG, "googleAPIClient already connected, opening map");
	    	openMap();
	    }
	    //check if there is a valid twitter session..
	    if(Twitter.getSessionManager().getActiveSession() != null){
	    	Log.i(TAG, "twitter SDK client already connected, opening map");
	    	openMap();
	    }
	}
	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
	    if (!mGoogleApiClient.isConnecting()) {
	        mSignInClicked = true;
	        mGoogleApiClient.connect();
	        
	        //resolveSignInError();
	    }
	}
	@Override
	public void onStop() {
	    super.onStop();
	
	   /* if (mGoogleApiClient.isConnected()) {
	      mGoogleApiClient.disconnect();
	      Log.i(TAG, "User disconnected");
	    }*/
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
		 
		        if (mSignInClicked  && result.hasResolution()) {
		            // The user has already clicked 'sign-in' so we attempt to
		            // resolve all
		            // errors until the user is signed in, or they cancel.
		        	try {
		                result.startResolutionForResult(getActivity(), RC_SIGN_IN);
		                mIntentInProgress = true;
		              } catch (SendIntentException e) {
		                // The intent was canceled before it was sent.  Return to the default
		                // state and attempt to connect to get an updated ConnectionResult.
		                mIntentInProgress = false;
		                mGoogleApiClient.connect();
		              }
		        	
		            //resolveSignInError();
		        }
		    }
	}
	
	public void onConnected(Bundle connectionHint) {
	    mSignInClicked = false;
	    //Toast.makeText(getActivity(), "User is connected!" , Toast.LENGTH_LONG).show();
	    Log.i(TAG, "Google API client connected");
	    openMap();
	}

	@Override
	public void onClick(View v) {
		 switch (v.getId()) {
	        case R.id.btnGooglePlus:
	            // Signin button clicked
	            signInWithGplus();
	            break;
	        default:
	        	
	        	break;
	     }
	}
}
