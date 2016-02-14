package it.polito.mobilecourseproject.poliapp.jobs;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import it.polito.mobilecourseproject.poliapp.ExternalIntents;
import it.polito.mobilecourseproject.poliapp.MyUtils;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.JobOffer;
import it.polito.mobilecourseproject.poliapp.model.Notice;

public class JobOfferDetailActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private static final int PERCENTAGE_TO_SHOW_TITLE = 80;
    private boolean mIsTitleHidden=false;
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private JobOffer jobOffer;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joboffer_detail);

        mFab = findViewById(R.id.emailFab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.flexible_example_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.flexible_example_appbar);
        appbar.addOnOffsetChangedListener(this);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

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


                    (findViewById(R.id.website)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ExternalIntents.goToWebsite(JobOfferDetailActivity.this, jobOffer.getWebsite());
                        }
                    });

                }
            }
        });

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(i)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;
                ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
            }
        }

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_TITLE) {
            if (mIsTitleHidden) {
                mIsTitleHidden = false;
                if(jobOffer!=null) {
                    collapsingToolbarLayout.setTitle(jobOffer.getTitle());
                }

            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_TITLE) {
            if (!mIsTitleHidden) {
                mIsTitleHidden = true;
                collapsingToolbarLayout.setTitle("");
            }
        }

    }


    public void sendEmail(View view) {
        ExternalIntents.sendMail(this, jobOffer.getEmailAddress());
    }
}
