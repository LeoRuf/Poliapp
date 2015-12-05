package it.polito.mobilecourseproject.poliapp;



import it.polito.mobilecourseproject.poliapp.model.Room;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream; 
import java.util.ArrayList; 
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject; 
import android.content.Context;




/*
 * Manages json operations
 * 
 * 
 */ 

public class JSONManager {
	
	Context ctx;
	JSONObject jsonRootObject=null ;
	
	
	
	 
	
    
    
    
    public JSONManager(Context ctx){
    	this.ctx=ctx; 
    }
    
    
    
    //loads from file the string json content e converts to jsonRootObject
    private void loadFromFile(String file) throws JSONException{
    	if(jsonRootObject==null){
    	String jsonContent=JSONManager.getJsonFile(ctx,file);
    	//getting root object
    	 jsonRootObject = new JSONObject(jsonContent);
    	}
    }
    
     
	
	/*
	 * Returns the list of lectures
	 * 
	 */
	public List<Room> jsonTORooms() {
		JSONArray jsonRoomsArray=null;
		try {
			loadFromFile("rooms.json");
			jsonRoomsArray = jsonRootObject.getJSONArray("rooms");
		}catch (Exception e){
			return  null;
		}
		
		List<Room> allRooms = new ArrayList<Room>();
		for (int i=0; i<jsonRoomsArray.length(); i++) {
			try {
				JSONObject jsonObject = jsonRoomsArray.getJSONObject(i);
				Room room = new Room(jsonObject);
				allRooms.add(room);
			}catch (Exception e){
				e.printStackTrace();

			}
	     }
		
		return allRooms;
	}
	
	


	




	
	 
 
	
	
	
	//Returns a string from a text file in assets folder
    public static String getJsonFile(Context ctx, String nomeFile) {
			String JsonString = "";
			
			InputStream inputStream = null;
			try {
				inputStream = ctx.getAssets().open(nomeFile);
			} catch (IOException e1) {
				e1.printStackTrace();
				return JsonString;
			}
		     
		     ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		     
		
		try {
			int i = inputStream.read();
			while (i != -1) {
				byteArray.write(i);
				i = inputStream.read();
			}
			inputStream.close();
			JsonString=byteArray.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

			return JsonString;
	}

	
	
	
}
