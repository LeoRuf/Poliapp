package it.polito.mobilecourseproject.poliapp.time_schedule;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import it.polito.mobilecourseproject.poliapp.R;

public class NoEvent extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noevent);
        Button button = (Button)findViewById(R.id.ok_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
