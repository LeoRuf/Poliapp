package it.polito.mobilecourseproject.poliapp.jobs;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.ExternalIntents;
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
    CollapsingToolbarLayout collapsingToolbarLayout;
    private User currentUser;

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
        }

        if(!addMode){
            ParseQuery.getQuery("JobOffer").fromLocalDatastore().getInBackground(getIntent().getStringExtra("jobOfferId"), new GetCallback<ParseObject>() {
                public void done(ParseObject jobOfferRetrieved, ParseException e) {
                    if (e == null) {

                        //TODO: Gestire meglio cosa succede mentre carica

                        jobOffer = (JobOffer)jobOfferRetrieved;

                        ((TextView)findViewById(R.id.title)).setText(jobOffer.getTitle());
                        ((TextView)findViewById(R.id.placeOfWork)).setText(jobOffer.getLocation());
                        ((TextView)findViewById(R.id.description)).setText(jobOffer.getDescription());
                        ((TextView)findViewById(R.id.responsibilities)).setText(jobOffer.getResponsibilities());
                        ((TextView)findViewById(R.id.prerequisites)).setText(jobOffer.getPrerequisites());
                        ((TextView)findViewById(R.id.employmentType)).setText(jobOffer.getEmploymentType());
                        ((TextView)findViewById(R.id.company)).setText(jobOffer.getCompany());
                        ((TextView)findViewById(R.id.companyDescription)).setText(jobOffer.getCompanyDescription());


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

                        } else {
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

                        } else {
                            (findViewById(R.id.website)).setVisibility(View.GONE);
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


        findViewById(R.id.saveButton).setVisibility(View.VISIBLE);
    }

    public void editTitle(){
        new MaterialDialog.Builder(this)
                .title("Edit job title")
                .inputRangeRes(1, 35, R.color.md_material_blue_800)
                .input(null, jobOffer.getTitle(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        if(input.length()>0){
                            ((TextView)findViewById(R.id.title_empty)).setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.title)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.title)).setText(input.toString());
                            jobOffer.setTitle(input.toString());
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),"Job title cannot be empty", Snackbar.LENGTH_LONG);
                        }

                    }
                }).show();
    }

    public void editPlaceOfWork(){
        new MaterialDialog.Builder(this)
                .title("Edit place of work")
                .inputRangeRes(1, 35, R.color.md_material_blue_800)
                .input(null, jobOffer.getLocation(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        if(input.length()>0){
                            ((TextView)findViewById(R.id.placeOfWork_empty)).setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.placeOfWork)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.placeOfWork)).setText(input.toString());
                            jobOffer.setLocation(input.toString());
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),"Place of work cannot be empty", Snackbar.LENGTH_LONG);
                        }

                    }
                }).show();
    }

    public void editEmploymentType(){
        new MaterialDialog.Builder(this)
                .title("Edit employment type")
                .inputRangeRes(1, 35, R.color.md_material_blue_800)
                .input(null, jobOffer.getEmploymentType(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        if(input.length()>0){
                            ((TextView)findViewById(R.id.employmentType_empty)).setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.employmentType)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.employmentType)).setText(input.toString());
                            jobOffer.setEmploymentType(input.toString());
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),"Employment type cannot be empty", Snackbar.LENGTH_LONG);
                        }

                    }
                }).show();
    }

    public void editWebsite(){
        new MaterialDialog.Builder(this)
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
                            jobOffer.setWebsite(null);
                        }

                    }
                }).show();
    }

    public void editEmail(){
        new MaterialDialog.Builder(this)
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
                            stringBuilder.setSpan(new StyleSpan(Typeface.NORMAL), 0, emailLabel.length()-1,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            stringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), emailLabel.length(),
                                    emailLabel.length() + email.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);



                            ((TextView)findViewById(R.id.emailLabel)).setText(stringBuilder);
                            jobOffer.setEmailAddress(input.toString());

                        } else {
                            ((TextView) findViewById(R.id.emailLabel_empty)).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.emailLabel)).setVisibility(View.GONE);
                            jobOffer.setEmailAddress(null);
                        }

                    }
                }).show();
    }


    public void save(View view){

    }


}
