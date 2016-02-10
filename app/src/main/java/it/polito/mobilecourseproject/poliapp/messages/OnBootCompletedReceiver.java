package it.polito.mobilecourseproject.poliapp.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootCompletedReceiver extends BroadcastReceiver {
    public OnBootCompletedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,MessageService.class));
    }
}
