package it.polito.mobilecourseproject.poliapp.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.AsyncTaskWithoutProgressBar;
import it.polito.mobilecourseproject.poliapp.Connectivity;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.User;

public class ProfileEditOtherActivity extends AppCompatActivity {

    private EditText website;
    private EditText linkedin;
    private EditText facebook;

    private ActionProcessButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_other);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit your information");

        website = (EditText)findViewById(R.id.website);
        linkedin = (EditText)findViewById(R.id.linkedin);
        facebook = (EditText)findViewById(R.id.fb);

        try {
            User thisUser = AccountManager.getCurrentUser();

            if(thisUser.getWebsite()!=null && !thisUser.getWebsite().trim().isEmpty())
                website.setText(thisUser.getWebsite());

            if(thisUser.getLinkedIn()!=null && !thisUser.getLinkedIn().trim().isEmpty())
                linkedin.setText(thisUser.getLinkedIn());

            if(thisUser.getFacebook()!=null && !thisUser.getFacebook().trim().isEmpty())
                facebook.setText(thisUser.getFacebook());

        } catch (Exception e) {

            //TODO: RIMUOVERE TOAST
            Toast.makeText(this, "DEBUG: Error while getting current user", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }


        saveButton = (ActionProcessButton) findViewById(R.id.saveButton);
        saveButton.setMode(ActionProcessButton.Mode.ENDLESS);

        linkedin.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                saveButton.setProgress(0);
                linkedin.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.linkedinWrapper)).setErrorEnabled(false);
            }
        });

        facebook.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                saveButton.setProgress(0);
                facebook.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.fbWrapper)).setErrorEnabled(false);
            }
        });

        website.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                saveButton.setProgress(0);
                website.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.websiteWrapper)).setErrorEnabled(false);
            }
        });

    }

    public void save(View v) {

        boolean error=false;

        findViewById(R.id.parentView).requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(website.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(linkedin.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(facebook.getWindowToken(), 0);

        if(!Connectivity.hasNetworkConnection(getApplicationContext())){
            Snackbar.make(findViewById(R.id.parentView), "No network connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            save(v);
                        }
                    })
                    .show();

            return;
        }

        if(error){
            saveButton.setProgress(-1);
            return;
        }


        new AsyncTaskWithoutProgressBar(this) {

            String websiteText;
            String fbText;
            String linkedinText;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                saveButton.setProgress(1);
                saveButton.setEnabled(false);

                websiteText=website.getText().toString().trim();
                fbText=facebook.getText().toString().trim();
                linkedinText=linkedin.getText().toString().trim();
            }

            @Override
            protected String doInBackground(Void... params) {
                String resultMessage = null;

                try {
                    User thisUser = AccountManager.getCurrentUser();

                    thisUser.setLinkedIn(linkedinText);
                    thisUser.setFacebook(fbText);
                    thisUser.setWebsite(websiteText);

                    AccountManager.getCurrentUser().save();
                } catch (Exception e) {
                    resultMessage = "Error occurred:\n" + e.getMessage();
                    e.printStackTrace();
                }
                return resultMessage;
            }

            @Override
            protected void onPostExecute(String resultMessage) {
                super.onPostExecute(resultMessage);
                if(resultMessage==null) {
                    saveButton.setProgress(100);
                    //activity.onBackPressed();
                    finish();
                    Snackbar.make(findViewById(R.id.parentView), "Changes saved!", Snackbar.LENGTH_LONG).show();

                } else {
                    saveButton.setProgress(-1);
                    Snackbar.make(findViewById(R.id.parentView), resultMessage, Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            save(v);
                        }
                    }).show();
                }
            }

        }.execute();

    }

}
