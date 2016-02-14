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

public class ProfileEditAboutMeActivity extends AppCompatActivity {

    private EditText aboutMe;

    private ActionProcessButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_about_me);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit your information");

        aboutMe = (EditText)findViewById(R.id.aboutMe);

        try {
            User thisUser = AccountManager.getCurrentUser();

            if(thisUser.getDescription()!=null && !thisUser.getDescription().trim().isEmpty())
                aboutMe.setText(thisUser.getDescription());

        } catch (Exception e) {

            //TODO: RIMUOVERE TOAST
            Toast.makeText(this, "DEBUG: Error while getting current user", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }


        saveButton = (ActionProcessButton) findViewById(R.id.saveButton);
        saveButton.setMode(ActionProcessButton.Mode.ENDLESS);

        aboutMe.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                saveButton.setProgress(0);
                aboutMe.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.aboutMeWrapper)).setErrorEnabled(false);
            }
        });


    }

    public void save(View v) {

        boolean error=false;

        findViewById(R.id.parentView).requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(aboutMe.getWindowToken(), 0);


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

        if(aboutMe.getText().toString().length()>400) {
            ((TextInputLayout)findViewById(R.id.aboutMeWrapper)).setError("The description is too long!");
            error=true;
        }

        if(error){
            saveButton.setProgress(-1);
            return;
        }



        new AsyncTaskWithoutProgressBar(this) {

            String aboutMeText;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                saveButton.setProgress(1);
                saveButton.setEnabled(false);

                aboutMeText=aboutMe.getText().toString().trim();
            }

            @Override
            protected String doInBackground(Void... params) {
                String resultMessage = null;

                try {
                    User thisUser = AccountManager.getCurrentUser();

                    thisUser.setDescription(aboutMeText);

                    thisUser.save();
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
