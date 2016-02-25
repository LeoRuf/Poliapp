package it.polito.mobilecourseproject.poliapp.jobs;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.AsyncTaskWithoutProgressBar;
import it.polito.mobilecourseproject.poliapp.Connectivity;
import it.polito.mobilecourseproject.poliapp.ExternalIntents;
import it.polito.mobilecourseproject.poliapp.PoliApp;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.JobOffer;
import it.polito.mobilecourseproject.poliapp.model.User;

public class JobOfferDetailActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private static final int PERCENTAGE_TO_SHOW_TITLE = 80;
    private boolean isTitleHidden =false;
    private View fab;
    private int maxScrollSize;
    private boolean isImageHidden;
    private JobOffer jobOffer;
    private boolean addMode=false;
    private boolean editMode=false;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private User currentUser;
    private ActionProcessButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joboffer_detail);

        fab = findViewById(R.id.emailFab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.flexible_example_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.flexible_example_appbar);
        appbar.addOnOffsetChangedListener(this);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        try {
            currentUser = AccountManager.getCurrentUser();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(getIntent().hasExtra("addMode")) {
            addMode=true;
            jobOffer=new JobOffer();
            jobOffer.setUser(currentUser);
            jobOffer.setCompany(currentUser.getCompanyName());
        }


        if(addMode){
            ((TextView)findViewById(R.id.website)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            ((TextView)findViewById(R.id.website)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            PoliApp.getModel().getBitmapByUser(this, currentUser, new User.OnGetPhoto() {
                @Override
                public void onGetPhoto(Bitmap b) {
                    if (b == null) return;
                    ((ImageView) findViewById(R.id.imageLogoComp)).setImageBitmap(b);

                }
            });

        }


        if(!addMode){
            ParseQuery.getQuery("JobOffer").fromLocalDatastore().getInBackground(getIntent().getStringExtra("jobOfferId"), new GetCallback<ParseObject>() {
                public void done(ParseObject jobOfferRetrieved, ParseException e) {
                    if (e == null) {

                        //TODO: Gestire meglio cosa succede mentre carica


                        jobOffer = (JobOffer)jobOfferRetrieved;


                        PoliApp.getModel().getBitmapByUser(JobOfferDetailActivity.this, jobOffer.getPublisher(), new User.OnGetPhoto() {
                            @Override
                            public void onGetPhoto(Bitmap b) {
                                if (b == null) return;
                                ((ImageView) findViewById(R.id.imageLogoComp)).setImageBitmap(b);

                            }
                        });

                        ((TextView)findViewById(R.id.title)).setText(jobOffer.getTitle());
                        ((TextView)findViewById(R.id.placeOfWork)).setText(jobOffer.getLocation());
                        ((TextView)findViewById(R.id.description)).setText(jobOffer.getDescription());
                        ((TextView)findViewById(R.id.responsibilities)).setText(jobOffer.getResponsibilities());
                        ((TextView)findViewById(R.id.prerequisites)).setText(jobOffer.getPrerequisites());
                        ((TextView)findViewById(R.id.employmentType)).setText(jobOffer.getEmploymentType());
                        ((TextView)findViewById(R.id.company)).setText(jobOffer.getCompany());
                        ((TextView)findViewById(R.id.companyDescription)).setText(jobOffer.getCompanyDescription());



                        jobOffer.getPublisher().getPhotoAsync(JobOfferDetailActivity.this, new User.OnGetPhoto() {
                            @Override
                            public void onGetPhoto(Bitmap b) {
                                if(b!=null){
                                    ((ImageView)findViewById(R.id.imageLogoComp)).setImageBitmap(b);
                                }
                            }
                        });


                        if (jobOffer.getPublisher().getObjectId().equals(currentUser.getObjectId())) {
                            editMode=true;
                        }



                        if(editMode ){

                            ((TextView)findViewById(R.id.website)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }

                        if(jobOffer.getEmailAddress()!=null && !jobOffer.getEmailAddress().trim().isEmpty()) {
                            String emailLabel = "For any additional information, contact us on ";
                            String email = jobOffer.getEmailAddress();

                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(emailLabel + email);
                            stringBuilder.setSpan(new StyleSpan(Typeface.NORMAL), 0, emailLabel.length()-1,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            stringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), emailLabel.length(),
                                    emailLabel.length() + email.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);



                            ((TextView)findViewById(R.id.emailLabel)).setText(stringBuilder);
                            ((TextView)findViewById(R.id.emailLabel)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    sendEmail(view);
                                }
                            });

                        } else if(!editMode){
                            (findViewById(R.id.emailLabel)).setVisibility(View.GONE);
                            fab.setVisibility(View.GONE);
                        }

                        if(jobOffer.getWebsite()!=null && !jobOffer.getWebsite().trim().isEmpty()) {
                            (findViewById(R.id.website)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ExternalIntents.goToWebsite(JobOfferDetailActivity.this, jobOffer.getWebsite());
                                }
                            });

                        } else if(!editMode){
                            (findViewById(R.id.website)).setVisibility(View.GONE);
                        }

                        if(editMode) {
                            fab.setVisibility(View.GONE);
                            enableEdit();
                        }

                    }
                }
            });
        } else {

            ((TextView)findViewById(R.id.title)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.placeOfWork)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.description)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.responsibilities)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.prerequisites)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.employmentType)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.companyDescription)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.emailLabel)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.website)).setVisibility(View.GONE);
            fab.setVisibility(View.GONE);

            ((TextView)findViewById(R.id.title_empty)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.placeOfWork_empty)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.description_empty)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.responsibilities_empty)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.prerequisites_empty)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.employmentType_empty)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.companyDescription_empty)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.emailLabel_empty)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.website_empty)).setVisibility(View.VISIBLE);

            ((TextView)findViewById(R.id.company)).setText(currentUser.getCompanyName());

            enableEdit();
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (maxScrollSize == 0)
            maxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(i)) * 100
                / maxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!isImageHidden) {
                isImageHidden = true;
                ViewCompat.animate(fab).scaleY(0).scaleX(0).start();
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (isImageHidden) {
                isImageHidden = false;
                ViewCompat.animate(fab).scaleY(1).scaleX(1).start();
            }
        }

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_TITLE) {
            if (isTitleHidden) {
                isTitleHidden = false;
                if(jobOffer!=null) {
                    collapsingToolbarLayout.setTitle(jobOffer.getTitle());
                }

            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_TITLE) {
            if (!isTitleHidden) {
                isTitleHidden = true;
                collapsingToolbarLayout.setTitle("");
            }
        }

    }


    public void sendEmail(View view) {
        ExternalIntents.sendMail(this, jobOffer.getEmailAddress(), "Contact the employer");
    }

    public void enableEdit(){

        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true);

        findViewById(R.id.title_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTitle();
            }
        });

        findViewById(R.id.placeOfWork_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPlaceOfWork();
            }
        });

        findViewById(R.id.employmentType_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEmploymentType();
            }
        });

        findViewById(R.id.emailLabel_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEmail();
            }
        });

        findViewById(R.id.website_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editWebsite();
            }
        });

        findViewById(R.id.prerequisites_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPrerequisites();
            }
        });

        findViewById(R.id.responsibilities_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editResponsibilities();
            }
        });

        findViewById(R.id.companyDescription_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCompanyDescription();
            }
        });

        findViewById(R.id.description_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDescription();
            }
        });


        findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTitle();
            }
        });

        findViewById(R.id.placeOfWork).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPlaceOfWork();
            }
        });

        findViewById(R.id.employmentType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEmploymentType();
            }
        });

        findViewById(R.id.emailLabel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEmail();
            }
        });

        findViewById(R.id.website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editWebsite();
            }
        });

        findViewById(R.id.prerequisites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPrerequisites();
            }
        });

        findViewById(R.id.responsibilities).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editResponsibilities();
            }
        });

        findViewById(R.id.companyDescription).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCompanyDescription();
            }
        });

        findViewById(R.id.description).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDescription();
            }
        });



        findViewById(R.id.title).setBackgroundResource(outValue.resourceId);

        findViewById(R.id.placeOfWork).setBackgroundResource(outValue.resourceId);

        findViewById(R.id.employmentType).setBackgroundResource(outValue.resourceId);

        findViewById(R.id.prerequisites).setBackgroundResource(outValue.resourceId);

        findViewById(R.id.responsibilities).setBackgroundResource(outValue.resourceId);

        findViewById(R.id.companyDescription).setBackgroundResource(outValue.resourceId);

        findViewById(R.id.description).setBackgroundResource(outValue.resourceId);


        findViewById(R.id.saveButton).setVisibility(View.VISIBLE);
    }

     MaterialDialog dTitle;
    public void editTitle(){
        dTitle=new MaterialDialog.Builder(this)
                .title("Edit job title")
                .inputRange(1, 35)
                .input(null, jobOffer.getTitle(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        if(input.length()>0){
                            ((TextView)findViewById(R.id.title_empty)).setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.title)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.title)).setText(input.toString());
                            jobOffer.setTitle(input.toString());
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),"Job title cannot be empty", Snackbar.LENGTH_LONG).show();
                        }



                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }
                }).onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View view = dTitle.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }).show();
    }

   
    public void editResponsibilities(){

        String positiveButtonText = "Edit";

        if(jobOffer.getResponsibilities()==null)
            positiveButtonText="Add";

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Edit responsibilities")
                .customView(R.layout.dialog_company_edit_responsibilities, true)
                .positiveText(positiveButtonText)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        EditText responsibilitiesEditText = (EditText) dialog.getCustomView().findViewById(R.id.responsibilitiesEditText);

                        if(responsibilitiesEditText.getText()!=null && !responsibilitiesEditText.getText().toString().isEmpty()){
                            ((TextView)findViewById(R.id.responsibilities_empty)).setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.responsibilities)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.responsibilities)).setText(responsibilitiesEditText.getText().toString());
                            jobOffer.setResponsibilities(responsibilitiesEditText.getText().toString());
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),"Responsibilities cannot be empty", Snackbar.LENGTH_LONG).show();
                        }
                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }
                }).onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }).build();
            dTitle=dialog;

        final MDButton positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        EditText responsibilitiesEditText = (EditText) dialog.getCustomView().findViewById(R.id.responsibilitiesEditText);

        if(jobOffer.getResponsibilities()!=null ){
            responsibilitiesEditText.setText(jobOffer.getResponsibilities());
        }

        responsibilitiesEditText.addTextChangedListener(new TextWatcher() {
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

        if(jobOffer.getResponsibilities()==null || jobOffer.getResponsibilities().isEmpty()) {

            positiveAction.setEnabled(false); // disabled by default
        }


    }

    public void editCompanyDescription(){

        String positiveButtonText = "Edit";

        if(jobOffer.getCompanyDescription()==null)
            positiveButtonText="Add";

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Edit company description")
                .customView(R.layout.dialog_company_edit_company_description, true)
                .positiveText(positiveButtonText)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        EditText companyDescriptionEditText = (EditText) dialog.getCustomView().findViewById(R.id.companyDescriptionEditText);

                        if(companyDescriptionEditText.getText()!=null && !companyDescriptionEditText.getText().toString().isEmpty()){
                            ((TextView)findViewById(R.id.companyDescription_empty)).setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.companyDescription)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.companyDescription)).setText(companyDescriptionEditText.getText().toString());
                            jobOffer.setCompanyDescription(companyDescriptionEditText.getText().toString());
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),"Company description cannot be empty", Snackbar.LENGTH_LONG).show();
                        }
                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }
                }).onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }).build();
        dTitle=dialog;

        final MDButton positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        EditText companyDescriptionEditText = (EditText) dialog.getCustomView().findViewById(R.id.companyDescriptionEditText);

        if(jobOffer.getCompanyDescription()!=null ){
            companyDescriptionEditText.setText(jobOffer.getCompanyDescription());
        }

        companyDescriptionEditText.addTextChangedListener(new TextWatcher() {
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

        if(jobOffer.getCompanyDescription()==null || jobOffer.getCompanyDescription().isEmpty()) {

            positiveAction.setEnabled(false); // disabled by default
        }


    }

    public void editDescription(){

        String positiveButtonText = "Edit";

        if(jobOffer.getDescription()==null)
            positiveButtonText="Add";

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Edit  description")
                .customView(R.layout.dialog_company_edit_description, true)
                .positiveText(positiveButtonText)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        EditText descriptionEditText = (EditText) dialog.getCustomView().findViewById(R.id.descriptionEditText);

                        if(descriptionEditText.getText()!=null && !descriptionEditText.getText().toString().isEmpty()){
                            ((TextView)findViewById(R.id.description_empty)).setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.description)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.description)).setText(descriptionEditText.getText().toString());
                            jobOffer.setDescription(descriptionEditText.getText().toString());
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),"Description cannot be empty", Snackbar.LENGTH_LONG).show();
                        }
                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }
                }).onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }).build();
        dTitle=dialog;

        final MDButton positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        EditText descriptionEditText = (EditText) dialog.getCustomView().findViewById(R.id.descriptionEditText);

        if(jobOffer.getDescription()!=null ){
            descriptionEditText.setText(jobOffer.getDescription());
        }

        descriptionEditText.addTextChangedListener(new TextWatcher() {
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

        if(jobOffer.getDescription()==null || jobOffer.getDescription().isEmpty()) {

            positiveAction.setEnabled(false); // disabled by default
        }


    }

    public void editPrerequisites(){

        String positiveButtonText = "Edit";

        if(jobOffer.getPrerequisites()==null)
            positiveButtonText="Add";

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Edit prerequisites")
                .customView(R.layout.dialog_company_edit_prerequisites, true)
                .positiveText(positiveButtonText)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        EditText prerequisitesEditText = (EditText) dialog.getCustomView().findViewById(R.id.prerequisitesEditText);

                        if(prerequisitesEditText.getText()!=null && !prerequisitesEditText.getText().toString().isEmpty()){
                            ((TextView)findViewById(R.id.prerequisites_empty)).setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.prerequisites)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.prerequisites)).setText(prerequisitesEditText.getText().toString());
                            jobOffer.setPrerequisites(prerequisitesEditText.getText().toString());
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),"Prerequisites cannot be empty", Snackbar.LENGTH_LONG).show();
                        }
                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }
                }).onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }).build();
        dTitle=dialog;

        final MDButton positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        EditText prerequisitesEditText = (EditText) dialog.getCustomView().findViewById(R.id.prerequisitesEditText);

        if(jobOffer.getPrerequisites()!=null ){
            prerequisitesEditText.setText(jobOffer.getPrerequisites());
        }

        prerequisitesEditText.addTextChangedListener(new TextWatcher() {
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

        if(jobOffer.getPrerequisites()==null || jobOffer.getPrerequisites().isEmpty()) {

            positiveAction.setEnabled(false); // disabled by default
        }


    }

    public void editPlaceOfWork(){
        dTitle=new MaterialDialog.Builder(this)
                .title("Edit place of work")
                .inputRange(1, 35)
                .input(null, jobOffer.getLocation(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        if(input.length()>0){
                            ((TextView)findViewById(R.id.placeOfWork_empty)).setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.placeOfWork)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.placeOfWork)).setText(input.toString());
                            jobOffer.setLocation(input.toString());
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),"Place of work cannot be empty", Snackbar.LENGTH_LONG).show();
                        }
                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }
                }).onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View view = dTitle.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }).show();

    }

    public void editEmploymentType(){
        dTitle=new MaterialDialog.Builder(this)
                .title("Edit employment type")
                .inputRange(1, 35)
                .input(null, jobOffer.getEmploymentType(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        if(input.length()>0){
                            ((TextView)findViewById(R.id.employmentType_empty)).setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.employmentType)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.employmentType)).setText(input.toString());
                            jobOffer.setEmploymentType(input.toString());
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),"Employment type cannot be empty", Snackbar.LENGTH_LONG).show();
                        }
                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }
                }).onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View view = dTitle.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }).show();
    }

    public void editWebsite(){
       dTitle= new MaterialDialog.Builder(this)
                .title("Edit website")
                .input(null, jobOffer.getWebsite(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        if(input.length()>0) {

                            //TODO: Cambiare...

                            ((TextView) findViewById(R.id.website_empty)).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.website)).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.website)).setText(input.toString());
                            jobOffer.setWebsite(input.toString());
                        } else {
                            ((TextView) findViewById(R.id.website_empty)).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.website)).setVisibility(View.GONE);
                            jobOffer.setWebsite("");
                        }

                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }
                }).onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View view = dTitle.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }).show();
    }

    public void editEmail(){
        dTitle=new MaterialDialog.Builder(this)
                .title("Edit email address")
                .input(null, jobOffer.getEmailAddress(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        if(input.length()>0) {
                            //TODO: Cambiare...

                            ((TextView) findViewById(R.id.emailLabel_empty)).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.emailLabel)).setVisibility(View.VISIBLE);
                            String emailLabel = "For any additional information, contact us on ";
                            String email = input.toString();

                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(emailLabel + email);
                            stringBuilder.setSpan(new StyleSpan(Typeface.NORMAL), 0, emailLabel.length() - 1,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            stringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), emailLabel.length(),
                                    emailLabel.length() + email.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);



                            ((TextView)findViewById(R.id.emailLabel)).setText(stringBuilder);
                            jobOffer.setEmailAddress(input.toString());

                        } else {
                            ((TextView) findViewById(R.id.emailLabel_empty)).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.emailLabel)).setVisibility(View.GONE);
                            jobOffer.setEmailAddress("");
                            jobOffer.setEmailAddress("");
                        }
                        View view = dTitle.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }
                }).onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                View view = dTitle.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }).show();
    }


    public void save(View view){

        boolean error=false;

        if(!Connectivity.hasNetworkConnection(getApplicationContext())){
            Snackbar sb=Snackbar.make(findViewById(R.id.coordinatorLayout), "No network connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            save(v);
                        }
                    });
            ((TextView)((ViewGroup)sb.getView()).findViewById(android.support.design.R.id.snackbar_text)).setBackgroundColor(0);
            sb.show();


            return;
        }

        if(jobOffer.getTitle()==null || jobOffer.getTitle().trim().equals("") ||
                jobOffer.getLocation()==null || jobOffer.getLocation().trim().equals("") ||
                jobOffer.getDescription()==null || jobOffer.getDescription().trim().equals("") ||
                jobOffer.getResponsibilities()==null || jobOffer.getResponsibilities().trim().equals("") ||
                jobOffer.getPrerequisites()==null || jobOffer.getPrerequisites().trim().equals("") ||
                jobOffer.getEmploymentType()==null || jobOffer.getEmploymentType().trim().equals("") ||
                jobOffer.getCompany()==null || jobOffer.getCompany().trim().equals("") ||
                jobOffer.getCompanyDescription()==null || jobOffer.getCompanyDescription().trim().equals("")
                ){
            Snackbar.make(findViewById(R.id.coordinatorLayout), "All fields are mandatory!", Snackbar.LENGTH_LONG).show();
            error=true;
        }

        if(((jobOffer.getEmailAddress()==null || jobOffer.getEmailAddress().trim().equals("")) &&
                (jobOffer.getWebsite()==null || jobOffer.getWebsite().trim().equals("")))){
            Snackbar.make(findViewById(R.id.coordinatorLayout), "Specify at least one between email and website!", Snackbar.LENGTH_LONG).show();
            error=true;
        }

        if(error){
            saveButton.setProgress(-1);
            saveButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                    saveButton.setProgress(0);
                }
            }, 2000);
            return;
        }


        new AsyncTaskWithoutProgressBar(this) {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                saveButton.setProgress(1);
                saveButton.setEnabled(false);
                JobOfferDetailActivity.this.findViewById(R.id.overlay).setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                String resultMessage = null;

                try {

                    jobOffer.save();

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
                    onBackPressed(); //TODO: Controllare se l'activity quando si torna indietro

                } else {
                    saveButton.setProgress(-1);
                    saveButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           saveButton.setProgress(0);
                        }
                    }, 2000);
                    Snackbar sb=Snackbar.make(findViewById(R.id.coordinatorLayout), "An error occurred", Snackbar.LENGTH_LONG);
                    ((TextView)((ViewGroup)sb.getView()).findViewById(android.support.design.R.id.snackbar_text)).setBackgroundColor(0);
                    sb.show();
                    JobOfferDetailActivity.this.findViewById(R.id.overlay).setVisibility(View.GONE);
                }
            }

        }.execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        saveButton = (ActionProcessButton) findViewById(R.id.saveButton);
    }




}
