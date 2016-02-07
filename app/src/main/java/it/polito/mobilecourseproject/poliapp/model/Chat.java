package it.polito.mobilecourseproject.poliapp.model;

import android.graphics.Bitmap;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.TimeManager;

/**
 * Created by Enrico on 19/06/15.
 */
@ParseClassName("Chat")
public class Chat extends ParseObject {

    public String getTitle() {
        String title=getString("title");
        if(title==null)title="";
        if(title.trim().equals("")){
           for(String user : getUsers()){
               try {
                   String userId= user.split(";;;")[0];
                   String name=user.split(";;;")[1];
                   if(!userId.equals(AccountManager.getCurrentUser().getObjectId())){
                       return name;
                   }
               } catch (Exception e) {
                  return title;
               }
           }

        }

        return title;
    }

    public void setTitle(String value) {
        put("title", value);
    }


    public ArrayList<String> getUsers() {
        ArrayList<String> userIds=new ArrayList<String>();
        try{
            JSONArray ja=getJSONArray("users");
            for(int i=0;i< ja.length();i++){
                userIds.add(ja.get(i).toString());
            }
            return userIds;
        }catch (Exception e){
            return new ArrayList<String>();
        }

    }
    public void setUsers(List<String> users) {
        put("users", new JSONArray(users));
    }



    public String getLastMessageDate() {
        Date date=getDate("date");
        if(date!=null) return TimeManager.getFormattedDate(date, "");
        else return "";

    }

    public void setLastMessageDate(Date value) {
        put("date", value);
    }


    public String getPreview() {
        String preview=getString("description");
        if(preview==null)preview="";
        return  preview;
    }

    public void setPreview(String value) {
        put("description", value);
    }




    public List<ParseObject> getPhotos(boolean fromLocalDatastore) throws com.parse.ParseException {
        ParseRelation<ParseObject> relation = getRelation("photos");
        ParseQuery<ParseObject> query = relation.getQuery();

        if(fromLocalDatastore)
            query = query.fromLocalDatastore();

        return query.find();
    }



    public static Chat createChat(String title, List<String> users, String preview, Date date, Bitmap bitmap){
        Chat chat =new Chat();
        if(title!=null) chat.setTitle(title);
        if(users!=null) chat.setUsers(users);
        if(preview!=null)chat.setPreview(preview);
        if(date!=null)chat.setLastMessageDate(date);
        //chat.bitmap
        return chat;

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
