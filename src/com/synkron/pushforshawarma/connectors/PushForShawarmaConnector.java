package com.synkron.pushforshawarma.connectors;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

public class PushForShawarmaConnector extends AsyncTask<String, String, String>{
	public static Context _context;
	public static String API_OUTLETS_REPOSITORY_ENDPOINT = "http://104.131.13.155/pfs/outlets/";
	
	public PushForShawarmaConnector(Context context){
		_context = context;
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isNetworkAvailable(){
		boolean connected = false;
		
		ConnectivityManager check = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
	      if (check != null) 
	      {
	         NetworkInfo[] info = check.getAllNetworkInfo();
	         if (info != null) 
	            for (int i = 0; i <info.length; i++) 
	            if (info[i].getState() == NetworkInfo.State.CONNECTED)
	            {
	               //Toast.makeText((Context) context, "Internet is connected",Toast.LENGTH_SHORT).show();
	            	connected =  true;
	            }
	         
	      }
	      else{
	         Toast.makeText((Context) _context, "failed to retrieve outlets, not connected to internet", Toast.LENGTH_SHORT).show();
	         connected = false;
	     }
		return connected;
	}

    protected void onProgressUpdate(String... progress) {
        // Set progress percentage
        //prgDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String file_url) {

    }
}
