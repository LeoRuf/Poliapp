package it.polito.mobilecourseproject.poliapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import it.polito.mobilecourseproject.poliapp.messages.MessageService;
import it.polito.mobilecourseproject.poliapp.model.Chat;
import it.polito.mobilecourseproject.poliapp.model.DataModel;
import it.polito.mobilecourseproject.poliapp.model.Message;
import it.polito.mobilecourseproject.poliapp.model.Notice;
import it.polito.mobilecourseproject.poliapp.model.User;

/**
 * Created by a-endifa on 18/11/2015.
 */
public class PoliApp extends android.support.multidex.MultiDexApplication{

    public static Context ctx;
    private static DataModel model;

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(getApplicationContext());
        ParseUser.registerSubclass(User.class);
        ParseObject.registerSubclass(Notice.class);
        ParseObject.registerSubclass(Chat.class);
        //ParseObject.registerSubclass(Message.class);

        Parse.initialize(getApplicationContext(), "WY7akYTncIqmxnem30lY0YxNljh6PkYqUtbsVx6L", "VydnpHbUe5vMtLbvY3P79iByFRilB5KFipYp2jeq");

        ctx=getApplicationContext();



        model=new DataModel(ctx);



        startService(new Intent(ctx,MessageService.class));

    }



    public static DataModel getModel(){
        if(model==null)return new DataModel(ctx);
        else return model;
    }






}
