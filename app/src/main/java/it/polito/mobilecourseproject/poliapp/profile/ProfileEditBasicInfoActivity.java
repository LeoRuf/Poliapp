package it.polito.mobilecourseproject.poliapp.profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.AsyncTaskWithoutProgressBar;
import it.polito.mobilecourseproject.poliapp.Connectivity;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.User;

public class ProfileEditBasicInfoActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText address;
    private EditText city;
    private EditText zipCode;
    private EditText country;
    private EditText mobilePhone;
    private EditText dateOfBirth;

    private Date dateOfBirthDate;


    private ActionProcessButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_basic_info);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit your information");

        firstName = (EditText)findViewById(R.id.firstName);
        lastName = (EditText)findViewById(R.id.lastName);
        email = (EditText)findViewById(R.id.email);
        address = (EditText)findViewById(R.id.address);
        city = (EditText)findViewById(R.id.city);
        zipCode = (EditText)findViewById(R.id.zipCode);
        country = (EditText)findViewById(R.id.country);
        mobilePhone = (EditText)findViewById(R.id.mobilephone);
        dateOfBirth = (EditText)findViewById(R.id.dateOfBirth);

        try {
            User thisUser = AccountManager.getCurrentUser();

            firstName.setText(thisUser.getFirstName());
            lastName.setText(thisUser.getLastName());

            if(thisUser.getAddress()!=null && !thisUser.getAddress().trim().isEmpty())
                address.setText(thisUser.getAddress());

            if(thisUser.getCity()!=null && !thisUser.getCity().trim().isEmpty())
                city.setText(thisUser.getCity());

            if(thisUser.getZipCode()!=null && !thisUser.getZipCode().trim().isEmpty())
                zipCode.setText(thisUser.getZipCode());

            if(thisUser.getCountry()!=null && !thisUser.getCountry().trim().isEmpty())
                country.setText(thisUser.getCountry());

            if(thisUser.getMobilePhone()!=null && !thisUser.getMobilePhone().trim().isEmpty())
                mobilePhone.setText(thisUser.getMobilePhone());

            if(thisUser.getDateOfBirth()!=null) {
                final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                dateOfBirthDate=thisUser.getDateOfBirth();
                dateOfBirth.setText(df.format(dateOfBirthDate));
            }

            if(thisUser.getEmail()!=null && !thisUser.getEmail().trim().isEmpty())
                email.setText(thisUser.getEmail());



        } catch (Exception e) {

            //TODO: RIMUOVERE TOAST
            Toast.makeText(this, "DEBUG: Error while getting current user", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }


        saveButton = (ActionProcessButton) findViewById(R.id.saveButton);
        saveButton.setMode(ActionProcessButton.Mode.ENDLESS);

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                saveButton.setProgress(0);
                email.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.emailWrapper)).setErrorEnabled(false);
            }
        });

        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                saveButton.setProgress(0);
                address.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.addressWrapper)).setErrorEnabled(false);
            }
        });

        country.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                saveButton.setProgress(0);
                country.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.countryWrapper)).setErrorEnabled(false);
            }
        });

        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                saveButton.setProgress(0);
                city.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.cityWrapper)).setErrorEnabled(false);
            }
        });


        zipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                saveButton.setProgress(0);
                zipCode.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.zipCodeWrapper)).setErrorEnabled(false);
            }
        });

        mobilePhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                saveButton.setProgress(0);
                mobilePhone.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.mobilephoneWrapper)).setErrorEnabled(false);
            }
        });

        dateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //TODO: Ricontrollare a che serve XD
                saveButton.setProgress(0);
                dateOfBirth.setEnabled(true);
                ((TextInputLayout) findViewById(R.id.dateOfBirthWrapper)).setErrorEnabled(false);
            }
        });




    }

    public void save(View v) {

        boolean error=false;

        findViewById(R.id.parentView).requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(firstName.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(lastName.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(email.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(address.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(city.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(country.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(zipCode.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(mobilePhone.getWindowToken(), 0);


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

        if(!email.getText().toString().trim().equals("") && !email.getText().toString().contains("@")){
            ((TextInputLayout)findViewById(R.id.emailWrapper)).setError("Insert a correct e-mail");
            error=true;
        }

        if(error){
            saveButton.setProgress(-1);
            return;
        }


        new AsyncTaskWithoutProgressBar(this) {

            String emailText;
            String addressText;
            String cityText;
            String countryText;
            String zipCodeText;
            String mobilePhoneText;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                saveButton.setProgress(1);
                saveButton.setEnabled(false);

                addressText=address.getText().toString().trim();
                cityText=city.getText().toString().trim();
                emailText=email.getText().toString().trim();
                countryText=country.getText().toString().trim();
                zipCodeText=zipCode.getText().toString().trim();
                mobilePhoneText=mobilePhone.getText().toString().trim();
            }

            @Override
            protected String doInBackground(Void... params) {
                String resultMessage = null;

                try {
                    User thisUser = AccountManager.getCurrentUser();

                    thisUser.setAddress(addressText);
                    thisUser.setCity(cityText);
                    thisUser.setZipCode(zipCodeText);
                    thisUser.setCountry(countryText);
                    thisUser.setMobilePhone(mobilePhoneText);
                    thisUser.setEmail(emailText);

                    if(dateOfBirthDate==null)
                        thisUser.remove("dateOfBirth");
                    else
                        thisUser.setDateOfBirth(dateOfBirthDate);

                    AccountManager.getCurrentUser().save();
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
                    saveButton.setEnabled(true);
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

    public void selectDateOfBirth(View view) {
                Calendar currentDate = Calendar.getInstance();
                int year = currentDate.get(Calendar.YEAR);
                int month = currentDate.get(Calendar.MONTH);
                int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);

                if (dateOfBirthDate != null) {

                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(dateOfBirthDate);
                    year = gc.get(Calendar.YEAR);
                    month = gc.get(Calendar.MONTH);
                    dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);

                }
                DatePickerDialog datePicker;

                datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        GregorianCalendar dateOfBirthGC = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                        dateOfBirthDate = dateOfBirthGC.getTime();

                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        dateOfBirth.setText(df.format(dateOfBirthDate));

                    }
                }, year, month, dayOfMonth);
                datePicker.setButton(DatePickerDialog.BUTTON_NEUTRAL, "CLEAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dateOfBirthDate=null;
                        dateOfBirth.setText("");
                    }
                });
                datePicker.show();
            }

}
