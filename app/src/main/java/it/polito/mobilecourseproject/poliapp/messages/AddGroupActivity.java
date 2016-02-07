package it.polito.mobilecourseproject.poliapp.messages;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.User;

public class AddGroupActivity extends AppCompatActivity {

    ContactAdapter contactAdapter;
    ArrayList<User> users;
    ArrayList<User> filteredUser=new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);






        users =new ArrayList<User>();
        users.add(User.createUser("Enrico", "Di Fazio", "Polito"));
        users.add(User.createUser("Ciccio", "Pasticcio", "Polito"));
        users.add(User.createUser("Luca", "Angileri", "Polito"));
        users.add(User.createUser("Pippo", "Pippino", "Polito"));
        users.add(User.createUser("Matteo", "Mommino", "Polito"));
        users.add(User.createUser("Mario", "Rossi", "Polito"));
        users.add(User.createUser("Enrico", "Rosi", "Polito"));
        users.add(User.createUser("Ciccio", "Poso", "Polito"));
        users.add(User.createUser("Luca", "Peso", "Polito"));
        users.add(User.createUser("Pippo", "Rasi", "Polito"));
        users.add(User.createUser("Matteo", "Zorro", "Polito"));
        users.add(User.createUser("Mario", "Zardy", "Polito"));
        sortUsers();

        contactAdapter=new ContactAdapter(users,this);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(contactAdapter);




    }

    public void sortUsers(){
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return (lhs.getLastName()+" "+lhs.getFirstName()).compareTo(rhs.getLastName()+" "+rhs.getFirstName());
            }
        });
    }



    public void goToChat(View v){
        AppCompatEditText grNameText=(AppCompatEditText)findViewById(R.id.groupName);
        if(grNameText.getText().toString().trim().equals("")){
            Toast.makeText(this,"Group name not valid",Toast.LENGTH_LONG).show();
            return;
        }

        Map<User,Boolean> selections = contactAdapter.getSelections();
        ArrayList<User> components= new ArrayList<User>();
        for(User u : selections.keySet()){
            if(selections.get(u)==true){
                components.add(u);
            }
        }
        if(components.size()==0){
            Toast.makeText(this,"No contact selected",Toast.LENGTH_LONG).show();
            return;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.add_chat_menu, menu);

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

        public Map<User, Boolean> getSelections(){
            return selections;
        }

        public void setUsers(ArrayList<User> users){
            this.users = users;
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

            ((TextView)holder.linearLayout.findViewById(R.id.nameV)).setText(user.getLastName() + " " + user.getFirstName());

            //((ImageView)holder.linearLayout.findViewById(R.id.imgAvatar)).setImageBitmap();

             holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     //
                 }
             });


            final AppCompatCheckBox checkBox=(AppCompatCheckBox)holder.linearLayout.findViewById(R.id.check);
            if(selections.get(user)==null || selections.get(user)==false){
                checkBox.setChecked(false);
            }else{
                checkBox.setChecked(true);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selections.put(user, isChecked);
                }
            });

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
