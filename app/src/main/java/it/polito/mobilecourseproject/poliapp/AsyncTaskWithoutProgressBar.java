package it.polito.mobilecourseproject.poliapp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class AsyncTaskWithoutProgressBar extends AsyncTask<Void, String, String>{
	private ProgressDialog dialog;
	protected Activity activity;
	protected final String OK_VALUE="OK";
	protected final String KO_VALUE="KO";
	protected final String DONTCARE_VALUE="OK";
	private boolean blackBackground;

	public AsyncTaskWithoutProgressBar(Activity act) {
		this.activity=act;
		blackBackground=true;
	}
	public AsyncTaskWithoutProgressBar(Activity act, boolean blackBackground) {
		this.activity=act;
		this.blackBackground=blackBackground;
	}




    @Override
	protected void onPreExecute() { 
	}
	@Override
	protected String doInBackground(Void... params) { 
		String resultMessage=OK_VALUE;
		//override this method to perform background operation
		return resultMessage;
	} 
	@Override
	protected void onPostExecute(String resultMessage) {

        /*if(resultMessage!=null)
            DialogManager.toastMessage(resultMessage, activity);
		*/
	   //override this method to perform operation after that background operation finishes
		
 }

 }