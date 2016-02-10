package it.polito.mobilecourseproject.poliapp.messages;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.login.LoginActivity;
import it.polito.mobilecourseproject.poliapp.model.Chat;
import it.polito.mobilecourseproject.poliapp.model.Message;
import it.polito.mobilecourseproject.poliapp.model.UserInfo;


public class MessageService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handleIntent(intent);

        return START_STICKY;
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), (int)System.currentTimeMillis(), restartService, PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
         alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);

    }



    public static String SERVICE_INTENT_MESSAGE_ID="extra";
    private void handleIntent(Intent intent) {
        if(intent!=null && intent.hasExtra(SERVICE_INTENT_MESSAGE_ID)){
            String messageID=intent.getStringExtra(SERVICE_INTENT_MESSAGE_ID);
            dispatchToMQTTSender(messageID);

        }

    }

    private synchronized boolean dispatchToMQTTSender(final String messageID){
        if(senderHandler==null)return false;

        senderHandler.post(new Runnable() {
            @Override
            public void run() {
                sendMQTTMessage(messageID);
            }
        });
        return true;
    }

    private synchronized void setHandler(Handler h){
         senderHandler=h;
    }






    @Override
    public void onCreate() {
        super.onCreate();
       // sendNotification(Message.createMessage("-", new UserInfo("-", "service started"), "service started", new Date()));
        connectAndSubscribeMQTT();

    }



    private String SERVER_URL="tcp://ec2-52-33-1-154.us-west-2.compute.amazonaws.com:1883";
    public static MqttClient mqttClient=null;
    String userID=null;
    Handler senderHandler=null;
    public void connectAndSubscribeMQTT(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                    try{
                        userID=AccountManager.getCurrentUser().getObjectId();
                        mqttClient= new MqttClient(SERVER_URL,userID,new MemoryPersistence());
                        mqttClient.connect();
                        //toastInService("Connected");
                    }catch (Exception e){
                        e.printStackTrace();
                        //toastInService("not connected...retry");
                        sleep();
                        restartService();
                        return;

                    }



                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        toastInService("connection lost");
                        sleep();
                        restartService();
                        return;
                    }

                    @Override
                    public void messageArrived(String topic, final MqttMessage message) throws Exception {
                        manageMessage(topic, new String(message.getPayload()));
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });



                    try {
                        mqttClient.subscribe(userID + "/#", 2);
                        toastInService("subscribed");
                    } catch (Exception e) {
                        e.printStackTrace();
                        toastInService("not subscribed...retry");
                        try {
                            mqttClient.close();
                        } catch (MqttException e1) {
                            e1.printStackTrace();
                        }
                        sleep();
                        restartService();
                        return;
                }


                setMQTTSender();







            }}).start();
    }



    public void sendPendingMessages(){
                ArrayList<Message> allMessages=Message.getMessagesFromLocal(getApplicationContext());
                Collections.sort(allMessages, new Comparator<Message>() {
                    @Override
                    public int compare(Message lhs, Message rhs) {
                         return ((int)(lhs.getPureDate().getTime()-rhs.getPureDate().getTime()));
                    }
                }) ;

                if(allMessages==null && allMessages.size()==0)return;
                for(Message m : allMessages){
                    if(m.getUser().userID.equals(userID) && !m.isSent()){
                        dispatchToMQTTSender(m.getMessageID());
                    }
                }

    }


    public void setMQTTSender(){
        (new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                setHandler(new Handler());
                sendPendingMessages();
                Looper.loop();
            }
        })).start();



    }


    public static String SERVICE_INTENT_BROADCAST="broadcast";
    public void sendMQTTMessage(String messID){




        Message message=Message.getMessageFromLocalByMessageID(getApplicationContext(), messID);
        if(message ==null)return;
        if(message.isSent())return;
        Chat chat=Chat.getChatFromLocal(message.getChatID());
        if(chat==null)return;

        UserInfo thisUserInfo=message.getUser();
        ArrayList<String> userInfoReceiverIds=new ArrayList<String>();
        for(UserInfo ui : chat.getChatters()){
            if(!ui.equals(thisUserInfo)){
                userInfoReceiverIds.add(ui.userID);
            }
        }


        String messageID=messID;
        ArrayList<String> receiverIds=userInfoReceiverIds;
        String senderID=thisUserInfo.userID;
        String content=Message.toJsonObject(message).toString();
        Bundle bundle=new Bundle();



        MqttMessage mqttMessage = new MqttMessage(content.getBytes());
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);

        // Publish the message
        MqttDeliveryToken token = null;
        for(String receiverID : receiverIds){
            while(true){
                try {
                    MqttTopic topic = mqttClient.getTopic(receiverID+"/"+messageID);
                    token = topic.publish(mqttMessage);
                    // Wait until the message has been delivered to the broker
                    token.waitForCompletion();
                    Thread.sleep(100);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }

        }


        if(message !=null){
            message.setSent(true);
            Message.storeOrReplaceMessageInLocal(getApplicationContext(), message);
            chat.setLastMessageDate(getApplicationContext(), message.getPureDate());
            chat.setPreview(getApplicationContext(), message.getText());
            Chat.storeChatInLocal(chat);
            sendBroadcast();
        }

    }


    public void sendBroadcast(){
        Intent i = new Intent(SERVICE_INTENT_BROADCAST);
        this.sendBroadcast(i);
    }






/*
    public void setMQTTSender(){
                Looper.prepare();
                senderHandler = new Handler() {
                    @Override
                    public void handleMessage(android.os.Message msg) {



                        Bundle bundle=msg.getData();
                        ArrayList<String> receiverIds=bundle.getStringArrayList(MessageService.SERVICE_INTENT_RECEIVERS);
                        String messageID=bundle.getString(MessageService.SERVICE_INTENT_MESSAGE_ID);
                        String senderID=bundle.getString(MessageService.SERVICE_INTENT_SENDER_ID);
                        String content=bundle.getString(MessageService.SERVICE_INTENT_CONTENT);





                        MqttMessage mqttMessage = new MqttMessage(content.getBytes());
                        mqttMessage.setQos(2);
                        mqttMessage.setRetained(false);

                        // Publish the message
                        MqttDeliveryToken token = null;
                        for(String receiverID : receiverIds){
                            while(true){
                                try {
                                    MqttTopic topic = mqttClient.getTopic(receiverID+"/"+messageID);
                                    token = topic.publish(mqttMessage);
                                    // Wait until the message has been delivered to the broker
                                    token.waitForCompletion();
                                    Thread.sleep(100);
                                    //break;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    continue;
                                }
                            }

                        }


                        Message messageSent=Message.getMessageFromLocalByMessageID(getApplicationContext(),messageID);
                        if(messageSent!=null){
                            messageSent.setSent(true);
                            Message.storeOrReplaceMessageInLocal(getApplicationContext(),messageSent);
                        }



                    }
                };

                Looper.loop();



    }*/









    public void sleep(){
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public void restartService(){
        this.stopSelf();
        startService(new Intent(getApplicationContext(),MessageService.class));
    }

    public void manageMessage(String topic, String payload) {
        //toastInService("message received");


        final Message m=Message.createMessage(payload,true);
        if(m==null)return;
        List<Message> messages=Message.getMessagesFromLocal(getApplicationContext());
        if(!messages.contains(m)){
            Message.storeOrReplaceMessageInLocal(getApplicationContext(), m);
            Chat.getChatFromLocalOrRemote(m.getChatID(), new Chat.OnChatDownloaded() {
                @Override
                public void onChatDownloaded(Chat chat) {
                    if (chat == null) {
                        return;//nothing to do
                    }
                    chat.setLastMessageDate(getApplicationContext(), m.getPureDate());
                    chat.setPreview(getApplicationContext(), m.getText());
                    chat.setSeen(getApplicationContext(), false);
                    Chat.storeChatInLocal(chat);
                    //toastInService(m.getText());
                    sendNotification(m,chat);
                    sendBroadcast();
                }
            });



        }
    }














    public void toastInService(final String s){
        try{
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), s,Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }





    public void sendNotification(Message m,Chat chat){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.mipmap.ic_launcher;
        CharSequence notiText = "notification" ;
        long now = System.currentTimeMillis();

        Context context = getApplicationContext();

        String title="";
        if(chat.isGroup())title=chat.getDerivedTitle()+"@";

        int SERVER_DATA_RECEIVED = m.getChatID().hashCode();

        CharSequence contentTitle = title+ m.getUser().name;
        CharSequence contentText = m.getText();
        Intent notificationIntent=new Intent(context, LoginActivity.class);
        notificationIntent.putExtra("CHAT_ID", m.getChatID());
        PendingIntent contentIntent = PendingIntent.getActivity(context,SERVER_DATA_RECEIVED, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        Notification notification=notificationBuilder.setContentText(notiText)
                .setSmallIcon(icon)
                .setWhen(now)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(contentIntent)
                .build();


        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;


        notificationManager.notify(SERVER_DATA_RECEIVED, notification);
    }


    public static void hideNotification(Context ctx,Chat chat){
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(chat.getChatID().hashCode());
    }




}
