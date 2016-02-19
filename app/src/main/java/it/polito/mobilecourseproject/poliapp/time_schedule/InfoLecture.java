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

public class InfoLecture extends Activity {

    SharedPreferences sharedPreference;
    Button favouriteButton;
    boolean favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_lecture);

        Intent intent  = getIntent();
        String date  = intent.getStringExtra("date");
        String color = intent.getStringExtra("color");
        final String professore = intent.getStringExtra("professore");
        String aula = intent.getStringExtra("aula");
        final String event_title = intent.getStringExtra("event_title");
        String alreadyInFavourite= intent.getStringExtra("favourite");
        String eventID= intent.getStringExtra("event_id");
        String flagConsulting = intent.getStringExtra("flagConsulting");
        Button okButton = (Button) findViewById(R.id.ok_button);
        favouriteButton = (Button) findViewById(R.id.favouriteButton);
        if(flagConsulting.equals("yes")){
            ((TextView)findViewById(R.id.titleInfoLecture)).setText("Consulting hours");
            ((LinearLayout)findViewById(R.id.linearLectureInfo)).setVisibility(View.GONE);
        }else{
            ((TextView)findViewById(R.id.titleInfoLecture)).setText("Detail lecture");
            ((LinearLayout)findViewById(R.id.linearLectureInfo)).setVisibility(View.VISIBLE);
        }

        stateStar(event_title);

        if(alreadyInFavourite!=null && alreadyInFavourite.equals("yes") || flagConsulting!=null && flagConsulting.equals("yes")){
            favouriteButton.setVisibility(View.INVISIBLE);
        }

        LinearLayout transparentLinear = (LinearLayout) findViewById(R.id.transparentLinear);
        transparentLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favouriteButton.setVisibility(View.INVISIBLE);
                finish();
            }
        });
        LinearLayout labelColor = (LinearLayout) findViewById(R.id.colorLabel);
        TextView labelColor2 = (TextView) findViewById(R.id.colorLabel2);
        labelColor.setBackgroundColor(Integer.parseInt(color));
        labelColor2.setBackgroundColor(Integer.parseInt(color));
        TextView dateLecture = (TextView) findViewById(R.id.dateLecture);
        TextView profLecture = (TextView) findViewById(R.id.profLecture);
        TextView locationLecture = (TextView) findViewById(R.id.locationLecture);
        TextView nameLecture = (TextView) findViewById(R.id.nameLecture);
        nameLecture.setText(event_title);
        dateLecture.setText(date);
        profLecture.setText(professore);
        locationLecture.setText(aula);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //favouriteButton.setVisibility(View.INVISIBLE);
                finish();
            }
        });
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                sharedPreference = PreferenceManager.getDefaultSharedPreferences(InfoLecture.this);
                SharedPreferences.Editor editor = sharedPreference.edit();
                String serialized = sharedPreference.getString("lectureList", null);
                try{
                    if(serialized!=null) {
                        List<String> listLecture =  new LinkedList<String>(Arrays.asList(TextUtils.split(serialized, ",")));
                        if (listLecture != null && listLecture.size() > 0) {
                            if(listLecture.indexOf(event_title)<0) {
                                listLecture.add(event_title);
                                editor.remove("lectureList");
                                editor.putString("lectureList", TextUtils.join(",", listLecture));
                                editor.commit();
                                favouriteButton.setBackgroundResource(R.drawable.star_filled);
                            }else{
                                favouriteButton.setBackgroundResource(R.drawable.star);
                                listLecture.remove(event_title);
                                editor.remove("lectureList");
                                editor.putString("lectureList", TextUtils.join(",", listLecture));
                                editor.commit();
                            }
                        } else {
                            listLecture = new LinkedList<String>();
                            listLecture.add(event_title);
                            editor.remove("lectureList");
                            editor.putString("lectureList", TextUtils.join(",", listLecture));
                            editor.commit();
                            favouriteButton.setBackgroundResource(R.drawable.star_filled);
                        }
                    }else{
                        List<String> listLecture = new LinkedList<String>();
                        listLecture.add(event_title);
                        editor.remove("lectureList");
                        editor.putString("lectureList", TextUtils.join(",", listLecture));
                        editor.commit();
                        favouriteButton.setBackgroundResource(R.drawable.star_filled);
                    }
                }
                catch(Exception e){
                    String error= e.getMessage().toString();
                }
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stateStar(String materia){
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(InfoLecture.this);
        String serialized = sharedPreference.getString("lectureList", null);
        if(serialized!=null){
            List<String> listLecture = Arrays.asList(TextUtils.split(serialized, ","));
            if(listLecture.indexOf(materia)>=0){
                favourite=true;
                favouriteButton.setBackgroundResource(R.drawable.star_filled);
            }else{
                favouriteButton.setBackgroundResource(R.drawable.star);
                favourite=false;
            }
        }
    }
}
