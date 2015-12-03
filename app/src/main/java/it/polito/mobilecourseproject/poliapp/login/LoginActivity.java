package it.polito.mobilecourseproject.poliapp.login;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dd.processbutton.iml.ActionProcessButton;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.AsyncTaskWithoutProgressBar;
import it.polito.mobilecourseproject.poliapp.Connectivity;
import it.polito.mobilecourseproject.poliapp.MainActivity;
import it.polito.mobilecourseproject.poliapp.R;

public class LoginActivity extends AppCompatActivity {

    private EditText userText, passText;
    ActionProcessButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userText = (EditText)findViewById(R.id.editTextUser);
        passText = (EditText)findViewById(R.id.editTextPass);

        loginButton = (ActionProcessButton) findViewById(R.id.buttonLogin);
        loginButton.setMode(ActionProcessButton.Mode.ENDLESS);

        userText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                loginButton.setProgress(0);
                loginButton.setEnabled(true);
                ((TextInputLayout)findViewById(R.id.userWrapper)).setErrorEnabled(false);
            }
        });

        passText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                loginButton.setProgress(0);
                loginButton.setEnabled(true);
                ((TextInputLayout)findViewById(R.id.passwordWrapper)).setErrorEnabled(false);
            }
        });

    }


    public void signUp(View v) {
        Intent intent=new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }


    public void login(View v) {




        boolean error=false;

        findViewById(R.id.parentView).requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(userText.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(passText.getWindowToken(), 0);
        findViewById(R.id.imageLogo).requestFocus();


        if(!Connectivity.hasNetworkConnection(getApplicationContext())){
            Snackbar.make(findViewById(R.id.parentView), "No network connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            login(v);
                        }
                    })
                    .show();

            return;
        }

        if(userText.getText().toString().trim().equals("") || !userText.getText().toString().contains("@")){
            ((TextInputLayout)findViewById(R.id.userWrapper)).setError("Insert a correct e-mail");
            error=true;
        }

        if(passText.getText().toString().trim().length()<6){
            ((TextInputLayout)findViewById(R.id.passwordWrapper)).setError("Password must be at least 6 characters");
            error=true;
        }

        if(error){
            loginButton.setProgress(-1);
            return;
        }


        new AsyncTaskWithoutProgressBar(this) {

            String email;
            String password;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loginButton.setProgress(1);
                loginButton.setEnabled(false);
                email=userText.getText().toString();
                password=passText.getText().toString();
                LoginActivity.this.findViewById(R.id.overlay).setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                String resultMessage = null;

                try {

                    AccountManager.login(email, password);

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
                    loginButton.setProgress(100);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    loginButton.setProgress(-1);
                    Snackbar.make(findViewById(R.id.parentView), "Wrong username or password", Snackbar.LENGTH_LONG).show();
                    LoginActivity.this.findViewById(R.id.overlay).setVisibility(View.GONE);
                }
            }

        }.execute();
    }

}
