package it.polito.mobilecourseproject.poliapp.profile;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import java.util.ArrayList;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.AsyncTaskWithoutProgressBar;
import it.polito.mobilecourseproject.poliapp.Connectivity;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.User;

public class ProfileEditLanguagesActivity extends AppCompatActivity {

    ArrayList<RelativeLayout> languageLayouts = new ArrayList<>();
    ArrayList<String> languages = new ArrayList<>();

    private ActionProcessButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_languages);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit your known languages");


        try {
            User thisUser = AccountManager.getCurrentUser();

            languages = thisUser.getLanguageSkillsAsList();

            for(String language: languages) {
                inflateLanguage(language);
            }



        } catch (Exception e) {

            //TODO: RIMUOVERE TOAST
            Toast.makeText(this, "DEBUG: Error while getting current user", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }


        saveButton = (ActionProcessButton) findViewById(R.id.saveButton);
        saveButton.setMode(ActionProcessButton.Mode.ENDLESS);

    }

    public void save(View v) {

        boolean error=false;

        findViewById(R.id.parentView).requestFocus();

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

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                saveButton.setProgress(1);
                saveButton.setEnabled(false);
            }

            @Override
            protected String doInBackground(Void... params) {
                String resultMessage = null;

                try {
                    User thisUser = AccountManager.getCurrentUser();

                    thisUser.setLanguageSkills("");

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



    public void inflateLanguage(final String language){

        final RelativeLayout languageLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.item_profile_language, null );
        languageLayouts.add(languageLayout);

        languageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removeLanguage(language, languageLayout);
                return true;
            }

        });

        TextView languageTextView = (TextView)((CardView)languageLayout.getChildAt(0)).getChildAt(0);
        languageTextView.setText(language);

        ((LinearLayout) findViewById(R.id.languagesContainer)).addView(languageLayout);

    }

    public void removeLanguage(String language, RelativeLayout languageLayout){
        languages.remove(language);
        languageLayout.setVisibility(View.GONE);
        languageLayouts.remove(languageLayout);
    }

    public void addNewLanguage(View v){
        
        String language = "Italian (Native proficiency)";
        languages.add(language);
        inflateLanguage(language);

    }


}
