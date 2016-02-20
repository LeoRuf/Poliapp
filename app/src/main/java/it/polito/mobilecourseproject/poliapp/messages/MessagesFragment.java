package it.polito.mobilecourseproject.poliapp.messages;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.PoliApp;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.Chat;
import it.polito.mobilecourseproject.poliapp.model.Message;
import it.polito.mobilecourseproject.poliapp.model.User;
import it.polito.mobilecourseproject.poliapp.model.UserInfo;


public class MessagesFragment extends android.support.v4.app.Fragment   {


    CoordinatorLayout.Behavior behavior;
    int scrollFlags;


    ChatAdapter chatAdapter;
    RecyclerView recyclerView;
    ArrayList<Chat> chats;
    //SwipeRefreshLayout refreshLayout;

    User thisUser;


    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_messages, container, false);

        return fragmentView;
    }


    private BroadcastReceiver broadcastReceiver;
    @Override
    public void onResume(){
        super.onResume();
        loadList();

        try{
            IntentFilter intentFilter = new IntentFilter(MessageService.SERVICE_INTENT_BROADCAST);
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    MessagesFragment.this.loadList();
                }
            };
            //registering our receiver
            getActivity().registerReceiver(broadcastReceiver, intentFilter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        //unregister our receiver
        try{
           getActivity().unregisterReceiver(this.broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void loadList(){

        chats=new ArrayList<>();
        getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.noChatAvailable).setVisibility(View.GONE);
        PoliApp.getModel().getChats(new Chat.OnChatsDownloaded() {
            @Override
            public void onChatsDownloaded(List<Chat> chats) {
                getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
                if (  chats.size() == 0) {
                    getActivity().findViewById(R.id.noChatAvailable).setVisibility(View.VISIBLE);
                    return;
                }
                Collections.sort(chats, new Comparator<Chat>() {
                    @Override
                    public int compare(Chat lhs, Chat rhs) {
                        if(lhs.getPureLastMessageDate(getActivity()).before(rhs.getPureLastMessageDate(getActivity())))
                            return 1;
                        else
                            return -1;
                    }
                });
                chatAdapter = new ChatAdapter(chats, getContext());
                recyclerView.setAdapter(chatAdapter);



                //refreshLayout.setRefreshing(false);
            }
        });





    }





    /*
 * onAttach(Context) is not called on pre API 23 versions of Android and onAttach(Activity) is deprecated
 * Use myOnAttach instead
 */
    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // myOnAttach(context);
    }

    /*
     * Deprecated on API 23
     * Use myOnAttach instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // myOnAttach(activity);
        }
    }

    /*
     * Called when the fragment attaches to the context
     */

    protected void myOnAttach(Context context) {

        android.support.v7.widget.Toolbar toolbar =(android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);


        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        //Salvo gli scrollFlags originali per poterli ripristinare nell'onDetach
        scrollFlags = params.getScrollFlags();

        params.setScrollFlags(0);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chat");

        /*
        //SE SI VUOLE MOSTRARE IL TABLAYOUT

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);


        // SE SI VUOLE MODIFICARE IL app:layout_behavior del FrameLayout


        if(behavior != null)
            return;

        FrameLayout layout =(FrameLayout) getActivity().findViewById(R.id.frame);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) layout.getLayoutParams();

        behavior = params.getBehavior();
        params.setBehavior(null);
        */

       FloatingActionButton fab =(FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity(),R.style.CustomDialog);
                dialog.setContentView(R.layout.dialog_add_chat);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setTitle("Add...");
                dialog.setCancelable(true);

                dialog.findViewById(R.id.newChat).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), AddChatActivity.class));
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.newGroup).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(),AddGroupActivity.class));
                        dialog.dismiss();
                    }
                });



                dialog.show();
            }
        });
        fab.setVisibility(View.VISIBLE);




    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myOnAttach(getActivity());


        try {
            thisUser= AccountManager.getCurrentUser();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        recyclerView=(RecyclerView)getView().findViewById(R.id.listView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);



        //moved in onResume()
        //loadList();


      /*refreshLayout=(SwipeRefreshLayout)getView().findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                       loadList();


            }
        });*/



    }

    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        android.support.v7.widget.Toolbar toolbar =(android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        //Ripristina gli scrollFlags originali
        params.setScrollFlags(scrollFlags);


        hideSoftKeyboard();





        /*
        //SE SI VUOLE MOSTRARE IL TABLAYOUT

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);


        // SE SI VUOLE MODIFICARE IL app:layout_behavior del FrameLayout

        if(behavior == null)
            return;

        FrameLayout layout =(FrameLayout) getActivity().findViewById(R.id.dashboard_content);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) layout.getLayoutParams();

        params.setBehavior(behavior);

        layout.setLayoutParams(params);

        behavior = null;
        */
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    SearchView searchView;
    SearchView.OnQueryTextListener onQueryTextListener;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.messages_menu, menu);

/*
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search");
        onQueryTextListener=new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {



                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {



                return false;
            }
        };
        searchView.setOnQueryTextListener(onQueryTextListener);

*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.filter:


/*
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_room_types);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setTitle("Room categories");
                dialog.setCancelable(true);

                RecyclerView recyclerView = (RecyclerView)dialog.findViewById(R.id.recyclerView);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
               // RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,RecyclerView.LayoutParams.WRAP_CONTENT);
               // recyclerView.setLayoutParams(layoutParams);
                recyclerView.setAdapter(new ChatAdapter(dialog, (new JSONManager(getActivity())).jsonTORoomTypes(), getActivity()));

                //dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                dialog.show();*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public boolean onBackPressed(){
        View descrLayout=getActivity().findViewById(R.id.description);
        if(descrLayout.getVisibility()==View.VISIBLE){
            descrLayout.setVisibility(View.GONE);
            return false;
        }
        return true;
    }



    public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

        private List<Chat> chats;
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
        public ChatAdapter( List<Chat> chats, Context ctx) {
            this.chats =chats;
            this.context=ctx;

        }

        public void setChats(ArrayList<Chat> chats){
            this.chats=chats;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item, parent, false);

            ViewHolder vh = new ViewHolder(ll);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {



            final Chat chat=chats.get(position);

            ((TextView)holder.linearLayout.findViewById(R.id.nameV)).setText(chat.getDerivedTitle());

            if(Message.getMessagesFromLocalByChatID(getActivity(),chat.getChatID()).size()==0){
                ((TextView)holder.linearLayout.findViewById(R.id.previeW)).setText("New chat...");

            }else{
                ((EmojiconTextView)holder.linearLayout.findViewById(R.id.previeW)).setText(chat.getPreview(getActivity()));
            }
            ((TextView)holder.linearLayout.findViewById(R.id.timeAgoView)).setText(chat.getLastMessageDate(getActivity()));


            if(!chat.getSeen(getActivity())){
                holder.linearLayout.findViewById(R.id.notReadV).setVisibility(View.VISIBLE);
                ((TextView)holder.linearLayout.findViewById(R.id.previeW)).setTypeface(null, Typeface.BOLD);
            }else{
                holder.linearLayout.findViewById(R.id.notReadV).setVisibility(View.GONE);
                ((TextView)holder.linearLayout.findViewById(R.id.previeW)).setTypeface(null, Typeface.NORMAL);
            }


            if(chat.isGroup()){
                ((CircleImageView) holder.linearLayout.findViewById(R.id.imgAvatar)).setImageResource(R.drawable.person);
            }else{
                User user=null;
                for(UserInfo ui : chat.getChatters()){
                    if(!ui.userID.equals(thisUser.getObjectId())){
                        user=User.getFromLocalStorageStudentById(ui.userID);
                    }
                }
                Bitmap b = null;
                if (user != null) {
                    b = PoliApp.getModel().getBitmapByUser(getActivity(), user,this);
                }
                if (b != null) {
                    ((CircleImageView) holder.linearLayout.findViewById(R.id.imgAvatar)).setImageBitmap(b);
                } else {
                    ((CircleImageView) holder.linearLayout.findViewById(R.id.imgAvatar)).setImageResource(R.drawable.default_avatar);
                }



            }


            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getActivity(),ChatActivity.class);
                    String chatID= chat.getChatID();
                    i.putExtra("CHAT_ID",chatID);
                    startActivity(i);
                }
            });

            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_delete);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setCancelable(true);
                    dialog.findViewById(R.id.delete_chat).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chats.remove(position);
                            notifyDataSetChanged();
                            dialog.dismiss();
                            Snackbar.make(getActivity().findViewById(R.id.drawer_layout), "Chat archived", Snackbar.LENGTH_LONG)
                                    .setAction("Cancel", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            chats.add(position, chat);
                                            notifyDataSetChanged();
                                        }
                                    }).show();
                            //((TextView)((ViewGroup)sb.getView()).findViewById(android.support.design.R.id.snackbar_text)).setBackgroundColor(0);


                        }
                    });
                    dialog.show();
                    return true;
                }
            });





        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {

            return chats.size();
        }

    }







}
