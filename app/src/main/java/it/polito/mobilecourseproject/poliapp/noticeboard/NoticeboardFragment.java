package it.polito.mobilecourseproject.poliapp.noticeboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.ExternalIntents;
import it.polito.mobilecourseproject.poliapp.MainActivity;
import it.polito.mobilecourseproject.poliapp.MyUtils;
import it.polito.mobilecourseproject.poliapp.TimeManager;
import it.polito.mobilecourseproject.poliapp.model.JobOffer;
import it.polito.mobilecourseproject.poliapp.model.Notice;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.User;
import it.polito.mobilecourseproject.poliapp.profile.ProfileActivity;


public class NoticeboardFragment extends android.support.v4.app.Fragment   {

    private OnFragmentInteractionListener mListener;
    private List<String> categoriesFiltered;
    private List<String> categoriesToBeFiltered;
    private NoticesAdapter noticesAdapter;
    private User currentUser = null;
    CoordinatorLayout.Behavior behavior;
    SwipeRefreshLayout swypeRefreshLayout;
    int scrollFlags;


    public NoticeboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        categoriesToBeFiltered = new LinkedList<>();
        categoriesFiltered = new LinkedList<>();
        noticesAdapter = new NoticesAdapter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_noticeboard, container, false);

        swypeRefreshLayout = ((SwipeRefreshLayout) fragmentView.findViewById(R.id.swipeRefreshLayout));

        RecyclerView recyclerView = (RecyclerView) fragmentView.findViewById(R.id.itemsRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(noticesAdapter);

        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNoticeActivity.class);
                startActivity(intent);
            }
        });

        search();

        swypeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ParseQuery<Notice> query = ParseQuery.getQuery("Notice");
                query.setLimit(1000);
                query.orderByDescending("updatedAt");
                query.whereGreaterThan("updatedAt",new Date(PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong("Noticeboard_timestamp", 0)));
                query.findInBackground(new FindCallback<Notice>() {
                    @Override
                    public void done(final List<Notice> objects, ParseException e) {
                        if(objects!=null && objects.size()!=0) {
                            ParseObject.pinAllInBackground(objects, new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putLong("Noticeboard_timestamp", objects.get(0).getUpdatedAt().getTime()).commit();
                                }
                            });
                        }
                        search();

                    }
                });

            }
        });

        swypeRefreshLayout.setColorSchemeColors(R.color.myAccentColor);

        return fragmentView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        myOnAttach(getActivity());

        try {
            currentUser = AccountManager.getCurrentUser();
        } catch (Exception e) {
            e.printStackTrace();
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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Noticeboard");


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
        fab.setVisibility(View.VISIBLE);



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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.noticeboard_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchedText) {

                String[] stringWords = searchedText.split(" ");
                final List<String> words = new ArrayList<String>();
                for (String s : stringWords) {
                    words.add(s.toLowerCase().trim());
                }


                getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
                ParseQuery<Notice> query = ParseQuery.getQuery("Notice");
                query.fromLocalDatastore();
                query.orderByDescending("createdAt");
                query.setLimit(1000);
                query.findInBackground(new FindCallback<Notice>() {
                    @Override
                    public void done(List<Notice> objects, ParseException e) {
                        if (objects == null || objects.size() == 0) {
                            getActivity().findViewById(R.id.itemsRecyclerView).setVisibility(View.GONE);
                            getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
                            getActivity().findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                            noticesAdapter.setNotices(new ArrayList<Notice>());
                            noticesAdapter.notifyDataSetChanged();
                            swypeRefreshLayout.setRefreshing(false);
                        } else {
                            List<Notice> filteredObjects = new ArrayList<Notice>();
                            for (Notice jo : objects) {
                                for (String w : words) {
                                    if (jo.getDescription().toLowerCase().contains(w) || jo.getTitle().toLowerCase().contains(w) || jo.getCategory().toLowerCase().contains(w)) {
                                        filteredObjects.add(jo);
                                        break;
                                    }
                                }
                            }
                            getActivity().findViewById(R.id.itemsRecyclerView).setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
                            getActivity().findViewById(R.id.empty_view).setVisibility(View.GONE);
                            noticesAdapter.setNotices(filteredObjects);
                            noticesAdapter.notifyDataSetChanged();
                            swypeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.filter:

                categoriesToBeFiltered.clear();
                categoriesToBeFiltered.addAll(categoriesFiltered);

                final Dialog dialog = new Dialog(getActivity(),R.style.CustomDialog);
                dialog.setContentView(R.layout.dialog_categories);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setTitle("Filter categories");
                dialog.setCancelable(true);
                SearchView searchView= (SearchView)dialog.findViewById(R.id.searchView);
                //TODO:per ENRICO: CUMMATTO CU TIA! TOLGO IL SEARCH DELLE CATEGORIE
                searchView.setVisibility(View.GONE);
                /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });*/

                RecyclerView recyclerView = (RecyclerView)dialog.findViewById(R.id.recyclerView);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new CategoriesAdapter(categoriesToBeFiltered, false, getActivity()));

                Button okButton = (Button)dialog.findViewById(R.id.ok_button);
                Button cancelButton = (Button)dialog.findViewById(R.id.cancel_button);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        categoriesFiltered.clear();
                        categoriesFiltered.addAll(categoriesToBeFiltered);
                        search();
                        dialog.dismiss();
                        if(categoriesFiltered.size()==0){
                            NoticeboardFragment.this.getView().findViewById(R.id.filterText).setVisibility(View.GONE);
                        }else{
                            String string="FILTER: ";
                                for(String s :categoriesFiltered){
                                    string=string +s+ ", ";
                                }
                            string=string.substring(0,string.length()-2);
                            ((TextView) NoticeboardFragment.this.getView().findViewById(R.id.filterText)).setText(string);
                            ((TextView) NoticeboardFragment.this.getView().findViewById(R.id.filterText)).setSelected(true);
                            NoticeboardFragment.this.getView().findViewById(R.id.filterText).setVisibility(View.VISIBLE);
                        }

                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //recyclerView..setTextFilterEnabled(true);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                //dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void search(){
        ParseQuery<Notice> query = ParseQuery.getQuery("Notice");
        query.fromLocalDatastore();
        query.orderByDescending("createdAt");
        query.setLimit(1000);
        query.findInBackground(new FindCallback<Notice>() {
            @Override
            public void done(List<Notice> objects, ParseException e) {

                if(objects == null || objects.isEmpty()) {
                    getActivity().findViewById(R.id.itemsRecyclerView).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.empty_view).setVisibility(View.GONE);

                    ParseQuery<Notice> query = ParseQuery.getQuery("Notice");
                    query.setLimit(1000);
                    query.orderByDescending("updatedAt");
                    query.whereGreaterThan("updatedAt",new Date(PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong("Noticeboard_timestamp", 0)));
                    query.findInBackground(new FindCallback<Notice>() {
                        @Override
                        public void done(final List<Notice> objects, ParseException e) {
                            if(objects != null && objects.size()!=0) {
                                ParseObject.pinAllInBackground(objects, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putLong("Noticeboard_timestamp", objects.get(0).getUpdatedAt().getTime()).commit();
                                        search();
                                    }
                                });
                            } else {
                                getActivity().findViewById(R.id.itemsRecyclerView).setVisibility(View.GONE);
                                getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
                                getActivity().findViewById(R.id.empty_view).setVisibility(View.VISIBLE);

                            }
                        }
                    });

                } else {

                    getActivity().findViewById(R.id.itemsRecyclerView).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.empty_view).setVisibility(View.GONE);

                    noticesAdapter.setNotices(objects);
                    noticesAdapter.notifyDataSetChanged();
                    swypeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }



    public class NoticesAdapter extends RecyclerView.Adapter<NoticesAdapter.ViewHolder> {
        private List<Notice> notices;

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

        public void setNotices(List<Notice> myDataset) {
            notices = myDataset;
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public NoticesAdapter() {
            notices = new ArrayList<>();
        }

        // Create new views (invoked by the layout manager)
        @Override
        public NoticesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            LinearLayout v = (LinearLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.noticeboard_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            final Notice notice = notices.get(position);



            if(categoriesFiltered.size()!=0 && !categoriesFiltered.contains(notice.getCategory())){
                holder.linearLayout.findViewById(R.id.card_view).setVisibility(View.GONE);
                return;
            }else{
                holder.linearLayout.findViewById(R.id.card_view).setVisibility(View.VISIBLE);
            }



            ((TextView)holder.linearLayout.findViewById(R.id.title)).setText(notice.getTitle());
            ((TextView)holder.linearLayout.findViewById(R.id.description)).setText(notice.getDescription());
            ((TextView)holder.linearLayout.findViewById(R.id.time_text)).setText(TimeManager.getFormattedTimestamp(notice.getCreatedAt(), "Published"));
            ((ImageView)holder.linearLayout.findViewById(R.id.category_icon)).setImageResource(MyUtils.getIconForCategory(notice.getCategory()));


            try {
                ((TextView) holder.linearLayout.findViewById(R.id.publisher_name)).setText(notice.getPublisher().getFirstName() + " " + notice.getPublisher().getLastName());
                holder.linearLayout.findViewById(R.id.publisher_name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(notice.getPublisher().getUsername().equals(currentUser.getUsername())) {
                            Intent i = new Intent(getActivity(), ProfileActivity.class);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(getActivity(), ProfileActivity.class);
                            i.putExtra("userId", notice.getPublisher().getObjectId());
                            startActivity(i);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();

            }

            holder.linearLayout.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExternalIntents.share(getActivity(), "Notice published on Poliapp: " + notice.getTitle(), notice.getDescription());
                }
            });

            if(notice.getPublisher().getObjectId().equals(currentUser.getObjectId())) {

                holder.linearLayout.findViewById(R.id.edit).setVisibility(View.VISIBLE);
                holder.linearLayout.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AddNoticeActivity.class);
                        intent.putExtra("noticeId", notice.getObjectId());
                        startActivity(intent);
                    }
                });

            } else {
                holder.linearLayout.findViewById(R.id.edit).setVisibility(View.GONE);
            }


            holder.linearLayout.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NoticeDetailActivity.class);
                    intent.putExtra("noticeId", notice.getObjectId());
                    startActivity(intent);
                }
            });

            ((ImageView)holder.linearLayout.findViewById(R.id.category_icon)).setImageResource(MyUtils.getIconForCategory(notice.getCategory()));


        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {

            return notices.size();
        }
    }

}
