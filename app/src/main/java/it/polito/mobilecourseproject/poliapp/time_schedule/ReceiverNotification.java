package it.polito.mobilecourseproject.poliapp.time_schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import it.polito.mobilecourseproject.poliapp.MainActivity;

/**
 * Created by Leo on 16/02/2016.
 */
public class ReceiverNotification extends ParsePushBroadcastReceiver {

    public void onPushOpen(Context context, Intent intent) {
        //Log.e("Push", "Clicked");
        Intent i = new Intent(context, MainActivity.class);
        Bundle extras = intent.getExtras();
        String jsonData = extras.getString("com.parse.Data");
        JSONObject json = null;
        try {
            json = new JSONObject(jsonData);
            String pushStore = json.getString("alert");
            Toast.makeText(context,pushStore, Toast.LENGTH_SHORT).show();
            i.putExtra("alert", pushStore);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}