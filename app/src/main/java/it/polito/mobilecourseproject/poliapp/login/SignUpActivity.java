package it.polito.mobilecourseproject.poliapp.login;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;

import de.hdodenhof.circleimageview.CircleImageView;
import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.Connectivity;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.User;
import it.polito.mobilecourseproject.poliapp.utils.imagezoomcrop.GOTOConstants;
import it.polito.mobilecourseproject.poliapp.utils.imagezoomcrop.ImageCropActivity;

public class SignUpActivity extends AppCompatActivity {
    private EditText companyName;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText password2;
    private EditText university;
    private ActionProcessButton signUpButton;
    private boolean isCompany=false;
    private  Bitmap bitmap;
    private CircleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Sign up!");

        imageView=((CircleImageView)findViewById(R.id.imgAvatar));


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(SignUpActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_delete);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                ((TextView) dialog.findViewById(R.id.text)).setText("DROP PHOTO");
                dialog.findViewById(R.id.delete_chat).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        bitmap=null;
                        imageView.setImageResource(R.drawable.default_avatar);

                    }
                });
                dialog.show();


                return true;
            }
        });

        companyName = (EditText) findViewById(R.id.companyName);
        firstName = (EditText)findViewById(R.id.firstName);
        lastName = (EditText)findViewById(R.id.lastName);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        password2 = (EditText)findViewById(R.id.password2);
        university = (EditText)findViewById(R.id.university);


        signUpButton = (ActionProcessButton) findViewById(R.id.signUpButton);
        signUpButton.setMode(ActionProcessButton.Mode.ENDLESS);


        companyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //TODO: Ricontrollare a che serve XD
                signUpButton.setProgress(0);
                companyName.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.firstNameWrapper)).setErrorEnabled(false);
            }
        });


        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                signUpButton.setProgress(0);
                firstName.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.firstNameWrapper)).setErrorEnabled(false);
            }
        });

        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                signUpButton.setProgress(0);
                lastName.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.lastNameWrapper)).setErrorEnabled(false);
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                signUpButton.setProgress(0);
                email.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.emailWrapper)).setErrorEnabled(false);
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                signUpButton.setProgress(0);
                password.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.passwordWrapper)).setErrorEnabled(false);
            }
        });

        password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                signUpButton.setProgress(0);
                password2.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.password2Wrapper)).setErrorEnabled(false);
            }
        });

        university.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                signUpButton.setProgress(0);
                university.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.universityWrapper)).setErrorEnabled(false);
            }
        });



        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.student) {
                    isCompany=false;
                    findViewById(R.id.studentWrapper).setVisibility(View.VISIBLE);
                    findViewById(R.id.universityWrapper).setVisibility(View.VISIBLE);
                    findViewById(R.id.companyNameWrapper).setVisibility(View.GONE);
                } else {
                    isCompany=true;
                    findViewById(R.id.studentWrapper).setVisibility(View.GONE);
                    findViewById(R.id.universityWrapper).setVisibility(View.GONE);
                    findViewById(R.id.companyNameWrapper).setVisibility(View.VISIBLE);
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.onBackPressed();
        }



        return super.onOptionsItemSelected(item);
    }





    String companyNameText;
    String firstNameText;
    String lastNameText;
    String emailText;
    String passwordText;
    String universityText;
    String resultMessage = null;
    public void signUp(View v) {

        boolean error = false;

        findViewById(R.id.parentView).requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(companyName.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(firstName.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(lastName.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(email.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(password.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(password2.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(university.getWindowToken(), 0);

        if (!Connectivity.hasNetworkConnection(getApplicationContext())) {
            Snackbar.make(findViewById(R.id.parentView), "No network connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signUp(v);
                        }
                    })
                    .show();

            return;
        }

        if (!isCompany) {
            if (firstName.getText().toString().trim().equals("")) {
                ((TextInputLayout) findViewById(R.id.firstNameWrapper)).setError("Name cannot be empty");
                error = true;
            }

            if (lastName.getText().toString().trim().equals("")) {
                ((TextInputLayout) findViewById(R.id.lastNameWrapper)).setError("Name cannot be empty");
                error = true;
            }
        } else {
            if (companyName.getText().toString().trim().equals("")) {
                ((TextInputLayout) findViewById(R.id.companyNameWrapper)).setError("Company name cannot be empty");
                error = true;
            }
        }


        if (email.getText().toString().trim().equals("") || !email.getText().toString().contains("@")) {
            ((TextInputLayout) findViewById(R.id.emailWrapper)).setError("Insert a correct e-mail");
            error = true;
        }

        if (password.getText().toString().length() < 6) {
            ((TextInputLayout) findViewById(R.id.passwordWrapper)).setError("Password must be at least 6 characters");
            error = true;
        }

        if (!password2.getText().toString().equals(password.getText().toString())) {
            ((TextInputLayout) findViewById(R.id.password2Wrapper)).setError("Passwords do not match!");
            error = true;
        }

        if (error) {
            signUpButton.setProgress(-1);
            return;
        }





        signUpButton.setProgress(1);
        signUpButton.setEnabled(false);

        companyNameText = companyName.getText().toString().trim();
        firstNameText = firstName.getText().toString().trim();
        lastNameText = lastName.getText().toString().trim();
        emailText = email.getText().toString().trim();
        passwordText = password.getText().toString().trim();
        universityText = university.getText().toString().trim();
        SignUpActivity.this.findViewById(R.id.overlay).setVisibility(View.VISIBLE);


        (new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    //TODO: TOGLIERE QUESTO IF!
                    if (universityText == null || universityText.trim().isEmpty())
                        universityText = "Politecnico di Torino";

                    AccountManager.signup(firstNameText, lastNameText, companyNameText, emailText, passwordText, universityText, isCompany,bitmap);
                } catch (Exception e) {
                    resultMessage = "Error occurred:\n" + e.getMessage();
                    e.printStackTrace();
                }



                try {
                    SignUpActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultMessage == null) {
                                signUpButton.setProgress(100);
                                //activity.onBackPressed();
                                finish();
                                Snackbar.make(findViewById(R.id.parentView), "Correctly registered!", Snackbar.LENGTH_LONG).show();

                            } else {
                                signUpButton.setProgress(-1);
                                Snackbar.make(findViewById(R.id.parentView), resultMessage, Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        signUp(v);
                                    }
                                }).show();
                                SignUpActivity.this.findViewById(R.id.overlay).setVisibility(View.GONE);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        })).start();
    }






    /********select photo from gallery or camera***********************/
    public void selectPhoto() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Mode")
                .setItems(picMode, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String action = picMode[which].equalsIgnoreCase(GOTOConstants.PicModes.CAMERA) ? GOTOConstants.IntentExtras.ACTION_CAMERA : GOTOConstants.IntentExtras.ACTION_GALLERY;
                        Intent intent = new Intent(SignUpActivity.this,ImageCropActivity.class);
                        intent.putExtra("ACTION",action);
                        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
                    }
                });
        builder.create().show();

    }



    public void deletePhoto(){
       /* profilePictureImage.setImageResource(R.drawable.no_profile_picture);
        deleteProfilePictureTextView.setVisibility(View.GONE);
        profilePictureTextView.setText("Deleting the photo...");
        profilePictureTextView.setVisibility(View.VISIBLE);
        pro_yourPhoto.setVisibility(View.VISIBLE);
        myProfile.removePhoto(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    DialogManager.toastMessage("Photo deleted", getActivity(), "center", true);
                    if (pro_yourPhoto != null){
                        pro_yourPhoto.setVisibility(View.GONE);
                        profilePictureTextView.setText("Tap on the photo to choose a profile picture.");
                    }
                } else {
                    DialogManager.toastMessage("" + e.getMessage(), getActivity(), "center", true);
                }
            }
        });*/
    }




    private String[] picMode = {GOTOConstants.PicModes.CAMERA,GOTOConstants.PicModes.GALLERY};
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;

    //handle data returning from camera or gallery
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if(requestCode == REQUEST_CODE_UPDATE_PIC) {
                String imagePath = data.getStringExtra(GOTOConstants.IntentExtras.IMAGE_PATH);
                if (imagePath != null) {
                    bitmap = BitmapFactory.decodeFile(imagePath);
                    imageView.setImageBitmap(bitmap);
                }

            }

        }

    }



}
