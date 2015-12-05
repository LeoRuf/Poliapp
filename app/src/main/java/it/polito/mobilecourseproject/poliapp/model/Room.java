package it.polito.mobilecourseproject.poliapp.model;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nicof on 04/12/2015.
 */
public class Room{

    private String name;
    private String type;
    private LatLng location;
    private String floor;
    private int seats;
    private float markerColor;
    private int color;


    public Room(String name, String type, LatLng location, String floor, int seats) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.floor = floor;
        this.seats = seats;


       if(type.equals("Classroom")){
            markerColor = BitmapDescriptorFactory.HUE_BLUE;
            color= Color.BLUE;
       }else if(type.equals("Bar")){
           markerColor = BitmapDescriptorFactory.HUE_YELLOW;
           color= Color.YELLOW;
       }else if(type.equals("Library")){
           markerColor = BitmapDescriptorFactory.HUE_ORANGE;
           color= Color.argb(255,255,165,0);
       }else if(type.equals("Laboratory")){
           markerColor = BitmapDescriptorFactory.HUE_RED;
           color= Color.RED;
       }else if(type.equals("Computer Room")){
           markerColor = BitmapDescriptorFactory.HUE_VIOLET;
           color= Color.argb(255,138,43,226);//color= 0x7F00FF;
       }else{
           markerColor = BitmapDescriptorFactory.HUE_GREEN;
           color= Color.GREEN;
       }
    }

    public Room(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getString("name").trim(),
                jsonObject.getString("type").trim(),
                new LatLng(jsonObject.getDouble("latitude"),jsonObject.getDouble("longitude")) ,
                jsonObject.getString("floor").trim(),
                jsonObject.getInt("seats"));
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getFloor() {
        return floor;
    }

    public int getSeats() {
        return seats;
    }


    @Override
    public String toString(){
        return name;
    }


    @Override
    public boolean equals(Object o){

        return name.equals(((Room)o).name);
    }


    public float getMarkerColor() {
        return markerColor;
    }

    public int getColor() {
        return color;
    }
}
