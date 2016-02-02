package it.polito.mobilecourseproject.poliapp.messages;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.MyUtils;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.TimeManager;
import it.polito.mobilecourseproject.poliapp.model.Chat;


public class MessagesFragment extends android.support.v4.app.Fragment   {


    CoordinatorLayout.Behavior behavior;
    int scrollFlags;


    ChatAdapter chatAdapter;
    RecyclerView recyclerView;




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
                //startActivity(new Intent(getActivity(),AddChatActivity.class));

                final Dialog dialog = new Dialog(getActivity());
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


        recyclerView=(RecyclerView)getView().findViewById(R.id.listView);
        ArrayList<Chat> chats=new ArrayList<>();
        try {
            chats.add(Chat.createChat("", Arrays.asList(new String[]{AccountManager.getCurrentUser().getObjectId()+";;;dfsf", "342432;;;Enri"}), "Hi", null,null));
            chats.add(Chat.createChat("", Arrays.asList(new String[]{AccountManager.getCurrentUser().getObjectId()+";;;dfsf", "34e2432;;;Luca"}), "Hey", null,null));
            chats.add(Chat.createChat("", Arrays.asList(new String[]{AccountManager.getCurrentUser().getObjectId()+";;;dfsf", "34e24e32;;;IOO"}), "laskda", null,null));
            chats.add(Chat.createChat("", Arrays.asList(new String[]{AccountManager.getCurrentUser().getObjectId()+";;;dfsf", "342432;;;Nicolò"}), ":)", null,null));
            chats.add(Chat.createChat("", Arrays.asList(new String[]{AccountManager.getCurrentUser().getObjectId()+";;;dfsf", "34e2e432;;;Mauro"}), ":(", null,null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        chatAdapter=new ChatAdapter(chats,getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);


        final SwipeRefreshLayout refreshLayout=(SwipeRefreshLayout)getView().findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                },500);

            }
        });



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


         FloatingActionButton fab =(FloatingActionButton) getActivity().findViewById(R.id.fab);
         fab.setVisibility(View.GONE);



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

            ((TextView)holder.linearLayout.findViewById(R.id.nameV)).setText(chat.getTitle());
            if(false)((TextView)holder.linearLayout.findViewById(R.id.timeAgoView)).setText(chat.getLastMessageDate());
            ((TextView)holder.linearLayout.findViewById(R.id.previeW)).setText(chat.getPreview());

           // ((ImageView)holder.linearLayout.findViewById(R.id.imgAvatar)).setImageBitmap();

            if(position==1 || position ==3){
                holder.linearLayout.findViewById(R.id.notReadV).setVisibility(View.VISIBLE);
                ((TextView)holder.linearLayout.findViewById(R.id.previeW)).setTypeface(null, Typeface.BOLD);
            }else{
                holder.linearLayout.findViewById(R.id.notReadV).setVisibility(View.GONE);
                ((TextView)holder.linearLayout.findViewById(R.id.previeW)).setTypeface(null, Typeface.NORMAL);
            }



           /* holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Marker marker : markers.keySet()) {
                        marker.remove();

                    }
                    getActivity().findViewById(R.id.description).setVisibility(View.GONE);

                    for (Room room : rooms) {
                        if (room.getType().equals(roomType)) {
                            MarkerOptions mo = new MarkerOptions().position(room.getLocation()).title(room.getName()).icon(BitmapDescriptorFactory.defaultMarker(room.getMarkerColor()));
                            markers.put(gMap.addMarker(mo), room);
                        }
                    }

                    dialog.dismiss();
                    gMap.animateCamera(cu);
                }
            });*/




        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {

            return chats.size();
        }

    }







}
