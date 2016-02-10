package it.polito.mobilecourseproject.poliapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.TimeManager;

/**
 * Created by Enrico on 19/06/15.
 */
@ParseClassName("Chat")
public class Chat extends ParseObject {



    public boolean isGroup(){
        if(getString("title")==null || getString("title").equals("")){
            return false;
        } else {
            return true;
        }

    }

    public String getDerivedTitle() {
        String title=getString("title");
        if(title==null || title.trim().equals("")){
           for(UserInfo user : getChatters()){
               try {
                   String userId= user.userID;
                   String name=user.name;
                   if(!userId.equals(AccountManager.getCurrentUser().getObjectId())){
                       return name;
                   }
               } catch (Exception e) {
                  return "";
               }
           }

        }

        return title;
    }

    public void setTitle(String value) {
        put("title", value);
    }


    public ArrayList<UserInfo> getChatters() {
        ArrayList<UserInfo> users=new ArrayList<UserInfo>();
        try{
            JSONArray userIDs=getJSONArray("userIDs");
            JSONArray userNames=getJSONArray("userNames");
            for(int i=0;i< userIDs.length();i++){
                users.add(new UserInfo((String)userIDs.get(i),(String)userNames.get(i)));
            }
            return users;
        }catch (Exception e){
            return new ArrayList<UserInfo>();
        }

    }
    public void setChatters(List<UserInfo> users) {
        ArrayList<String> userIDs=new ArrayList<>();
        ArrayList<String> userNames=new ArrayList<>();
        for(UserInfo u:users){
            userIDs.add(u.userID);
            userNames.add(u.name);
        }
        put("userIDs", new JSONArray(userIDs));
        put("userNames", new JSONArray(userNames));
    }



    public String getLastMessageDate(Context ctx) {
        Date date=getPureLastMessageDate(ctx);
        if(date!=null) return TimeManager.getFormattedDate(date, "");
        else return "";

    }
    public void setLastMessageDate(Context ctx,Date value) {
        put("date", value);
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString(getChatID()+"DATE", dateFormatter.format(value)).commit();


    }

    public Date getPureLastMessageDate(Context ctx) {
        String stringDateLocal= PreferenceManager.getDefaultSharedPreferences(ctx).getString(getChatID() + "DATE", null);
        try{
            return dateFormatter.parse(stringDateLocal);
        }catch (Exception e){
            e.printStackTrace();
            return getDate("date");
        }

    }


   /* public boolean isRemoved(Context ctx) {
        boolean removed=PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(getChatID() + "REMOVED", false);
        return removed;
    }

    public void remove(Context ctx) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putBoolean(getChatID() + "REMOVED", true).commit();
    }*/


    public String getPreview(Context ctx) {
        String stringPreviewLocal =PreferenceManager.getDefaultSharedPreferences(ctx).getString(getChatID() + "PREVIEW", null);
        if(stringPreviewLocal!=null){
            return stringPreviewLocal;
        }
        String preview=getString("description");
        if(preview==null)preview="";
        return  preview;
    }

    public void setPreview(Context ctx,String value) {
        put("description", value);
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString(getChatID()+"PREVIEW", value).commit();


    }

    public void setChatID(String value) {
        put("chatID", value);
    }


    public String getChatID() {
        String preview=getString("chatID");
        if(preview==null)preview="";
        return  preview;
    }


    public void setSeen(Context ctx,boolean value) {
        put("seen", value);
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putInt(getChatID() + "SEEN", value ? 1 : 0).commit();
    }

    public boolean getSeen(Context ctx) {
        int seenLocal= PreferenceManager.getDefaultSharedPreferences(ctx).getInt(getChatID() + "SEEN", -1);
        if(seenLocal!=-1){
            return seenLocal==1;
        }
        return getBoolean("seen");
    }




    static SimpleDateFormat dateFormatter=new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static Chat createChat(Context ctx,String chatID,String title, List<UserInfo> users, String preview, Date date){
        Chat chat =new Chat();
        if(title!=null) chat.setTitle(title);
        if(chatID!=null)chat.setChatID(chatID);
        if(users!=null){
            chat.setChatters(users);
        }
        if(preview!=null)chat.setPreview(ctx,preview);
        if(date!=null)chat.setLastMessageDate(ctx,date);
        //chat.bitmap
        return chat;

    }
    public static String generateChatID( List<UserInfo> users){
        String s="";
        for(UserInfo ui : users){
            s=s+ui.userID;
        }
        s=s+System.currentTimeMillis();
        return s;
    }

    public static Chat createChat(Context ctx,String title, List<UserInfo> users, String preview, Date date){
        Chat chat=createChat(ctx,generateChatID(users),title, users, preview, date);
        return chat;

    }


    public interface  OnChatsDownloaded{
        void onChatsDownloaded(List<Chat> chats);
    }

    public static synchronized void getChatsFromRemote( ArrayList<String> userIDs,final OnChatsDownloaded onChatsDownloaded) {
        ParseQuery.getQuery("Chat").whereContainedIn("userIDs",userIDs).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects == null || objects.size() == 0) {
                    onChatsDownloaded.onChatsDownloaded(new ArrayList<Chat>());
                } else {
                    ArrayList<Chat> chats = new ArrayList<Chat>();
                    for (ParseObject po : objects) {
                        if (po instanceof Chat) {
                            chats.add((Chat) po);

                        }
                    }
                    storeChatsInLocal(chats);
                    onChatsDownloaded.onChatsDownloaded(getChatsFromLocal());
                }


            }
        });
    }

    public static synchronized Chat  getChatFromLocal(String chatID){
        ArrayList<Chat> chats=getChatsFromLocal();
        if(chats!=null && chats.size()!=0) {
            for (Chat c : chats) {
                if (c.getChatID().equals(chatID)) {
                    return c;
                }
            }
        }
        return null;
    }


    public static synchronized ArrayList<Chat> getChatsFromLocal(){
        try {
            ArrayList<Chat> list=new ArrayList<Chat>();
            for(ParseObject o : ParseQuery.getQuery("Chat").fromLocalDatastore().fromPin("CHAT").find()){
               if(o instanceof Chat)
                list.add((Chat)o);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Chat>();
        }
    }
    public static synchronized void storeChatInLocal(final Chat item) {
        final ArrayList<Chat> previousChats=getChatsFromLocal();
        try {
            ParseObject.unpinAll("CHAT", previousChats);
            previousChats.add(item);
            ParseObject.pinAll("CHAT", previousChats);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }

    public static synchronized void storeChatsInLocal(final ArrayList<Chat> items) {
         for(Chat item : items){
             storeChatInLocal(item);
         }
    }



    public interface  OnChatDownloaded{
       void onChatDownloaded(Chat chat);
    }
    public interface  OnChatUploaded{
        void onChatUploaded(boolean result);
    }

    public static synchronized void getChatFromRemote(String chatID, final OnChatDownloaded onChatDownloaded){
        ParseQuery.getQuery("Chat").whereEqualTo("chatID",chatID).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects == null || objects.size() == 0){
                    onChatDownloaded.onChatDownloaded((Chat) null);
                } else{
                    if(objects.size()!=0){
                        ParseObject object=objects.get(0);
                        if (object instanceof Chat)
                            onChatDownloaded.onChatDownloaded((Chat) object);
                        else
                            onChatDownloaded.onChatDownloaded((Chat) null);
                    }else{
                        onChatDownloaded.onChatDownloaded((Chat) null);
                    }
                }

            }
        });

    }





/*
    public static synchronized void getChatFromRemoteByInfo(String title, final List<UserInfo> uis, final OnChatDownloaded onChatDownloaded){
         ParseQuery.getQuery("Chat").whereEqualTo("title",title).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects == null || objects.size() == 0){
                    onChatDownloaded.onChatDownloaded((Chat) null);
                } else{
                    Chat chat=null;
                    for(ParseObject po : objects){
                        if(po instanceof Chat){
                            Chat c=(Chat)po;
                            boolean found=true;
                            for(UserInfo u : uis){
                                if(!c.getChatters().contains(u)){
                                    found=false;
                                    break;
                                }
                            }
                            if(found){
                                chat=c;
                                break;
                            }
                        }
                        onChatDownloaded.onChatDownloaded(chat);
                    }


                     onChatDownloaded.onChatDownloaded((Chat) null);





                }


            }
        });

    }*/







    public static synchronized  void storeChatInRemote(final Chat item, final OnChatUploaded onChatUploaded) {
            item.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) onChatUploaded.onChatUploaded(true);
                    else onChatUploaded.onChatUploaded(false);
                }
            });
            

    }


    public static synchronized void getChatFromLocalOrRemote(String chatID, OnChatDownloaded onChatDownloaded) {
        Chat chat=null;
        ArrayList<Chat> chats=getChatsFromLocal();
        for(Chat c : chats){
            if(chatID.equals(c.getChatID())){
                onChatDownloaded.onChatDownloaded(c);
                return;
            }
        }
       getChatFromRemote(chatID, onChatDownloaded);


    }










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
