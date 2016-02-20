package it.polito.mobilecourseproject.poliapp.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.dd.processbutton.iml.ActionProcessButton;

import java.util.ArrayList;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.AsyncTaskWithoutProgressBar;
import it.polito.mobilecourseproject.poliapp.Connectivity;
import it.polito.mobilecourseproject.poliapp.MyUtils;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.Language;
import it.polito.mobilecourseproject.poliapp.model.User;

public class ProfileEditLanguagesActivity extends AppCompatActivity {

    ArrayList<RelativeLayout> languageLayouts = new ArrayList<>();
    ArrayList<Language> languages = new ArrayList<>();

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

            for(Language language: languages) {
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

                    String finalStringInParse="";

                    for(Language language: languages) {
                        finalStringInParse+=language.getLanguage();

                        if(language.getLevel()!=null && !language.getLevel().trim().isEmpty()){
                            finalStringInParse+=MyUtils.CUSTOM_DELIMITER_2;
                            finalStringInParse+=language.getLevel();
                        }

                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER;
                    }

                    thisUser.setLanguageSkills(finalStringInParse);
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



    public void inflateLanguage(final Language language){

        //Qua faccio l'inflate... Su StackOverflow avevo trovato che bisognava passare qualcos'altro al posto di null, ma non serve a un cazzo °_°
        final RelativeLayout languageLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.item_profile_language, null );
        languageLayouts.add(languageLayout);

        TextView languageTextView = (TextView)((CardView)languageLayout.getChildAt(0)).getChildAt(0);
        if(language.getLevel()!=null && !language.getLevel().trim().isEmpty())
            languageTextView.setText(language.getLanguage()+" ("+language.getLevel()+")");
        else
            languageTextView.setText(language.getLanguage());


        ((LinearLayout) findViewById(R.id.languagesContainer)).addView(languageLayout);

        //Qua provo a settare l'onLongClickListener sul primo figlio (Che è una CardView)
        (languageLayout.getChildAt(0)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removeLanguage(language, languageLayout);
                return true;
            }

        });

        (languageLayout.getChildAt(0)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(language, languageLayout);
                return;
            }

        });

    }

    public void removeLanguage(Language language, RelativeLayout languageLayout){
        languages.remove(language);
        languageLayout.setVisibility(View.GONE);
        languageLayouts.remove(languageLayout);
    }

    public void showDialog(final Language languageToEdit, final RelativeLayout languageLayout){

        String title="Add a language";
        String positiveButton="Add";

        if(languageToEdit!=null){

            title="Edit language";
            positiveButton="Edit";
        }

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(title)
                .customView(R.layout.dialog_profile_add_language, true)
                .positiveText(positiveButton)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText languageEditText = (EditText) dialog.getCustomView().findViewById(R.id.languageEditText);
                        EditText levelEditText = (EditText) dialog.getCustomView().findViewById(R.id.levelEditText);

                        if(languageToEdit==null) {
                            Language language = new Language();
                            language.setLanguage(languageEditText.getText().toString());
                            language.setLevel(levelEditText.getText().toString());
                            languages.add(language);
                            inflateLanguage(language);
                        } else {
                            languageToEdit.setLanguage(languageEditText.getText().toString());
                            languageToEdit.setLevel(levelEditText.getText().toString());
                            TextView languageTextView = (TextView)((CardView)languageLayout.getChildAt(0)).getChildAt(0);
                            if(languageToEdit.getLevel()!=null && !languageToEdit.getLevel().trim().isEmpty())
                                languageTextView.setText(languageToEdit.getLanguage()+" ("+languageToEdit.getLevel()+")");
                            else
                                languageTextView.setText(languageToEdit.getLanguage());
                        }
                    }
                }).build();

        final MDButton positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        EditText languageEditText = (EditText) dialog.getCustomView().findViewById(R.id.languageEditText);
        EditText levelEditText = (EditText) dialog.getCustomView().findViewById(R.id.languageEditText);

        if(languageToEdit!=null) {
            languageEditText.setText(languageToEdit.getLanguage());
            levelEditText.setText(languageToEdit.getLanguage());

        }

        languageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default

    }

    public void addNewLanguage(View v){
        showDialog(null, null);

    }


}
