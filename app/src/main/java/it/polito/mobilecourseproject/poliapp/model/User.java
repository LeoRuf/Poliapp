package it.polito.mobilecourseproject.poliapp.model;

import android.content.Context;
import android.preference.PreferenceManager;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Enrico on 19/06/15.
 */
@ParseClassName("_User")
public class User extends ParseUser {


    @Override
    public boolean equals(Object o){
        if(o instanceof User){
            return getObjectId().equals(((User)o).getObjectId());
        }else{
            return false;
        }

    }



    public String getFirstName() {
        return getString("firstName");
    }

    public void setFirstName(String value) {
        put("firstName", value);
    }

    public String getLastName() {
        return getString("lastName");
    }

    public void setDescription(String value) {
        put("description", value);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setLastName(String value) {
        put("lastName", value);
    }

    public String getUniversity() {
        return getString("university");
    }

    public void setUniversity(String value) {
        put("university", value);
    }

    public String getCity() {
        return getString("city");
    }

    public void setCity(String value) {
        put("city", value);
    }

    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String value) {
        put("address", value);
    }

    public String getZipCode() {
        return getString("zipCode");
    }

    public void setZipCode(String value) {
        put("zipCode", value);
    }

    public String getLinkedIn() {
        return getString("linkedin");
    }

    public void setLinkedIn(String value) {
        put("linkedin", value);
    }

    public String getFacebook() {
        return getString("facebook");
    }

    public void setFacebook(String value) {
        put("facebook", value);
    }

    public String getWebsite() {
        return getString("website");
    }

    public void setWebsite(String value) {
        put("website", value);
    }

    public String getCountry() {
        return getString("country");
    }

    public void setCountry(String value) {
        put("country", value);
    }

    public String getMobilePhone() {
        return getString("mobilePhone");
    }

    public void setMobilePhone(String value) {
        put("mobilePhone", value);
    }

    public String getSkills() {
        return getString("skills");
    }

    public void setSkills(String value) {

        put("skills", value);
        put("skills_search", value.toLowerCase());
    }

    public Date getDateOfBirth() {
        return getDate("dateOfBirth");
    }

    public void setDateOfBirth(Date value) {
        put("dateOfBirth", value);
    }

    public ArrayList<String> getLanguageSkillsAsList(){
        String languagesText=this.getLanguageSkills();
        if(languagesText==null || languagesText.trim().equals(""))return  new ArrayList<String>();
        String[] languages=languagesText.split("\n");

        return  new ArrayList<String>(Arrays.asList(languages));
    }

    public String getLanguageSkills() {
        return getString("languages");
    }

    public void setLanguageSkills(String value){
        put("languages", value);
    }

    public static User createUser(String firstName,String lastName, String university){
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUniversity(university);
        return user;

    }


    public interface OnUsersDownloadedCallback {
       void onUsersDownloaded(List<User> users);
    }


    public static void downloadAllUsers(final Context ctx,final OnUsersDownloadedCallback callback){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
         //query.orderByDescending("updatedAt");
        // query.whereGreaterThan("updatedAt", new Date(PreferenceManager.getDefaultSharedPreferences(ctx).getLong("Users_timestamp", 0)));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(final List<ParseUser> objects, ParseException e) {
                ArrayList<User> users= new ArrayList<User>();
                if(objects==null || objects.size()==0){
                    callback.onUsersDownloaded(users);
                }else{
                    ParseObject.pinAllInBackground(objects, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            //PreferenceManager.getDefaultSharedPreferences(ctx).edit().putLong("Users_timestamp", objects.get(0).getUpdatedAt().getTime()).commit();
                        }
                    });
                    for(ParseUser pu : objects){
                        users.add((User)pu);
                    }
                    callback.onUsersDownloaded((users));
                }

            }
        });

    }


    public static void getFromLocalStorageAllUsers(final Context ctx,final OnUsersDownloadedCallback callback){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(final List<ParseUser> objects, ParseException e) {
                ArrayList<User> users= new ArrayList<User>();
                if(objects==null || objects.size()==0){
                    callback.onUsersDownloaded(users);
                }else{
                    for(ParseUser pu : objects){
                        users.add((User) pu);
                    }
                    callback.onUsersDownloaded((users));
                }

            }
        });

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
