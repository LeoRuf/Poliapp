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
import it.polito.mobilecourseproject.poliapp.model.JobExperience;
import it.polito.mobilecourseproject.poliapp.model.Language;
import it.polito.mobilecourseproject.poliapp.model.User;

public class ProfileEditJobExperiencesActivity extends AppCompatActivity {

    ArrayList<RelativeLayout> jobExperienceLayouts = new ArrayList<>();
    ArrayList<JobExperience> jobExperiences = new ArrayList<>();

    private ActionProcessButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_job_experiences);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit your job experiences");


        try {
            User thisUser = AccountManager.getCurrentUser();

            jobExperiences = thisUser.getJobExperiencesAsList();

            for(JobExperience jobExperience: jobExperiences) {
                inflateJobExperience(jobExperience);
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

        for(JobExperience jobExperience : jobExperiences) {
            if(jobExperience.getTitle()==null || jobExperience.getTitle().trim().isEmpty() ||
                    jobExperience.getCity()==null || jobExperience.getCity().trim().isEmpty() ||
                    jobExperience.getStartDate()==null || jobExperience.getStartDate().trim().isEmpty() ||
                    jobExperience.getEndDate()==null || jobExperience.getEndDate().trim().isEmpty() ||
                    jobExperience.getDescription()==null || jobExperience.getDescription().trim().isEmpty() ||
                    jobExperience.getOrganization()==null || jobExperience.getOrganization().trim().isEmpty()){
                error=true;
            }
        }

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
            Snackbar.make(findViewById(R.id.parentView), "Cannot save: There are some empty fields", Snackbar.LENGTH_LONG)
                    .show();

            saveButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                   saveButton.setProgress(0);
                }
            }, 3000);

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

                    for(JobExperience jobExperience: jobExperiences) {
                        finalStringInParse+=jobExperience.getTitle();
                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER_2;
                        finalStringInParse+=jobExperience.getOrganization();
                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER_2;
                        finalStringInParse+=jobExperience.getCity();
                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER_2;
                        finalStringInParse+=jobExperience.getStartDate();
                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER_2;
                        finalStringInParse+=jobExperience.getEndDate();
                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER_2;
                        finalStringInParse+=jobExperience.getDescription();

                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER;
                    }

                    thisUser.setJobExperiences(finalStringInParse);
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



    public void inflateJobExperience(final JobExperience jobExperience){

        //Qua faccio l'inflate... Su StackOverflow avevo trovato che bisognava passare qualcos'altro al posto di null, ma non serve a un cazzo °_°
        final RelativeLayout jobExperienceLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.item_profile_job_experience, null );
        jobExperienceLayouts.add(jobExperienceLayout);

        TextView titleTextView = (TextView)((LinearLayout)((CardView)jobExperienceLayout.getChildAt(0)).getChildAt(0)).getChildAt(0);
        TextView organizationLocationTextView = (TextView)((LinearLayout)((CardView)jobExperienceLayout.getChildAt(0)).getChildAt(0)).getChildAt(1);
        TextView dateTextView = (TextView)((LinearLayout)((CardView)jobExperienceLayout.getChildAt(0)).getChildAt(0)).getChildAt(2);
        TextView descriptionTextView = (TextView)((LinearLayout)((CardView)jobExperienceLayout.getChildAt(0)).getChildAt(0)).getChildAt(3);

        titleTextView.setText(jobExperience.getTitle());
        organizationLocationTextView.setText(jobExperience.getOrganization()+", "+jobExperience.getCity());
        dateTextView.setText(jobExperience.getStartDate()+" - "+jobExperience.getEndDate());
        descriptionTextView.setText(jobExperience.getDescription());

        ((LinearLayout) findViewById(R.id.jobsContainer)).addView(jobExperienceLayout);

        //Qua provo a settare l'onLongClickListener sul primo figlio (Che è una CardView)
        (jobExperienceLayout.getChildAt(0)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removeJobExperience(jobExperience, jobExperienceLayout);
                return true;
            }

        });

        (jobExperienceLayout.getChildAt(0)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog(jobExperience, jobExperienceLayout);
            }

        });

    }

    public void removeJobExperience(final JobExperience jobExperience, final RelativeLayout jobExperienceLayout){

        new MaterialDialog.Builder(this)
                .content("Do you want to delete this job experience?")
                .positiveText("DELETE")
                .negativeText("CANCEL")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        jobExperiences.remove(jobExperience);
                        jobExperienceLayout.setVisibility(View.GONE);
                        jobExperienceLayouts.remove(jobExperienceLayout);
                    }
                })
                .show();


    }

    public void showDialog(final JobExperience jobExperienceToEdit, final RelativeLayout jobExperienceLayout){

        String title="Add a job experience";
        String positiveButton="Add";

        if(jobExperienceToEdit!=null){

            title="Edit job experience";
            positiveButton="Edit";
        }

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(title)
                .customView(R.layout.dialog_profile_add_job_experience, true)
                .positiveText(positiveButton)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText titleEditText = (EditText) dialog.getCustomView().findViewById(R.id.titleEditText);
                        EditText organizationEditText = (EditText) dialog.getCustomView().findViewById(R.id.organizationEditText);
                        EditText locationEditText = (EditText) dialog.getCustomView().findViewById(R.id.locationEditText);
                        EditText startDateEditText = (EditText) dialog.getCustomView().findViewById(R.id.startDateEditText);
                        EditText endDateEditText = (EditText) dialog.getCustomView().findViewById(R.id.endDateEditText);
                        EditText descriptionEditText = (EditText) dialog.getCustomView().findViewById(R.id.descriptionEditText);

                        if(jobExperienceToEdit==null) {
                            JobExperience jobExperience = new JobExperience();
                            jobExperience.setTitle(titleEditText.getText().toString());
                            jobExperience.setOrganization(organizationEditText.getText().toString());
                            jobExperience.setCity(locationEditText.getText().toString());
                            jobExperience.setStartDate(startDateEditText.getText().toString());
                            jobExperience.setEndDate(endDateEditText.getText().toString());
                            jobExperience.setDescription(descriptionEditText.getText().toString());
                            jobExperiences.add(jobExperience);
                            inflateJobExperience(jobExperience);
                        } else {
                            jobExperienceToEdit.setTitle(titleEditText.getText().toString());
                            jobExperienceToEdit.setOrganization(organizationEditText.getText().toString());
                            jobExperienceToEdit.setCity(locationEditText.getText().toString());
                            jobExperienceToEdit.setStartDate(startDateEditText.getText().toString());
                            jobExperienceToEdit.setEndDate(endDateEditText.getText().toString());
                            jobExperienceToEdit.setDescription(descriptionEditText.getText().toString());

                            TextView titleTextView = (TextView)((LinearLayout)((CardView)jobExperienceLayout.getChildAt(0)).getChildAt(0)).getChildAt(0);
                            TextView organizationLocationTextView = (TextView)((LinearLayout)((CardView)jobExperienceLayout.getChildAt(0)).getChildAt(0)).getChildAt(1);
                            TextView dateTextView = (TextView)((LinearLayout)((CardView)jobExperienceLayout.getChildAt(0)).getChildAt(0)).getChildAt(2);
                            TextView descriptionTextView = (TextView)((LinearLayout)((CardView)jobExperienceLayout.getChildAt(0)).getChildAt(0)).getChildAt(3);

                            titleTextView.setText(jobExperienceToEdit.getTitle());
                            organizationLocationTextView.setText(jobExperienceToEdit.getOrganization()+", "+jobExperienceToEdit.getCity());
                            dateTextView.setText(jobExperienceToEdit.getStartDate()+" - "+jobExperienceToEdit.getEndDate());
                            descriptionTextView.setText(jobExperienceToEdit.getDescription());
                        }
                    }
                }).build();

        final MDButton positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        EditText titleEditText = (EditText) dialog.getCustomView().findViewById(R.id.titleEditText);
        EditText organizationEditText = (EditText) dialog.getCustomView().findViewById(R.id.organizationEditText);
        EditText locationEditText = (EditText) dialog.getCustomView().findViewById(R.id.locationEditText);
        EditText startDateEditText = (EditText) dialog.getCustomView().findViewById(R.id.startDateEditText);
        EditText endDateEditText = (EditText) dialog.getCustomView().findViewById(R.id.endDateEditText);
        EditText descriptionEditText = (EditText) dialog.getCustomView().findViewById(R.id.descriptionEditText);

        if(jobExperienceToEdit!=null) {
            titleEditText.setText(jobExperienceToEdit.getTitle());
            organizationEditText.setText(jobExperienceToEdit.getOrganization());
            locationEditText.setText(jobExperienceToEdit.getCity());
            startDateEditText.setText(jobExperienceToEdit.getStartDate());
            endDateEditText.setText(jobExperienceToEdit.getEndDate());
            descriptionEditText.setText(jobExperienceToEdit.getDescription());
        }

        //TODO: GESTIRE QUA
        titleEditText.addTextChangedListener(new TextWatcher() {
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
        if(jobExperienceToEdit==null) {

            positiveAction.setEnabled(false); // disabled by default
        }

    }

    public void addNewJobExperience(View v){
        showDialog(null, null);

    }


}
