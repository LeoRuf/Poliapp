package it.polito.mobilecourseproject.poliapp.login;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dd.processbutton.iml.ActionProcessButton;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.AsyncTaskWithoutProgressBar;
import it.polito.mobilecourseproject.poliapp.Connectivity;
import it.polito.mobilecourseproject.poliapp.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText password2;
    private EditText university;
    private ActionProcessButton signUpButton;

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

        findViewById(R.id.imgAvatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Aggiungere logica per upload immagine
                ((de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.imgAvatar)).setImageResource(R.drawable.my_photo);
            }
        });

        firstName = (EditText)findViewById(R.id.firstName);
        lastName = (EditText)findViewById(R.id.lastName);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        password2 = (EditText)findViewById(R.id.password2);
        university = (EditText)findViewById(R.id.university);


        signUpButton = (ActionProcessButton) findViewById(R.id.signUpButton);
        signUpButton.setMode(ActionProcessButton.Mode.ENDLESS);

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

    }

    public void signUp(View v) {

        boolean error=false;

        findViewById(R.id.parentView).requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(firstName.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(lastName.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(email.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(password.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(password2.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(university.getWindowToken(), 0);

        if(!Connectivity.hasNetworkConnection(getApplicationContext())){
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

        if(firstName.getText().toString().trim().equals("")){
            ((TextInputLayout)findViewById(R.id.firstNameWrapper)).setError("Name cannot be empty");
            error=true;
        }

        if(lastName.getText().toString().trim().equals("")){
            ((TextInputLayout)findViewById(R.id.lastNameWrapper)).setError("Name cannot be empty");
            error=true;
        }

        if(email.getText().toString().trim().equals("") || !email.getText().toString().contains("@")){
            ((TextInputLayout)findViewById(R.id.emailWrapper)).setError("Insert a correct e-mail");
            error=true;
        }

        if(password.getText().toString().length()<6){
            ((TextInputLayout)findViewById(R.id.passwordWrapper)).setError("Password must be at least 6 characters");
            error=true;
        }

        if(!password2.getText().toString().equals(password.getText().toString())){
            ((TextInputLayout)findViewById(R.id.password2Wrapper)).setError("Passwords do not match!");
            error=true;
        }

        if(error){
            signUpButton.setProgress(-1);
            return;
        }


        new AsyncTaskWithoutProgressBar(this) {

            String firstNameText;
            String lastNameText;
            String emailText;
            String passwordText;
            String universityText;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                signUpButton.setProgress(1);
                signUpButton.setEnabled(false);

                firstNameText=firstName.getText().toString();
                lastNameText=lastName.getText().toString();
                emailText=email.getText().toString();
                passwordText=password.getText().toString();
                universityText=university.getText().toString();
                SignUpActivity.this.findViewById(R.id.overlay).setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                String resultMessage = null;

                try {
                    //TODO: TOGLIERE QUESTO IF!
                    if(universityText==null || universityText.trim().isEmpty())
                        universityText="Politecnico di Torino";

                    AccountManager.signup(firstNameText, lastNameText, emailText, passwordText, universityText);
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

        }.execute();
    }

}
