package it.polito.mobilecourseproject.poliapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

/**
 * Created by Enrico on 19/06/15.
 */
@ParseClassName("JobOffer")
public class JobOffer extends ParseObject {

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String value) {
        put("title", value);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String value) {
        put("description", value);
    }

    public void setUser(User value) {
        put("user", value);
    }

    public List<ParseObject> getPhotos(boolean fromLocalDatastore) throws com.parse.ParseException {
        ParseRelation<ParseObject> relation = getRelation("photos");
        ParseQuery<ParseObject> query = relation.getQuery();

        if(fromLocalDatastore)
            query = query.fromLocalDatastore();

        return query.find();
    }

    public String getCompany() {
        return getString("company");
    }

    public void setCompany(String value) {
        put("company", value);
    }

    public String getLocation() {
        return getString("location");
    }

    public void setLocation(String value) {
        put("location", value);
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
