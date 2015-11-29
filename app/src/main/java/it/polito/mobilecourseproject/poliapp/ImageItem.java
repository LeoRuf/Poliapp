package it.polito.mobilecourseproject.poliapp;

import android.graphics.Bitmap;

public class ImageItem {
    private Bitmap image;
    private String title;
    private String objectId;

    public ImageItem(Bitmap image, String title, String objectId) {
        super();
        this.image = image;
        this.title = title;
        this.objectId = objectId;

    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

}