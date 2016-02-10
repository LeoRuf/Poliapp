package it.polito.mobilecourseproject.poliapp.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.Chat;
import it.polito.mobilecourseproject.poliapp.model.Message;
import it.polito.mobilecourseproject.poliapp.model.User;
import it.polito.mobilecourseproject.poliapp.model.UserInfo;

public class ChatActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener{
    private Chat chat;
    String userId;
    User thisUser=null;
    EmojiconEditText inputText;

    boolean emoticonsShown=false;

    ArrayList<Message> messages;
    MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        try {
            thisUser=AccountManager.getCurrentUser();
            userId=thisUser.getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
            this.finish();
            return;
        }




        String chatID=getIntent().getStringExtra("CHAT_ID");
        if(chatID==null){
            this.onBackPressed();
            return;
        }


        //get chat from local store
        chat= Chat.getChatFromLocal(chatID);
        if(chat==null){
            this.onBackPressed();
            return;
        }
        getSupportActionBar().setTitle(chat.getDerivedTitle());

        String s="";
        for(UserInfo ui : chat.getChatters()){
            String chatter="";
            if(ui.userID.equals(userId)){
                chatter="You";
            }else{
                chatter=ui.name;
            }

            if(s.equals(""))s=chatter;
            else s=s+", "+chatter;
        }
        TextView componentsView=(TextView)findViewById(R.id.componentsText);
        componentsView.setSelected(true);
        if(!s.equals("") && chat.isGroup()){
            componentsView.setText(s);
            componentsView.setVisibility(View.VISIBLE);
        }





        chat.setSeen(this, true);
        Chat.storeChatInLocal(chat);
        MessageService.hideNotification(getApplicationContext(), chat);




        inputText=((EmojiconEditText)findViewById(R.id.textSend));
        inputText.requestFocus();
        inputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.emoticonsImage).setBackgroundResource(R.drawable.emoticon);
                emoticonsShown=false;
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.emojicons).setVisibility(View.GONE);
                    }
                },100);
            }
        });



        findViewById(R.id.emojicons).setVisibility(View.GONE);
        findViewById(R.id.emoticons).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emoticonsShown){
                    findViewById(R.id.emoticonsImage).setBackgroundResource(R.drawable.emoticon);
                    showSoftKeyboard();
                    emoticonsShown=false;
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.emojicons).setVisibility(View.GONE);
                        }
                    }, 100);
                }else{
                    findViewById(R.id.emoticonsImage).setBackgroundResource(R.drawable.keyboard);
                    hideSoftKeyboard();
                    emoticonsShown=true;
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.emojicons).setVisibility(View.VISIBLE);
                        }
                    }, 180);
                }
            }
        });








        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String text=((EmojiconEditText)findViewById(R.id.textSend)).getText().toString().trim();
                if(text.equals(""))return;

                UserInfo thisUserInfo=new UserInfo(thisUser.getObjectId(),thisUser.getLastName()+" "+thisUser.getFirstName());

                Message m = Message.createMessage(chat.getChatID(),thisUserInfo,text,new Date());
                Message.storeOrReplaceMessageInLocal(getApplicationContext(), m);
                chat.setLastMessageDate(getApplicationContext(), m.getPureDate());
                Chat.storeChatInLocal(chat);

               inputText.setText("");

                messages.add(m);
                loadMessagesFromMemory();



                Intent intent=new Intent(getApplicationContext(),MessageService.class);
                intent.putExtra(MessageService.SERVICE_INTENT_MESSAGE_ID,m.getMessageID());
                 startService(intent);


            }
        });
    }


    public void hideSoftKeyboard() {
        if( getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    public void showSoftKeyboard() {
        inputText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(inputText, 0);
            }
        },50);
    }


    private BroadcastReceiver broadcastReceiver;
    @Override
    public void onResume(){
        super.onResume();
        loadMessages();

        try{
            IntentFilter intentFilter = new IntentFilter(MessageService.SERVICE_INTENT_BROADCAST);
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ChatActivity.this.loadMessages();
                    MessageService.hideNotification(getApplicationContext(), chat);
                    chat.setSeen(ChatActivity.this, true);
                    Chat.storeChatInLocal(chat);
                }
            };
            //registering our receiver
            this.registerReceiver(broadcastReceiver, intentFilter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onPause() {
        super.onPause();

        //unregister our receiver
        try{
            this.unregisterReceiver(this.broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


   public void loadMessagesFromMemory(){
       Message.reverseSort(messages);
       //ArrayList<Message> ms =new ArrayList<Message>();
       /*for(int i = messages.size()-1; i>-1 ; i--){
           ms.add(messages.get(i));
       }*/
       //messages=ms;
       messageAdapter.setMessages(messages);
       messageAdapter.notifyDataSetChanged();
   }


    public void loadMessages(){

         messages=Message.getMessagesFromLocalByChatID(getApplicationContext(), chat.getChatID());

        Message.reverseSort(messages);
        /*ArrayList<Message> ms =new ArrayList<Message>();
        for(int i = messages.size()-1; i>-1 ; i--){
            ms.add(messages.get(i));
        }
        messages=ms;*/

        messageAdapter=new MessageAdapter(messages,this);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.add_chat_menu, menu);


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
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(inputText);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(inputText, emojicon);
    }


    @Override
    public void onBackPressed(){
        if(emoticonsShown){
            findViewById(R.id.emoticonsImage).setBackgroundResource(R.drawable.emoticon);
            emoticonsShown=false;
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.emojicons).setVisibility(View.GONE);
                }
            }, 100);
        }else{
            hideSoftKeyboard();
            super.onBackPressed();
        }
    }

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        private List<Message> messages;
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
        public MessageAdapter(List<Message> messages, Context ctx) {
            this.messages = messages;
            this.context=ctx;

        }

        public void setMessages(ArrayList<Message> messages){
            this.messages = messages;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item, parent, false);

            ViewHolder vh = new ViewHolder(ll);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final Message message= messages.get(position);
            
            
            //my message
            if(message.getUser().userID!=null && message.getUser().userID.equals(userId)){
                (holder.linearLayout.findViewById(R.id.card_view_me)).setVisibility(View.VISIBLE);
                (holder.linearLayout.findViewById(R.id.card_view_others)).setVisibility(View.GONE);

                ((EmojiconTextView)holder.linearLayout.findViewById(R.id.me_messageV)).setText(message.getText());
                ((TextView)holder.linearLayout.findViewById(R.id.me_timeV)).setText(message.getMessageDate());


                if(!message.isSent()){
                    ((ImageView)holder.linearLayout.findViewById(R.id.time)).setImageResource(R.drawable.ic_action_time);
                }else{
                    ((ImageView)holder.linearLayout.findViewById(R.id.time)).setImageResource(R.drawable.sent);

                }


                
            }else{//other message
                (holder.linearLayout.findViewById(R.id.card_view_others)).setVisibility(View.VISIBLE);
                (holder.linearLayout.findViewById(R.id.card_view_me)).setVisibility(View.GONE);


                ((TextView)holder.linearLayout.findViewById(R.id.otherNameV)).setText(message.getUser().name);
                ((EmojiconTextView)holder.linearLayout.findViewById(R.id.otherMessageV)).setText(message.getText());
                ((TextView)holder.linearLayout.findViewById(R.id.otherTimeV)).setText(message.getMessageDate());
                
                
            }
 
            
/*
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });*/




        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() { 
            return messages.size();
        }

    }


}
