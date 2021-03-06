package it.polito.mobilecourseproject.poliapp.model;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import it.polito.mobilecourseproject.poliapp.MyUtils;

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


    public boolean isCompany(){
        return getBoolean("isCompany");
    }

    public void setCompany(){
        put("isCompany", true);
    }
    public void setStudent(){
        put("isCompany", false);
    }

    public String getCompanyName() {
        return getString("companyName");
    }

    public void setCompanyName(String value) {
        put("companyName", value);
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

    public ArrayList<Language> getLanguageSkillsAsList(){
        String languagesText=this.getLanguageSkills();
        if(languagesText==null || languagesText.trim().equals(""))
            return new ArrayList<Language>();

        String[] languages=languagesText.split(Pattern.quote(MyUtils.CUSTOM_DELIMITER));
        ArrayList<Language> list = new ArrayList<Language>();

        for(String lang:languages){

            //Per sicurezza...
            if(lang.trim().isEmpty() || lang.contains(MyUtils.CUSTOM_DELIMITER))
                continue;

            String[] tokens=lang.split(Pattern.quote(MyUtils.CUSTOM_DELIMITER_2));
            Language languageObj = new Language();
            languageObj.setLanguage(tokens[0]);
            if(tokens.length>1 && !tokens[1].contains(MyUtils.CUSTOM_DELIMITER_2))
                languageObj.setLevel(tokens[1]);

            list.add(languageObj);
        }

        return list;
    }

    public String getLanguageSkills() {
        return getString("languages");
    }

    public void setLanguageSkills(String value){
        put("languages", value);
    }

    public ArrayList<JobExperience> getJobExperiencesAsList(){
        String jobsText=this.getJobExperiences();
        if(jobsText==null || jobsText.trim().equals(""))
            return new ArrayList<JobExperience>();

        String[] jobs=jobsText.split(Pattern.quote(MyUtils.CUSTOM_DELIMITER));
        ArrayList<JobExperience> list = new ArrayList<JobExperience>();

        for(String job:jobs){

            //Per sicurezza...
            if(job.trim().isEmpty() || job.contains(MyUtils.CUSTOM_DELIMITER))
                continue;

            String[] tokens=job.split(Pattern.quote(MyUtils.CUSTOM_DELIMITER_2));
            JobExperience jobExperienceObj = new JobExperience();
            jobExperienceObj.setTitle(tokens[0]);
            jobExperienceObj.setOrganization(tokens[1]);
            jobExperienceObj.setCity(tokens[2]);
            jobExperienceObj.setStartDate(tokens[3]);
            jobExperienceObj.setEndDate(tokens[4]);
            jobExperienceObj.setDescription(tokens[5]);

            list.add(jobExperienceObj);
        }

        return list;
    }

    public String getJobExperiences() {
        return getString("jobExperiences");
    }

    public void setJobExperiences(String value){
        put("jobExperiences", value);
    }

    public ArrayList<Education> getEducationsAsList(){
        String educationsText=this.getEducations();
        if(educationsText==null || educationsText.trim().equals(""))
            return new ArrayList<Education>();

        String[] educations=educationsText.split(Pattern.quote(MyUtils.CUSTOM_DELIMITER));
        ArrayList<Education> list = new ArrayList<Education>();

        for(String education:educations){

            //Per sicurezza...
            if(education.trim().isEmpty() || education.contains(MyUtils.CUSTOM_DELIMITER))
                continue;

            String[] tokens=education.split(Pattern.quote(MyUtils.CUSTOM_DELIMITER_2));
            Education eduObj = new Education();
            eduObj.setTitle(tokens[0]);
            eduObj.setUniversity(tokens[1]);
            eduObj.setCity(tokens[2]);
            eduObj.setStartDate(tokens[3]);
            eduObj.setEndDate(tokens[4]);
            eduObj.setFinalGrade(tokens[5]);

            list.add(eduObj);
        }

        return list;
    }

    public String getEducations() {
        return getString("educations");
    }

    public void setEducations(String value){
        put("educations", value);
    }

    public String getProfessionalHeadline() {
        return getString("professionalHeadline");
    }

    public void setProfessionalHeadline(String value){
        put("professionalHeadline", value);
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


    public static void downloadAllUsers(final Context ctx, final OnUsersDownloadedCallback callback){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        //query.whereEqualTo("isCompany",true);
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


    public static void getFromLocalStorageAllUsers(final Context ctx, final OnUsersDownloadedCallback callback){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.fromLocalDatastore();
        //query.whereEqualTo("isCompany",false);
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


    public static  User getFromLocalStorageStudentById(String objectID){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("isCompany", false);
        try {
            User u=(User)query.get(objectID);
            return u;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static  User getFromLocalStorageCompanyById(String objectID){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("isCompany", true);
        try {
            User u=(User)query.get(objectID);
            return u;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }



    public static void searchStudentsBySkills(final Context ctx, final ArrayList<String> skills, final OnUsersDownloadedCallback callback){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("isCompany", false);
        //query.orderByDescending("updatedAt");
        // query.whereGreaterThan("updatedAt", new Date(PreferenceManager.getDefaultSharedPreferences(ctx).getLong("Users_timestamp", 0)));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(final List<ParseUser> objects, ParseException e) {
                ArrayList<User> filteredUsers= new ArrayList<User>();
                if(objects==null || objects.size()==0){
                    callback.onUsersDownloaded(filteredUsers);
                }else{
                    ArrayList<User> allUsers= new ArrayList<User>();
                    for(ParseUser pu : objects){
                        allUsers.add((User)pu);
                    }

                   if(skills.size()!=0){
                    for(User u : allUsers){
                        for(String skill : skills){
                            if(u.getSkills()!=null && u.getSkills().contains(skill)){
                                filteredUsers.add(u);
                            }
                        }
                    }
                   }else{
                       filteredUsers.addAll(allUsers);
                   }


                    callback.onUsersDownloaded((filteredUsers));
                }

            }
        });

    }


    public interface OnGetPhoto{
        void onGetPhoto(Bitmap b);
    }

    public void getPhotoAsync(final Activity activity, final OnGetPhoto onGetPhoto)  {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final Bitmap b=getPhotoSync(activity);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onGetPhoto.onGetPhoto(b);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        })).start();

    }


    public Bitmap getPhotoSync(Context context){
        try{
            ParseObject photo = (ParseObject)this.get("photoProfile");
            if(photo==null)return null;
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
            byte[]  data = query.get(photo.getObjectId()).getParseFile("photo").getData();
            if(data==null)return null;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public void updatePhotoAsync(Bitmap bitImage, SaveCallback saveCallback) {

        if(bitImage!=null) {
            ParseObject photo = new ParseObject("Photo");
            photo.put("name", "photoProfile");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitImage.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] byteArray = stream.toByteArray();
            photo.put("photo", new ParseFile(byteArray));

            if(this.has("photoProfile"))
                this.getParseObject("photoProfile").deleteInBackground();

            this.put("photoProfile", photo);
            this.saveInBackground(saveCallback);

        } else {
            if(this.has("photoProfile"))
                this.getParseObject("photoProfile").deleteInBackground();

            this.saveInBackground(saveCallback);

        }

    }

    public void updatePhotoSync(Bitmap bitImage) throws ParseException {
        ParseObject photo = new ParseObject("Photo");
        photo.put("name", "photoProfile");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitImage.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        photo.put("photo", new ParseFile(byteArray));

        if(this.has("photoProfile"))
            this.getParseObject("photoProfile").deleteInBackground();

        this.put("photoProfile", photo);
        this.save();

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
