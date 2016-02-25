package it.polito.mobilecourseproject.poliapp.noticeboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.processbutton.iml.ActionProcessButton;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.ExpandableHeightGridView;
import it.polito.mobilecourseproject.poliapp.GridViewAdapter;
import it.polito.mobilecourseproject.poliapp.ImageDetailActivity;
import it.polito.mobilecourseproject.poliapp.ImageItem;
import it.polito.mobilecourseproject.poliapp.MyUtils;
import it.polito.mobilecourseproject.poliapp.PoliApp;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.messages.ChatActivity;
import it.polito.mobilecourseproject.poliapp.model.Chat;
import it.polito.mobilecourseproject.poliapp.model.Notice;
import it.polito.mobilecourseproject.poliapp.model.User;
import it.polito.mobilecourseproject.poliapp.model.UserInfo;
import it.polito.mobilecourseproject.poliapp.profile.ProfileActivity;
import it.polito.mobilecourseproject.poliapp.utils.imagezoomcrop.GOTOConstants;
import it.polito.mobilecourseproject.poliapp.utils.imagezoomcrop.ImageCropActivity;

public class NoticeDetailActivity extends AppCompatActivity {

    private TextView description;
    private TextView categoryTextView;
    private User thisUser;














    private ExpandableHeightGridView gridView;
    private GridViewAdapter gridAdapter;
    private List<ParseObject> photos;
    final ArrayList<ImageItem> imageItems = new ArrayList<>();
    private Notice notice;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        try {
            thisUser= AccountManager.getCurrentUser();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            return;
        }


        description = (TextView)findViewById(R.id.editTextDescription);
        categoryTextView = (TextView)findViewById(R.id.categoryTextView);

        ParseQuery.getQuery("Notice").fromLocalDatastore().getInBackground(getIntent().getStringExtra("noticeId"), new GetCallback<ParseObject>() {
                public void done(ParseObject noticeRetrieved, ParseException e) {
                    if (e == null) {

                        //TODO: Gestire meglio cosa succede mentre carica

                        notice = (Notice)noticeRetrieved;



                        ((TextView)findViewById(R.id.title)).setText(notice.getTitle());

                        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setTitle(notice.getTitle());

                        category = notice.getCategory();
                        categoryTextView.setText(category);
                        ((ImageView)findViewById(R.id.category_icon)).setImageResource(MyUtils.getIconForCategory(category));
                        description.setText(notice.getDescription());

                        //TODO: ciao, qui carico nome e foto
                        final User user=notice.getPublisher();
                        ((TextView)findViewById(R.id.publisher_name)).setText(user.getFirstName() + " " + user.getLastName());
                        PoliApp.getModel().getBitmapByUser(NoticeDetailActivity.this, user, new User.OnGetPhoto() {
                            @Override
                            public void onGetPhoto(Bitmap b) {
                                if (b == null) return;
                                ((ImageView) NoticeDetailActivity.this.findViewById(R.id.imgAvatar)).setImageBitmap(b);

                            }
                        });


                        //TODO: qui mi occupo del fab send message
                        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
                        if(user.getUsername().equals(thisUser.getUsername())){
                            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                            p.setAnchorId(View.NO_ID);
                            fab.setLayoutParams(p);
                            fab.setVisibility(View.GONE);
                        }else{
                           fab.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   //APRO LA CHAT CON LO STUDENTE
                                   final List<UserInfo> components = new ArrayList<UserInfo>();
                                   components.add(new UserInfo(user.getObjectId(), user.getLastName() + " " + user.getFirstName()));
                                   components.add(new UserInfo(thisUser.getObjectId(), thisUser.getLastName() + " " + thisUser.getFirstName()));

                                   ArrayList<String> filteredUsers = new ArrayList<String>();
                                   filteredUsers.add(thisUser.getObjectId());
                                   NoticeDetailActivity.this.findViewById(R.id.loading).setVisibility(View.VISIBLE);
                                   Chat.getChatsFromRemote(filteredUsers, new Chat.OnChatsDownloaded() {
                                       @Override
                                       public void onChatsDownloaded(List<Chat> chats) {
                                           Chat chat = null;
                                           for (Chat c : Chat.getChatsFromLocal()) {
                                               ArrayList<UserInfo> userInfos = c.getChatters();
                                               if (userInfos.size() == 2) {
                                                   if (userInfos.contains(components.get(0)) && userInfos.contains(components.get(1))) {
                                                       chat = c;
                                                   }
                                               }
                                           }
                                           if (chat != null) {
                                               
                                               Intent i = new Intent(NoticeDetailActivity.this, ChatActivity.class);
                                               i.putExtra("CHAT_ID", chat.getChatID());
                                               startActivity(i);
                                           } else {
                                               chat = Chat.createChat(getApplicationContext(), "", components, "", new Date());
                                               Chat.storeChatInLocal(chat);
                                               final Chat finalChat = chat;
                                               Chat.storeChatInRemote(chat, new Chat.OnChatUploaded() {
                                                   @Override
                                                   public void onChatUploaded(boolean result) {
                                                       if (result) {
                                                           
                                                           Intent i = new Intent(NoticeDetailActivity.this, ChatActivity.class);
                                                           i.putExtra("CHAT_ID", finalChat.getChatID());
                                                           startActivity(i);
                                                       } else {
                                                           Toast.makeText(NoticeDetailActivity.this, "Network error occurred", Toast.LENGTH_LONG).show();
                                                       }
                                                       NoticeDetailActivity.this.findViewById(R.id.loading).setVisibility(View.GONE);
                                                   }
                                               });
                                           }

                                       }
                                   });

                               }
                           });


                        }

                            //TODO: qui vai all'utente se clicchi sul nome
                            findViewById(R.id.goToUser).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(notice.getPublisher().getUsername().equals(thisUser.getUsername())) {
                                        Intent i = new Intent(NoticeDetailActivity.this, ProfileActivity.class);
                                        startActivity(i);
                                    } else {
                                        Intent i = new Intent(NoticeDetailActivity.this, ProfileActivity.class);
                                        i.putExtra("userId", notice.getPublisher().getObjectId());
                                        startActivity(i);
                                    }
                                }
                            });




                    }
                }
            });


        photos = new LinkedList<>();

        gridView = (ExpandableHeightGridView) findViewById(R.id.photosGridView);
        gridAdapter = new GridViewAdapter(this, R.layout.image_grid_layout, getData());
        gridView.setAdapter(gridAdapter);
        gridView.setExpanded(true);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(NoticeDetailActivity.this, ImageDetailActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("objectId", item.getObjectId());

                //Start details activity
                startActivity(intent);
            }
        });

        findViewById(R.id.addPhotoButton).setVisibility(View.VISIBLE);

        if(!(imageItems!=null && imageItems.size()>0)){
            findViewById(R.id.card_view_pictures).setVisibility(View.GONE);
        }else{

        }







    }




    @Override
    public void onResume(){
        super.onResume();
        NoticeDetailActivity.this.findViewById(R.id.loading).setVisibility(View.GONE);
    }


    public void contactPublisher(View v) {

        /*
        findViewById(R.id.drawer_layout).requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(title.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(description.getWindowToken(), 0);

        if(title.getText().toString().trim().equals("")){
            Snackbar.make(findViewById(R.id.drawer_layout), "Title cannot be blank.", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(description.getText().toString().trim().equals("")){
            Snackbar.make(findViewById(R.id.drawer_layout), "Description cannot be blank.", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(categoriesSelected.isEmpty()){
            Snackbar.make(findViewById(R.id.parentView), "Please choose a category.", Snackbar.LENGTH_LONG).show();
            return;
        }

        notice.setTitle(title.getText().toString().trim());
        notice.setDescription(description.getText().toString().trim());
        notice.setCategory(categoriesSelected.get(0));

        notice.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Snackbar.make(findViewById(R.id.drawer_layout), "No network connection.", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addNotice(v);
                        }
                    }).show();
                } else {
                    notice.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Snackbar.make(findViewById(R.id.drawer_layout), "Generic error.", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addNotice(v);
                                    }

                                }).show();
                            } else {
                                Snackbar.make(findViewById(R.id.drawer_layout), "Done :)", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        */

    }

    // Prepare some dummy data for gridview
    private ArrayList<ImageItem> getData() {

        if(photos.size()==0) {
            findViewById(R.id.noPhotosTextview).setVisibility(View.VISIBLE);
            findViewById(R.id.tapAndHoldTextview).setVisibility(View.GONE);

        } else {
            findViewById(R.id.noPhotosTextview).setVisibility(View.GONE);
        }

        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < photos.size() ; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(0, -1));
            imageItems.add(new ImageItem(bitmap, "Loading...", photos.get(i).getObjectId()));
        }

        for(int i = 0; i < photos.size() ; i++) {

            final int position = i;

            photos.get(i).getParseFile("photo").getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if(bytes!=null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageItems.get(position).setImage(bitmap);
                        imageItems.get(position).setTitle(photos.get(position).getString("name" + position));
                        gridAdapter.notifyDataSetChanged();
                    }

                }
            });
        }

        return imageItems;
    }


}


