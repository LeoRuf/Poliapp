package it.polito.mobilecourseproject.poliapp.time_schedule;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.R;

public class NoEvent extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noevent);
        Button buttonOK = (Button)findViewById(R.id.ok_button);
        Intent intent= getIntent();
        final String longPress = intent.getStringExtra("longPress");
        Button buttonCANCEL= ((Button)findViewById(R.id.cancel_button));
        if(longPress!=null && longPress.equals("yes")){
            ((TextView)findViewById(R.id.header)).setText("Delete lecture");
            ((TextView)findViewById(R.id.content)).setText("Are you sure you want to delete this lecture?");
            ((Button)findViewById(R.id.cancel_button)).setVisibility(View.VISIBLE);
        } if(longPress!=null && longPress.equals("no")){
            ((TextView)findViewById(R.id.header)).setText("No events");
            ((TextView)findViewById(R.id.content)).setText("Click the button to add one.");
            ((Button)findViewById(R.id.cancel_button)).setVisibility(View.INVISIBLE);
        }
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(longPress.equals("no")) {
                    finish();
                }else{
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result","delete");
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });

        buttonCANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
