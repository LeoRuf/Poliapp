package it.polito.mobilecourseproject.poliapp.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.AccountManager;

/**
 * Created by nicof on 05/02/2016.
 */
public class DataModel {

    private List<User> contacts;
    private List<Chat> chats;
    private Context ctx;



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











}
