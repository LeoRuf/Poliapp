package it.polito.mobilecourseproject.poliapp.profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.ExternalIntents;
import it.polito.mobilecourseproject.poliapp.PoliApp;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.login.SignUpActivity;
import it.polito.mobilecourseproject.poliapp.model.Education;
import it.polito.mobilecourseproject.poliapp.model.JobExperience;
import it.polito.mobilecourseproject.poliapp.model.Language;
import it.polito.mobilecourseproject.poliapp.model.User;
import it.polito.mobilecourseproject.poliapp.utils.imagezoomcrop.GOTOConstants;
import it.polito.mobilecourseproject.poliapp.utils.imagezoomcrop.ImageCropActivity;

public class ProfileActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private Bitmap bitmap;
    private CircleImageView imageView;
    private User user;

    private String[] picMode = {GOTOConstants.PicModes.CAMERA,GOTOConstants.PicModes.GALLERY};
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;

    private ImageView mProfileImage;
    private int mMaxScrollSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.materialup_appbar);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.materialup_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        user = new User(); //SENNO' NON COMPILA XD

        imageView = (CircleImageView) findViewById(R.id.profile_image);

        if(getIntent().hasExtra("userId")){

            //TODO: GESTIRE caricamento user!!


            findViewById(R.id.editAboutMe).setVisibility(View.GONE);
            findViewById(R.id.editBasicInfo).setVisibility(View.GONE);
            findViewById(R.id.editLanguages).setVisibility(View.GONE);
            findViewById(R.id.editSkills).setVisibility(View.GONE);
            findViewById(R.id.editEducation).setVisibility(View.GONE);
            findViewById(R.id.editJobExperiences).setVisibility(View.GONE);
            findViewById(R.id.editLinks).setVisibility(View.GONE);


        } else {
            try {
                user = AccountManager.getCurrentUser();

                findViewById(R.id.professionalHeadline).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(ProfileActivity.this,ProfileEditProfessionalHeadlineActivity.class);
                        startActivity(intent);
                    }
                });

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String[] options = new String[2];

                        //TODO: Remove photo non dovrebbe spuntare se non c'è ancora la foto
                        options[0] = "Remove photo";
                        options[1] = "Upload new photo";

                        new MaterialDialog.Builder(ProfileActivity.this)
                                .items(options)
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                        if(which==0){

                                            dialog.dismiss();
                                            ((CircleImageView)findViewById(R.id.profile_image)).setImageResource(R.drawable.default_avatar);

                                            //TODO: Gestire remove photo su parse, salvando null
                                        } else {
                                            selectPhoto();
                                        }
                                    }
                                })
                                .show();
                        //TODO: GESTIRE UPLOAD FOTO
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ((TextView)findViewById(R.id.name)).setText(user.getFirstName()+" "+user.getLastName());
        imageView.setImageResource(R.drawable.default_avatar);
        if(getIntent().hasExtra("userId")){

            //TODO: PER NICO - In questo if c'è il caso in cui sto aprendo il profilo di un altro
            // L'oggetto User è nella variabile "user", però non ho ancora gestito la parte
            // in cui faccio la query per scaricarmi lo user XD
            //
            // SAMPLE CODE:
            //
            // Bitmap bitmap = user.getPhotoSync();
            // imageView.setImageDrawable(bitmap);

            //TODO: PER ENRICO: TUTTI GLI USER VENGONO SCARICATI ALL'AVVIO:
            String userID=getIntent().getStringExtra("userId");
            User otherUser=User.getFromLocalStorageStudentById(userID);
            PoliApp.getModel().getBitmapByUser(this, otherUser, new User.OnGetPhoto(){
                @Override
                public void onGetPhoto(Bitmap b) {
                    if(b==null)return;
                    imageView.setImageBitmap(b);

                }
            });




        } else {

            //TODO: PER NICO - In questo if c'è il caso in cui sto aprendo il profilo MIO
            // Il mio profilo è già salvato nell'oggetto user
            //
            // SAMPLE CODE:
            //
            // Bitmap bitmap = user.getPhotoSync();
            // imageView.setImageDrawable(bitmap);
            PoliApp.getModel().getProfileBitmap(this, user, new User.OnGetPhoto() {
                @Override
                public void onGetPhoto(Bitmap b) {
                    if(b==null)return;
                    imageView.setImageBitmap(b);
                }
            });


        }

        if(user.getProfessionalHeadline()!=null)
            ((TextView)findViewById(R.id.professionalHeadline)).setText(user.getProfessionalHeadline());

        if(user.getDescription()!=null)
            ((TextView)findViewById(R.id.aboutMe)).setText(user.getDescription());
        else if(getIntent().hasExtra("userId"))
            findViewById(R.id.aboutMeWrapper).setVisibility(View.GONE);

        ((TextView)findViewById(R.id.firstName)).setText(user.getFirstName());
        ((TextView)findViewById(R.id.lastName)).setText(user.getLastName());

        if(user.getDateOfBirth()!=null) {
            final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            ((TextView)findViewById(R.id.dateOfBirth)).setText(df.format(user.getDateOfBirth()));
        } else {
            findViewById(R.id.dateOfBirthWrapper).setVisibility(View.GONE);
        }

        String location="";

        if(user.getAddress()!=null) {
            location+=user.getAddress();
            location+="\n";
        }

        if(user.getCity()!=null) {
            location+=user.getCity();
        }

        if(user.getZipCode()!=null){
            if(user.getCity()!=null)
                location+=", ";

            location+=user.getZipCode();
            location+="\n";
        }

        if(user.getCountry()!=null) {
            location+=user.getCountry();
        }

        if(!location.trim().isEmpty()){
            ((TextView)findViewById(R.id.address)).setText(location);
        } else {
            findViewById(R.id.addressWrapper).setVisibility(View.GONE);
        }


        if(user.getMobilePhone()!=null)
            ((TextView)findViewById(R.id.mobilePhone)).setText(user.getMobilePhone());
        else
            findViewById(R.id.mobilephoneWrapper).setVisibility(View.GONE);

        if(user.getEmail()!=null)
            ((TextView)findViewById(R.id.email)).setText(user.getEmail());
        else
            findViewById(R.id.emailWrapper).setVisibility(View.GONE);


        if(!user.getLanguageSkillsAsList().isEmpty()) {

            String languagesString="";
            for(Language language: user.getLanguageSkillsAsList()){
                languagesString+=language.getLanguage();
                if(language.getLevel()!=null)
                    languagesString+=" ("+language.getLevel()+")";

                languagesString+="\n";
            }
            ((TextView)findViewById(R.id.languages)).setText(languagesString);

        } else if(getIntent().hasExtra("userId"))
            findViewById(R.id.languagesCardView).setVisibility(View.GONE);


        if(!user.getJobExperiencesAsList().isEmpty()) {

            for(JobExperience jobExperience : user.getJobExperiencesAsList()) {
                final LinearLayout jobExperienceLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_profile_main_job_experience, null );

                TextView titleTextView = (TextView)jobExperienceLayout.getChildAt(0);
                TextView organizationLocationTextView = (TextView)jobExperienceLayout.getChildAt(1);
                TextView dateTextView = (TextView)jobExperienceLayout.getChildAt(2);
                TextView descriptionTextView = (TextView)jobExperienceLayout.getChildAt(3);

                titleTextView.setText(jobExperience.getTitle());
                organizationLocationTextView.setText(jobExperience.getOrganization()+", "+jobExperience.getCity());
                dateTextView.setText(jobExperience.getStartDate()+" - "+jobExperience.getEndDate());
                descriptionTextView.setText(jobExperience.getDescription());

                ((LinearLayout) findViewById(R.id.jobsContainer)).addView(jobExperienceLayout);

            }


        } else if(getIntent().hasExtra("userId"))
            findViewById(R.id.jobsCardView).setVisibility(View.GONE);

        if(!user.getEducationsAsList().isEmpty()) {

            for(Education education : user.getEducationsAsList()) {

                final LinearLayout educationLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_profile_main_education, null );

                TextView titleTextView = (TextView)educationLayout.getChildAt(0);
                TextView universityLocationTextView = (TextView)educationLayout.getChildAt(1);
                TextView dateTextView = (TextView)educationLayout.getChildAt(2);
                TextView finalGradeTextView = (TextView)educationLayout.getChildAt(3);

                titleTextView.setText(education.getTitle());
                universityLocationTextView.setText(education.getUniversity()+" ("+education.getCity()+")");
                dateTextView.setText("Dates attended: "+education.getStartDate()+" - "+education.getEndDate());
                finalGradeTextView.setText("Final grade: "+education.getFinalGrade());

                ((LinearLayout) findViewById(R.id.educationsContainer)).addView(educationLayout);

            }

        } else if(getIntent().hasExtra("userId"))
            findViewById(R.id.educationsCardView).setVisibility(View.GONE);

        if(user.getSkills()!=null) {
            ((TextView)findViewById(R.id.skills)).setText(user.getSkills());
        } else if(getIntent().hasExtra("userId"))
            findViewById(R.id.skillsWrapper).setVisibility(View.GONE);

        if(user.getWebsite()!=null) {

            final String website = user.getWebsite();
            findViewById(R.id.website).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExternalIntents.goToWebsite(ProfileActivity.this, website);
                }
            });
        } else
            findViewById(R.id.website).setVisibility(View.GONE);

        if(user.getLinkedIn()!=null) {

            final String linkedin = user.getLinkedIn();
            findViewById(R.id.linkedin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExternalIntents.goToWebsite(ProfileActivity.this, "https://it.linkedin.com/in/"+linkedin);
                }
            });
        } else
            findViewById(R.id.linkedin).setVisibility(View.GONE);

        if(user.getFacebook()!=null) {

            final String facebook = user.getFacebook();
            findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExternalIntents.goToWebsite(ProfileActivity.this, "https://www.facebook.com/"+facebook);
                }
            });
        } else
            findViewById(R.id.facebook).setVisibility(View.GONE);


    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            mProfileImage.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            mProfileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    public void editAboutMe(View v) {

        Intent intent=new Intent(this,ProfileEditAboutMeActivity.class);
        startActivity(intent);

    }

    public void editBasicInfo(View v) {

        Intent intent=new Intent(this,ProfileEditBasicInfoActivity.class);
        startActivity(intent);
    }

    public void editLanguages(View v) {

        Intent intent=new Intent(this,ProfileEditLanguagesActivity.class);
        startActivity(intent);
    }

    public void editJobExperiences(View v) {

        Intent intent=new Intent(this,ProfileEditJobExperiencesActivity.class);
        startActivity(intent);
    }

    public void editEducations(View v) {

        Intent intent=new Intent(this,ProfileEditEducationsActivity.class);
        startActivity(intent);
    }



    public void editSkills(View v) {

        Intent intent=new Intent(this,ProfileEditSkillsActivity.class);
        startActivity(intent);
    }

    public void editOther(View v) {
        Intent intent=new Intent(this,ProfileEditOtherActivity.class);
        startActivity(intent);
    }

    public void selectPhoto() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Mode")
                .setItems(picMode, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String action = picMode[which].equalsIgnoreCase(GOTOConstants.PicModes.CAMERA) ? GOTOConstants.IntentExtras.ACTION_CAMERA : GOTOConstants.IntentExtras.ACTION_GALLERY;
                        Intent intent = new Intent(ProfileActivity.this,ImageCropActivity.class);
                        intent.putExtra("ACTION",action);
                        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
                    }
                });
        builder.create().show();

    }

    //handle data returning from camera or gallery
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if(requestCode == REQUEST_CODE_UPDATE_PIC) {
                String imagePath = data.getStringExtra(GOTOConstants.IntentExtras.IMAGE_PATH);
                if (imagePath != null) {
                    bitmap = BitmapFactory.decodeFile(imagePath);
                    imageView.setImageBitmap(bitmap);

                    final MaterialDialog dialog = new MaterialDialog.Builder(this)
                            .title("Upload in progress")
                            .content("Please wait")
                            .progress(true, 0)
                            .progressIndeterminateStyle(true)
                            .show();

                    user.updatePhotoAsync(bitmap, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            dialog.dismiss();
                            //TODO: da mettere ogni volta che la foto cambia
                            PoliApp.getModel().removeLocalCacheProfileBitmap();
                        }
                    });
                }

            }

        }

    }




}