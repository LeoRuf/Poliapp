package it.polito.mobilecourseproject.poliapp.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.PoliApp;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.Chat;
import it.polito.mobilecourseproject.poliapp.model.User;
import it.polito.mobilecourseproject.poliapp.model.UserInfo;

public class AddGroupActivity extends AppCompatActivity {

    ContactAdapter contactAdapter;
    ArrayList<User> users;
    User thisUser;
    ArrayList<User> filteredUser=new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
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
                for(User u : usrs){
                    if(!u.isCompany())
                        users.add(u);
                }
                sortUsers();
                ArrayList<User> others=new ArrayList<User>();
                for(User u : users){
                  if(!u.getObjectId().equals(thisUser.getObjectId())){
                      others.add(u);
                  }
                }
                users=others;

                contactAdapter = new ContactAdapter(users, AddGroupActivity.this);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddGroupActivity.this);
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
                return (lhs.getLastName().toLowerCase()).compareTo(rhs.getLastName().toLowerCase());
            }
        });
    }


    public void goToChat(View v) {
        final AppCompatEditText grNameText = (AppCompatEditText) findViewById(R.id.groupName);
        if (grNameText.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Group name not valid", Toast.LENGTH_LONG).show();
            return;
        }

        Map<User, Boolean> selections = contactAdapter.getSelections();
        final ArrayList<UserInfo> components = new ArrayList<UserInfo>();
        for (User u : selections.keySet()) {
            if (selections.get(u) == true) {
                components.add(new UserInfo(u.getObjectId(), u.getLastName() + " " + u.getFirstName()));
            }
        }
        if (components.size() == 0) {
            Toast.makeText(this, "No contact selected", Toast.LENGTH_LONG).show();
            return;
        }
        components.add(new UserInfo(thisUser.getObjectId(),thisUser.getLastName() + " " + thisUser.getFirstName()));

        ArrayList<String> filteredUsers = new ArrayList<String>();
        filteredUsers.add(thisUser.getObjectId());
        AddGroupActivity.this.findViewById(R.id.loading).setVisibility(View.VISIBLE);
        Chat.getChatsFromRemote(filteredUsers, new Chat.OnChatsDownloaded() {
            @Override
            public void onChatsDownloaded(List<Chat> chats) {
                Chat chat = null;
                for (Chat c : Chat.getChatsFromLocal()) {
                    if (c.getDerivedTitle().equals(grNameText.getText().toString().trim())) {
                        chat = c;
                    }
                }

                if (chat != null) {
                    AddGroupActivity.this.onBackPressed();
                    Intent i = new Intent(AddGroupActivity.this, ChatActivity.class);
                    i.putExtra("CHAT_ID", chat.getChatID());
                    startActivity(i);
                } else {
                    chat = Chat.createChat(getApplicationContext(), grNameText.getText().toString(), components, "", new Date());
                    Chat.storeChatInLocal(chat);
                    final Chat finalChat = chat;
                    Chat.storeChatInRemote(chat, new Chat.OnChatUploaded() {
                        @Override
                        public void onChatUploaded(boolean result) {
                            if (result) {
                                AddGroupActivity.this.onBackPressed();
                                Intent i = new Intent(AddGroupActivity.this, ChatActivity.class);
                                i.putExtra("CHAT_ID", finalChat.getChatID());
                                startActivity(i);
                            } else {
                                Toast.makeText(AddGroupActivity.this, "Network error occurred", Toast.LENGTH_LONG).show();
                            }
                            AddGroupActivity.this.findViewById(R.id.loading).setVisibility(View.GONE);
                        }
                    });
                }


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
                    contactAdapter.setFilteredUsers(filteredUser);
                    contactAdapter.notifyDataSetChanged();
                    return true;
                }

                for (User user : users) {
                    if ((user.getFirstName().toLowerCase() + " " + user.getLastName().toLowerCase()).contains(newText.trim().toLowerCase())) {
                        filteredUser.add(user);
                    }
                }
                contactAdapter.setFilteredUsers(filteredUser);
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

        private Map<User,Boolean> selections=new HashMap<User,Boolean>();
        private List<User> users=new ArrayList<>();;
        private List<User> filteredUsers=new ArrayList<>();
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
            this.users.addAll(users);
            this.filteredUsers.addAll(users);
            this.context=ctx;

        }

        public Map<User, Boolean> getSelections(){
            return selections;
        }

        public void setFilteredUsers(ArrayList<User> users){
            this.filteredUsers.clear();
            this.filteredUsers.addAll(users);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selectable_contact_item, parent, false);

            ViewHolder vh = new ViewHolder(ll);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final User user= users.get(position);

            if(filteredUsers.contains(user)){
                holder.linearLayout.findViewById(R.id.hideable).setVisibility(View.VISIBLE);
            }else{
                holder.linearLayout.findViewById(R.id.hideable).setVisibility(View.GONE);
                return;
            }


            ((TextView)holder.linearLayout.findViewById(R.id.nameV)).setText(user.getLastName() + " " + user.getFirstName());

            Bitmap b= PoliApp.getModel().getBitmapByUser(AddGroupActivity.this, user,this);
            if(b!=null){
                ((CircleImageView) holder.linearLayout.findViewById(R.id.imgAvatar)).setImageBitmap(b);
            }else{
                ((CircleImageView) holder.linearLayout.findViewById(R.id.imgAvatar)).setImageResource(R.drawable.default_avatar);
            }
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     //
                 }
             });


            final AppCompatCheckBox checkBox=(AppCompatCheckBox)holder.linearLayout.findViewById(R.id.check);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selections.put(user, isChecked);
                }
            });
            if(selections.get(user)==null || selections.get(user)==false){
                checkBox.setChecked(false);
            }else{
                checkBox.setChecked(true);
            }


            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());

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
