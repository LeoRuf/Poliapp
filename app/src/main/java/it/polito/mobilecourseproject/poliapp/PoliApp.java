package it.polito.mobilecourseproject.poliapp;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseUser;

import it.polito.mobilecourseproject.poliapp.model.Chat;
import it.polito.mobilecourseproject.poliapp.model.Notice;
import it.polito.mobilecourseproject.poliapp.model.User;

/**
 * Created by a-endifa on 18/11/2015.
 */
public class PoliApp extends Application {

    public static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(getApplicationContext());
        ParseUser.registerSubclass(User.class);
        ParseUser.registerSubclass(Notice.class);
        ParseUser.registerSubclass(Chat.class);

        Parse.initialize(getApplicationContext(), "WY7akYTncIqmxnem30lY0YxNljh6PkYqUtbsVx6L", "VydnpHbUe5vMtLbvY3P79iByFRilB5KFipYp2jeq");

        ctx=getApplicationContext();

    }

}
