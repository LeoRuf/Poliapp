package it.polito.mobilecourseproject.poliapp.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mobilecourseproject.poliapp.AccountManager;

/**
 * Created by nicof on 05/02/2016.
 */
public class DataModel {

    private List<User> contacts;
    private List<Chat> chats;
    private Bitmap profileBitmap;
    private Map<String,Bitmap> usersBitmaps=new HashMap<String,Bitmap>();
    private Context ctx;



    public void flush(){
        contacts=null;
        chats=null;
        profileBitmap=null;
    }


    public DataModel(Context ctx ){

        this.ctx=ctx;

        User.downloadAllUserStudents(ctx, new User.OnUsersDownloadedCallback() {
            @Override
            public void onUsersDownloaded(List<User> users) {
                contacts = users;
            }
        });



        try{
            ArrayList<String> userdIs=new ArrayList<>();
            String userID= AccountManager.getCurrentUser().getObjectId();
            userdIs.add(userID);
            Chat.getChatsFromRemote(userdIs,new Chat.OnChatsDownloaded() {
                @Override
                public void onChatsDownloaded(List<Chat> remoteChats) {
                    chats=remoteChats;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }



    }




    public  void getContacts(final User.OnUsersDownloadedCallback callback){
        if(false && contacts!=null && contacts.size()!=0){
            //get da ram
            callback.onUsersDownloaded(contacts);
        }else{
            User.getFromLocalStorageAllUserStudents(ctx, new User.OnUsersDownloadedCallback() {
                @Override
                public void onUsersDownloaded(List<User> users) {
                    if (users.size() != 0) {
                        //get da locale
                        contacts = users;
                        callback.onUsersDownloaded(contacts);
                    } else {
                        //scarico dalla rete
                        User.downloadAllUserStudents(ctx, new User.OnUsersDownloadedCallback() {
                            @Override
                            public void onUsersDownloaded(List<User> users) {
                                contacts = users;
                                callback.onUsersDownloaded(contacts);
                            }
                        });
                    }


                }
            });



        }

    }


    public  void getChats(final Chat.OnChatsDownloaded callback){
        if(false && chats!=null && chats.size()!=0){
            //get da ram
            callback.onChatsDownloaded(chats);
        }else{
            List<Chat> localChats =Chat.getChatsFromLocal();
             if(localChats!=null && localChats.size()!=0){
                  chats=localChats;
                 callback.onChatsDownloaded(chats);
             }else{
                 try {
                     ArrayList<String> userdIs = new ArrayList<>();
                     String userID = AccountManager.getCurrentUser().getObjectId();
                     userdIs.add(userID);
                     Chat.getChatsFromRemote(userdIs,new Chat.OnChatsDownloaded() {
                         @Override
                         public void onChatsDownloaded(List<Chat> remoteChats) {
                             chats = remoteChats;
                             callback.onChatsDownloaded(chats);
                         }
                     });
                 }catch (Exception e){
                     e.printStackTrace();
                 }
             }




        }

    }



    public void getProfileBitmap(Activity act,User thisUser, final User.OnGetPhoto onGetPhoto){

        if(profileBitmap!=null){
            onGetPhoto.onGetPhoto(profileBitmap);
            return;
        }
        thisUser.getPhotoAsync(act, new User.OnGetPhoto() {
            @Override
            public void onGetPhoto(Bitmap b) {
                profileBitmap=b;
                onGetPhoto.onGetPhoto(profileBitmap);
            }
        });

    }


    public Bitmap getBitmapByUser(Activity act, final User user,final RecyclerView.Adapter adapter){
        Bitmap b=usersBitmaps.get(user.getObjectId());
        if(b==null){
            user.getPhotoAsync(act, new User.OnGetPhoto() {
                @Override
                public void onGetPhoto(Bitmap b) {
                    if(b!=null){
                        usersBitmaps.put(user.getObjectId(), b);
                        try{
                            adapter.notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                }

                }
            });
            return null;
        }else{
            return b;
        }

    }








}
