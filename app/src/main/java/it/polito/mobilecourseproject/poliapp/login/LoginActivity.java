package it.polito.mobilecourseproject.poliapp.login;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.AsyncTaskWithoutProgressBar;
import it.polito.mobilecourseproject.poliapp.CompanyMainActivity;
import it.polito.mobilecourseproject.poliapp.Connectivity;
import it.polito.mobilecourseproject.poliapp.MainActivity;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.messages.ChatActivity;
import it.polito.mobilecourseproject.poliapp.messages.MessageService;

public class LoginActivity extends AppCompatActivity {

    private EditText userText, passText;
    ActionProcessButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //FROM NOTIFICATION
        String chatID=getIntent().getStringExtra("CHAT_ID");
        if(chatID!=null && AccountManager.checkIfLoggedIn()){
            Intent i1=new Intent(LoginActivity.this, MainActivity.class);
            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i1);
            Intent i=new Intent(LoginActivity.this, ChatActivity.class);
            i.putExtra("CHAT_ID", chatID);
            startActivity(i);
            finish();
            return;
        }




        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(AccountManager.checkIfLoggedIn()){
           nextActivity();
        }

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
             Snackbar sb=Snackbar.make(findViewById(R.id.parentView), "No network connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            login(v);
                        }
                    });
            ((TextView)((ViewGroup)sb.getView()).findViewById(android.support.design.R.id.snackbar_text)).setBackgroundColor(0);
            sb.show();


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
                  nextActivity();
                } else {
                    loginButton.setProgress(-1);
                    Snackbar sb=Snackbar.make(findViewById(R.id.parentView), "Wrong username or password", Snackbar.LENGTH_LONG);
                    ((TextView)((ViewGroup)sb.getView()).findViewById(android.support.design.R.id.snackbar_text)).setBackgroundColor(0);
                    sb.show();
                    LoginActivity.this.findViewById(R.id.overlay).setVisibility(View.GONE);
                }
            }

        }.execute();
    }





    public void nextActivity(){
        if(AccountManager.checkIfStudentLoggedIn()){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }else{
            startActivity(new Intent(LoginActivity.this, CompanyMainActivity.class));
        }
        finish();
        return;
    }

}
