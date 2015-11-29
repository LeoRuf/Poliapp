package it.polito.mobilecourseproject.poliapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Connectivity {


public static boolean hasNetworkConnection(Context ctx) {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	      }
	    
	    return haveConnectedWifi || haveConnectedMobile;
	}






	public  static void connectionError(final Activity activity){
		/*
		if(activity==null)return;
		Class activityClass=StudentMainActivity.class;

		String title="CONNECTION ERROR";
		String description="No connectivity";

		AlertDialog aDialog=null;
		final AlertDialog.Builder alertBuilder=new AlertDialog.Builder(activity);

		if(title!=null)alertBuilder.setTitle(title);
		alertBuilder.setMessage(description);
		alertBuilder.setCancelable(false);
		alertBuilder.setPositiveButton("RETRY", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(android.content.DialogInterface dialog, int which) {
				activity.recreate();
			}
		});
		final Class finalActivityClass = activityClass;
		alertBuilder.setNegativeButton("EXIT",new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(android.content.DialogInterface dialog, int which) {
				Intent i = new Intent(activity, finalActivityClass);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				i.putExtra("EXIT","EXIT");
				activity.startActivity(i);

			}});
		aDialog=alertBuilder.create();
		aDialog.show();
*/


	}

	
	
	
	
	
	
	
	
	
	
	
}
