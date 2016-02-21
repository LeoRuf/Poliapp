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
import it.polito.mobilecourseproject.poliapp.model.Education;
import it.polito.mobilecourseproject.poliapp.model.User;

public class ProfileEditEducationsActivity extends AppCompatActivity {

    ArrayList<RelativeLayout> educationsLayouts = new ArrayList<>();
    ArrayList<Education> educations = new ArrayList<>();

    private ActionProcessButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_educations);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit education");


        try {
            User thisUser = AccountManager.getCurrentUser();

            educations = thisUser.getEducationsAsList();

            for(Education education: educations) {
                inflateEducation(education);
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

                    for(Education education: educations) {
                        finalStringInParse+=education.getTitle();
                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER_2;
                        finalStringInParse+=education.getUniversity();
                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER_2;
                        finalStringInParse+=education.getCity();
                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER_2;
                        finalStringInParse+=education.getStartDate();
                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER_2;
                        finalStringInParse+=education.getEndDate();
                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER_2;
                        finalStringInParse+=education.getFinalGrade();

                        finalStringInParse+=MyUtils.CUSTOM_DELIMITER;
                    }

                    thisUser.setEducations(finalStringInParse);
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



    public void inflateEducation(final Education education){

        final RelativeLayout educationLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.item_profile_education, null );
        educationsLayouts.add(educationLayout);

        TextView titleTextView = (TextView)((LinearLayout)((CardView)educationLayout.getChildAt(0)).getChildAt(0)).getChildAt(0);
        TextView universityLocationTextView = (TextView)((LinearLayout)((CardView)educationLayout.getChildAt(0)).getChildAt(0)).getChildAt(1);
        TextView dateTextView = (TextView)((LinearLayout)((CardView)educationLayout.getChildAt(0)).getChildAt(0)).getChildAt(2);
        TextView finalGradeTextView = (TextView)((LinearLayout)((CardView)educationLayout.getChildAt(0)).getChildAt(0)).getChildAt(3);

        titleTextView.setText(education.getTitle());
        universityLocationTextView.setText(education.getUniversity()+" ("+education.getCity()+")");
        dateTextView.setText("Dates attended: "+education.getStartDate()+" - "+education.getEndDate());
        finalGradeTextView.setText("Final grade: "+education.getFinalGrade());

        ((LinearLayout) findViewById(R.id.educationsContainer)).addView(educationLayout);

        //Qua provo a settare l'onLongClickListener sul primo figlio (Che è una CardView)
        (educationLayout.getChildAt(0)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removeEducation(education, educationLayout);
                return true;
            }

        });

        (educationLayout.getChildAt(0)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog(education, educationLayout);
            }

        });

    }

    public void removeEducation(final Education education, final RelativeLayout educationLayout){

        new MaterialDialog.Builder(this)
                .content("Do you want to delete this education?")
                .positiveText("DELETE")
                .negativeText("CANCEL")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        educations.remove(education);
                        educationLayout.setVisibility(View.GONE);
                        educationsLayouts.remove(educationLayout);
                    }
                })
                .show();
    }

    public void showDialog(final Education educationToEdit, final RelativeLayout educationLayout){

        String title="Add education";
        String positiveButton="Add";

        if(educationToEdit!=null){

            title="Edit education";
            positiveButton="Edit";
        }

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(title)
                .customView(R.layout.dialog_profile_add_education, true)
                .positiveText(positiveButton)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText titleEditText = (EditText) dialog.getCustomView().findViewById(R.id.titleEditText);
                        EditText universityEditText = (EditText) dialog.getCustomView().findViewById(R.id.universityEditText);
                        EditText locationEditText = (EditText) dialog.getCustomView().findViewById(R.id.locationEditText);
                        EditText startDateEditText = (EditText) dialog.getCustomView().findViewById(R.id.startDateEditText);
                        EditText endDateEditText = (EditText) dialog.getCustomView().findViewById(R.id.endDateEditText);
                        EditText finalGradeEditText = (EditText) dialog.getCustomView().findViewById(R.id.finalGradeEditText);

                        if(educationToEdit==null) {
                            Education education = new Education();
                            education.setTitle(titleEditText.getText().toString());
                            education.setUniversity(universityEditText.getText().toString());
                            education.setCity(locationEditText.getText().toString());
                            education.setStartDate(startDateEditText.getText().toString());
                            education.setEndDate(endDateEditText.getText().toString());
                            education.setFinalGrade(finalGradeEditText.getText().toString());
                            educations.add(education);
                            inflateEducation(education);
                        } else {
                            educationToEdit.setTitle(titleEditText.getText().toString());
                            educationToEdit.setUniversity(universityEditText.getText().toString());
                            educationToEdit.setCity(locationEditText.getText().toString());
                            educationToEdit.setStartDate(startDateEditText.getText().toString());
                            educationToEdit.setEndDate(endDateEditText.getText().toString());
                            educationToEdit.setFinalGrade(finalGradeEditText.getText().toString());


                            TextView titleTextView = (TextView)((LinearLayout)((CardView)educationLayout.getChildAt(0)).getChildAt(0)).getChildAt(0);
                            TextView universityLocationTextView = (TextView)((LinearLayout)((CardView)educationLayout.getChildAt(0)).getChildAt(0)).getChildAt(1);
                            TextView dateTextView = (TextView)((LinearLayout)((CardView)educationLayout.getChildAt(0)).getChildAt(0)).getChildAt(2);
                            TextView finalGradeTextView = (TextView)((LinearLayout)((CardView)educationLayout.getChildAt(0)).getChildAt(0)).getChildAt(3);

                            titleTextView.setText(educationToEdit.getTitle());
                            universityLocationTextView.setText(educationToEdit.getUniversity()+" ("+educationToEdit.getCity()+")");
                            dateTextView.setText("Dates attended: "+educationToEdit.getStartDate()+" - "+educationToEdit.getEndDate());
                            finalGradeTextView.setText("Final grade: "+educationToEdit.getFinalGrade());

                        }
                    }
                }).build();

        final MDButton positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        EditText titleEditText = (EditText) dialog.getCustomView().findViewById(R.id.titleEditText);
        EditText universityEditText = (EditText) dialog.getCustomView().findViewById(R.id.universityEditText);
        EditText locationEditText = (EditText) dialog.getCustomView().findViewById(R.id.locationEditText);
        EditText startDateEditText = (EditText) dialog.getCustomView().findViewById(R.id.startDateEditText);
        EditText endDateEditText = (EditText) dialog.getCustomView().findViewById(R.id.endDateEditText);
        EditText finalGradeEditText = (EditText) dialog.getCustomView().findViewById(R.id.finalGradeEditText);

        if(educationToEdit!=null) {
            titleEditText.setText(educationToEdit.getTitle());
            universityEditText.setText(educationToEdit.getUniversity());
            locationEditText.setText(educationToEdit.getCity());
            startDateEditText.setText(educationToEdit.getStartDate());
            endDateEditText.setText(educationToEdit.getEndDate());
            finalGradeEditText.setText(educationToEdit.getFinalGrade());
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
        if(educationToEdit==null) {

            positiveAction.setEnabled(false); // disabled by default
        }

    }

    public void addEducation(View v){
        showDialog(null, null);

    }


}
