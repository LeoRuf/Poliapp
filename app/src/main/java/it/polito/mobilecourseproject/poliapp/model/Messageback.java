package it.polito.mobilecourseproject.poliapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import it.polito.mobilecourseproject.poliapp.TimeManager;

/**
 * Created by Enrico on 19/06/15.
 */

@ParseClassName("Message")
public class Messageback extends ParseObject implements Comparable<Messageback>{




    public String getText() {
        return getString("text");
    }
    public void setText(String value) {
        put("text", value);
    }


    @Override
    public boolean equals(Object o){
        if(!(o instanceof Messageback)) return false;
        Messageback m=(Messageback)o;
        return  m.getMessageID().equals(getMessageID());
    }






    public String getMessageDate() {
        Date date= getDate("date");
        if(date!=null) return TimeManager.getFormattedMessageDate(date);
        else return "";

    }

    public Date getPureDate() {
        Date date= getDate("date");
        return date;
    }


    @Override
    public int compareTo(Messageback another) {
        Date date1=getDate("date");
        Date date2=another.getDate("date");
        if(date1.before(date2))return 1;
        else return -1;
    }


    public static void reverseSort(ArrayList<Messageback> messages){
        Collections.sort(messages);
    }



    public void setMessageDate(Date value) {
        put("date", value);
    }


    public UserInfo getUser() {

        return new UserInfo(getString("userID"),getString("userName"));
    }

    public void setUser(UserInfo value) {
        put("userID",value.userID);
        put("userName",value.name);;
    }


    public String getChatID() {
        return getString("chatID").split(DIVIDER)[0];
    }
    public String getMessageID() {
        return getString("chatID").split(DIVIDER)[1];
    }
    public void setChatIDAndMessageID(String s){
         put("chatID",s);
    }



    /*
    public String getChatID() {
       String messageAndChatID =getMessageAndChatID();
        try {
            JSONObject jo = new JSONObject(messageAndChatID);
            return jo.getString("chatID");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
    public String getMessageID() {
        String messageAndChatID =getMessageAndChatID();
        try {
            JSONObject jo = new JSONObject(messageAndChatID);
            return jo.getString("messageID");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setMessageAndChatID(String messageID,String chatID) {
        try {
            JSONObject jo=new JSONObject();
            jo.put("messageID",messageID);
            jo.put("chatID",chatID);
            put("messageAndChatID", jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getMessageAndChatID() {
        return getString("messageAndChatID");
    }

     */





    public static String generateMessageID(String userID){
        return  userID+System.currentTimeMillis();
    }


    public static Messageback createMessage(String chatID, UserInfo user, String text, Date date){
        return createMessage(generateMessageID(user.userID), chatID, user, text, date);

    }
    private static String DIVIDER=";;;;;;;;;;";
    public static Messageback createMessage(String messageID,String chatID, UserInfo user, String text, Date date){
        Messageback message =new Messageback();
        message.setChatIDAndMessageID(chatID+DIVIDER);
        message.setUser(user);
        message.setText(text);
        message.setMessageDate(date);
        return message;

    }

    public static Messageback createMessage(String messageJson){
        Messageback message=null;
        try {
            JSONObject jo= new JSONObject(messageJson);
            String messageID=jo.getString("messageID");
            String chatID=jo.getString("chatID");
            String userName=jo.getString("userName");
            String userID=jo.getString("userID");
            String text=jo.getString("text");
            Date date=new Date();
            message= Messageback.createMessage(messageID, chatID, new UserInfo(userID, userName), text, date);
            return message;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }




    }



    public String toJson(){
        JSONObject jo=new JSONObject();
        try {
            jo.put("messageID", getMessageID());
            jo.put("chatID", getChatID());
            jo.put("userName", getUser().name);
            jo.put("userID", getUser().userID);
            jo.put("text", getText());
            return jo.toString();
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

    public static ArrayList<Messageback> getMessagesFromLocal(){
        try {
            ArrayList<Messageback> list=new ArrayList<Messageback>();
            for(ParseObject o : ParseQuery.getQuery("Message").fromLocalDatastore().find()){
                if(o instanceof Messageback)
                    list.add((Messageback)o);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Messageback>();
        }
    }
    public static  void storeMessageInLocal(final Messageback item) {
        try {
            item.pin();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
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
