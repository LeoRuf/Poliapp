package it.polito.mobilecourseproject.poliapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.PoliApp;
import it.polito.mobilecourseproject.poliapp.TimeManager;
import it.polito.mobilecourseproject.poliapp.messages.MessageService;


public class Message  implements Comparable<Message>{

    String text="";
    Date messageDate;
    String userID="";
    String userName="";
    String chatID="";
    String messageID="";
    boolean sent=false;
    static SimpleDateFormat jsonDateFormatter=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");



    public String getText() {
       return text;
    }
    public void setText(String value) {
        this.text=value;
    }


    @Override
    public boolean equals(Object o){
        if(!(o instanceof Message)) return false;
        Message m=(Message)o;
        return  m.getMessageID().equals(getMessageID());
    }






    public String getMessageDate() {
        if(messageDate!=null) return TimeManager.getFormattedMessageDate(messageDate);
        else return "";

    }

    public Date getPureDate() {
        return messageDate;
    }


    @Override
    public int compareTo(Message another) {
        Date date1=this.messageDate;
        Date date2=another.getPureDate();
        if(date1.after(date2))return 1;
        else return -1;
    }


    public static void reverseSort(ArrayList<Message> messages){
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message lhs, Message rhs) {
                return -1*((int)(lhs.getPureDate().getTime()-rhs.getPureDate().getTime()));
            }
        });
    }



    public void setMessageDate(Date value) {
       this.messageDate=value;
    }


    public UserInfo getUser() {

        return new UserInfo(userID,userName);
    }

    public void setUser(UserInfo value) {
        userID=value.userID;
        userName=value.name;
    }



    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean b) {
        sent=b;
    }


    public String getChatID() {
        return chatID;
    }
    public String setChatID(String value) {
       return chatID=value;
    }
    public String getMessageID() {
        return messageID;
    }
    public void setMessageID(String val) {
        messageID=val;
    }




    public static String generateMessageID(String userID){
        return  userID+System.currentTimeMillis();
    }


    public static Message createMessage(String chatID, UserInfo user, String text, Date date){
        return createMessage(generateMessageID(user.userID), chatID, user, text, date,false);

    }


    public static Message createMessage(String messageID,String chatID, UserInfo user, String text, Date date,boolean sent){
        Message message =new Message();
        message.setChatID(chatID);
        message.setMessageID(messageID);
        message.setUser(user);
        message.setText(text);
        message.setMessageDate(date);
        message.setSent(sent);
        return message;

    }

    public static Message createMessage(String messageJson,boolean dateAndSentExNovo){
        Message message=null;
        try {
            JSONObject jo= new JSONObject(messageJson);
            String messageID=jo.getString("messageID");
            String chatID=jo.getString("chatID");
            String userName=jo.getString("userName");
            String userID=jo.getString("userID");
            String text=jo.getString("text");
            Date date=null;
            boolean sent=false;
            if(dateAndSentExNovo){
                date=new Date();
            }else{
                String stringDate=jo.getString("date");
                date=jsonDateFormatter.parse(stringDate);
                sent=jo.getBoolean("sent");
            }

            message=Message.createMessage(messageID,chatID,new UserInfo(userID,userName),text,date,sent);
            return message;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }




    }



    public static JSONObject toJsonObject(Message m){
        JSONObject jo=new JSONObject();
        try {
            jo.put("messageID", m.getMessageID());
            jo.put("chatID", m.getChatID());
            jo.put("userName", m.getUser().name);
            jo.put("userID", m.getUser().userID);
            jo.put("text", m.getText());
            jo.put("sent", m.isSent());
            jo.put("date", jsonDateFormatter.format(m.getPureDate()));
            return jo;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }








   /* public static ArrayList<Message> getMessagesFromLocal(){
        try {
            ArrayList<Message> list=new ArrayList<Message>();
            for(ParseObject o : ParseQuery.getQuery("Message").fromLocalDatastore().fromPin("MESSAGE").fromfind()){
                if(o instanceof Message)
                    list.add((Message)o);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Message>();
        }
    }
    public static  void storeOrReplaceMessageInLocal(final Message item) {
        final ArrayList<Message> previousMessages=getMessagesFromLocal();
        try {
            item.unpinAll("MESSAGE", previousMessages);
            if(item instanceof Message)
                previousMessages.add(item);
            ParseObject.pinAll("MESSAGE", previousMessages);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }*/

   private static String MESSAGES="messages";
    public static synchronized ArrayList<Message> getMessagesFromLocal(Context ctx) {
        ArrayList<Message> messages=new ArrayList<>();
        Set<String> stringJsonObjects= PreferenceManager.getDefaultSharedPreferences(ctx).getStringSet(MESSAGES,null);


        if(stringJsonObjects!=null){
            for(String stringJsonObject : stringJsonObjects){
                Message message= Message.createMessage(stringJsonObject,false);
                if(message==null)continue;
                messages.add(message);

            }
        }

        return messages;
    }


    public static synchronized ArrayList<Message> getMessagesFromLocalByChatID(Context ctx,String chatID) {
        ArrayList<Message> messages= getMessagesFromLocal(ctx);
        ArrayList<Message> filteredMessages=new ArrayList<>();
        for(Message m : messages){
            if(m.getChatID().equals(chatID))
                filteredMessages.add(m);
        }
        return filteredMessages;
    }

    public static synchronized Message getMessageFromLocalByMessageID(Context ctx,String messageID) {
        ArrayList<Message> messages= getMessagesFromLocal(ctx);
         Message message=null;
        for(Message m : messages){
            if(m.getMessageID().equals(messageID)){
                message=m;
                break;
            }
        }
        return message;
    }



    public static synchronized void storeOrReplaceMessageInLocal(Context ctx, final Message item) {

        ArrayList<Message> messages=getMessagesFromLocal(ctx);
        ArrayList<Message> newMessages=new ArrayList<Message>();
        for(Message m: messages){
            if(!m.getMessageID().equals(item.getMessageID())){
                newMessages.add(m);
            }
        }
        newMessages.add(item);
        messages.clear();
        messages.addAll(newMessages);

        Set<String> stringJsonObjects = new HashSet<>();
        for(Message m : messages){
            String stringJsonObject = Message.toJsonObject(m).toString();
            if(stringJsonObject==null) continue;
            stringJsonObjects.add(stringJsonObject);
        }

        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putStringSet(MESSAGES, stringJsonObjects).commit();

    }









/*
    public interface OnMessagesGetFromLocalCallback {
        void onMessagesGet(List<Message> messages);
    }
    public interface OnMessageSaved {
        void onMessageSaved(boolean saved);
    }


    public static void getFromLocalStorageAllMessages(final OnMessagesGetFromLocalCallback callback){
        ParseQuery<Message> query = ParseQuery.getQuery("Message");
        query.fromLocalDatastore().fromPin("MessageStore");
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(final List<Message> objects, ParseException e) {
                if (objects == null || objects.size() == 0) {
                    callback.onMessagesGet(new ArrayList<Message>());
                } else {
                    callback.onMessagesGet((objects));
                }

            }
        });

    }


    public static void storeMessage(final Message message, final OnMessageSaved onMessageSaved){
            getFromLocalStorageAllMessages(
                    new OnMessagesGetFromLocalCallback() {
                        @Override
                        public void onMessagesGet(List<Message> messages) {
                            try {
                                if (messages.size()!=0){
                                    ParseObject.unpinAll("MessageStore", messages);
                                }
                                messages.add(message);
                                ParseObject.pinAll("MessageStore", messages);
                                onMessageSaved.onMessageSaved(true);
                            }catch (Exception e){
                                e.printStackTrace();
                                onMessageSaved.onMessageSaved(false);
                            }


                        }
                    }
            );


    }

*/


    /*
    public String getPhoneNumber() {
        return getString("phoneNumber");
    }
    public void setPhoneNumber(String phoneNumber) {
        put("phoneNumber", phoneNumber);
    }

    public String getCity() {
        return getString("city");
    }
    public void setCity(String city) {
        put("city", city);
    }

    public String getCountry() {
        return getString("country");
    }
    public void setCountry(String country) {
        put("country", country);
    }

    public List<Notice> getFavouritesOffers() throws com.parse.ParseException {

        ParseRelation<Notice> relation = getRelation("favouritesOffers");
        return relation.getQuery().find();

    }


    public void updatePhoto(Bitmap bitImage, SaveCallback saveCallback) {
        ParseObject photo = new ParseObject("Photo");
        photo.put("name", "photoProfile");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitImage.compress(Bitmap.CompressFormat.PNG, 60, stream);
        byte[] byteArray = stream.toByteArray();
        photo.put("photo", new ParseFile(byteArray));

        if(this.has("photoProfile"))
            this.getParseObject("photoProfile").deleteInBackground();

        this.put("photoProfile", photo);
        this.saveInBackground(saveCallback);

    }

    public void removePhoto(SaveCallback saveCallback) {
        final ParseObject photo = (ParseObject)this.get("photoProfile");
        if(photo==null)return;
        photo.deleteInBackground();
        this.remove("photoProfile");
        this.saveInBackground(saveCallback);

    }


    public Bitmap getPhoto(Context context) throws com.parse.ParseException {

        ParseObject photo = (ParseObject)this.get("photoProfile");
        if(photo==null)return null;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
        byte[]  data = query.get(photo.getObjectId()).getParseFile("photo").getData();
        if(data==null)return null;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bitmap;
    }

    public ParseQuery<Notice> getFavouritesOffersRelationQuery() throws com.parse.ParseException {
        ParseRelation<Notice> relation = getRelation("favouritesOffers");
        return relation.getQuery();
    }*/


}
