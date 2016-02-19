package it.polito.mobilecourseproject.poliapp.searchstudent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.PoliApp;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.messages.ChatActivity;
import it.polito.mobilecourseproject.poliapp.model.Chat;
import it.polito.mobilecourseproject.poliapp.model.User;
import it.polito.mobilecourseproject.poliapp.model.UserInfo;

public class SearchedStudentsActivity extends AppCompatActivity {

    ContactAdapter contactAdapter;
    ArrayList<User> users;
    User thisUser=null;
    ArrayList<User> filteredUser=new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        try{
            thisUser= AccountManager.getCurrentUser();
        }catch (Exception e){
            e.printStackTrace();
            this.onBackPressed();
            return;
        }



        findViewById(R.id.loadingUnder).setVisibility(View.VISIBLE);
        PoliApp.getModel().getContacts(new User.OnUsersDownloadedCallback() {
            @Override
            public void onUsersDownloaded(List<User> usrs) {
                users = new ArrayList<User>();
                users.addAll(usrs);
                sortUsers();

                ArrayList<User> others=new ArrayList<User>();
                for(User u : users){
                    if(!u.getObjectId().equals(thisUser.getObjectId())){
                        others.add(u);
                    }
                }
                users=others;


                contactAdapter = new ContactAdapter(users, SearchedStudentsActivity.this);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchedStudentsActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(contactAdapter);
                findViewById(R.id.loadingUnder).setVisibility(View.GONE);
            }
        });








    }

    public void sortUsers(){
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return (lhs.getLastName()+" "+lhs.getFirstName()).compareTo(rhs.getLastName()+" "+rhs.getFirstName());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_chat_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredUser.clear();
                if (newText == null || newText.equals("")) {
                    filteredUser.addAll(users);
                    contactAdapter.setUsers(filteredUser);
                    contactAdapter.notifyDataSetChanged();
                    return true;
                }

                for (User user : users) {
                    if ((user.getFirstName().toLowerCase() + " " + user.getLastName().toLowerCase()).contains(newText.trim().toLowerCase())) {
                        filteredUser.add(user);
                    }
                }
                contactAdapter.setUsers(filteredUser);
                contactAdapter.notifyDataSetChanged();


                return true;
            }
        });
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.onBackPressed();
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
        hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        if( getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow( getCurrentFocus().getWindowToken(), 0);
        }
    }




    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

        private List<User> users;
        private Context context;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public LinearLayout linearLayout;

            public ViewHolder(LinearLayout ll) {
                super(ll);
                linearLayout = ll;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ContactAdapter( List<User> users, Context ctx) {
            this.users = users;
            this.context=ctx;

        }

        public void setUsers(ArrayList<User> users){
            this.users = users;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_item, parent, false);

            ViewHolder vh = new ViewHolder(ll);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final User user= users.get(position);

            ((TextView)holder.linearLayout.findViewById(R.id.nameV)).setText(user.getLastName()+" "+user.getFirstName());

            //((ImageView)holder.linearLayout.findViewById(R.id.imgAvatar)).setImageBitmap();

             holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final List<UserInfo> components=new ArrayList<UserInfo>();
                    components.add(new UserInfo(user.getObjectId(),user.getLastName()+" "+user.getFirstName()));
                    components.add(new UserInfo(thisUser.getObjectId(), thisUser.getLastName() + " " + thisUser.getFirstName()));


                    ArrayList<String> filteredUsers=new ArrayList<String>();
                    filteredUsers.add(thisUser.getObjectId());
                    SearchedStudentsActivity.this.findViewById(R.id.loading).setVisibility(View.VISIBLE);
                    Chat.getChatsFromRemote(filteredUsers, new Chat.OnChatsDownloaded() {
                        @Override
                        public void onChatsDownloaded(List<Chat> chats) {
                            Chat chat=null;
                            for(Chat c: Chat.getChatsFromLocal()) {
                                ArrayList<UserInfo> userInfos = c.getChatters();
                                if (userInfos.size() == 2) {
                                    if (userInfos.contains(components.get(0)) && userInfos.contains(components.get(1))) {
                                        chat = c;
                                    }
                                }
                            }
                            if(chat!=null) {
                                SearchedStudentsActivity.this.onBackPressed();
                                Intent i = new Intent(SearchedStudentsActivity.this, ChatActivity.class);
                                i.putExtra("CHAT_ID", chat.getChatID());
                                startActivity(i);
                            }else{
                                chat=Chat.createChat(getApplicationContext(),"",components,"",new Date());
                                Chat.storeChatInLocal(chat);
                                final Chat finalChat = chat;
                                Chat.storeChatInRemote(chat, new Chat.OnChatUploaded() {
                                    @Override
                                    public void onChatUploaded(boolean result) {
                                        if (result) {
                                            SearchedStudentsActivity.this.onBackPressed();
                                            Intent i = new Intent(SearchedStudentsActivity.this, ChatActivity.class);
                                            i.putExtra("CHAT_ID", finalChat.getChatID());
                                            startActivity(i);
                                        } else {
                                            Toast.makeText(SearchedStudentsActivity.this, "Network error occurred", Toast.LENGTH_LONG).show();
                                        }
                                        SearchedStudentsActivity.this.findViewById(R.id.loading).setVisibility(View.GONE);
                                    }
                                });
                            }

                        }
                    });

                }
            });




        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {

            return users.size();
        }

    }


}
