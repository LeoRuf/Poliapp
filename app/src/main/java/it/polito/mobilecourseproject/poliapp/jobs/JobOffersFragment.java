package it.polito.mobilecourseproject.poliapp.jobs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import android.view.inputmethod.InputMethodManager;
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

import de.hdodenhof.circleimageview.CircleImageView;
import it.polito.mobilecourseproject.poliapp.AccountManager;
import it.polito.mobilecourseproject.poliapp.ExternalIntents;
import it.polito.mobilecourseproject.poliapp.MyUtils;
import it.polito.mobilecourseproject.poliapp.PoliApp;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.TimeManager;
import it.polito.mobilecourseproject.poliapp.model.JobOffer;
import it.polito.mobilecourseproject.poliapp.model.Notice;
import it.polito.mobilecourseproject.poliapp.model.User;
import it.polito.mobilecourseproject.poliapp.noticeboard.AddNoticeActivity;
import it.polito.mobilecourseproject.poliapp.noticeboard.CategoriesAdapter;


public class JobOffersFragment extends android.support.v4.app.Fragment {

    private OnFragmentInteractionListener mListener;
    private JobOffersAdapter jobOffersAdapter;
    CoordinatorLayout.Behavior behavior;
    SwipeRefreshLayout swypeRefreshLayout;
    int scrollFlags;
    private User currentUser = null;



    public JobOffersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        jobOffersAdapter = new JobOffersAdapter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_joboffers, container, false);

        swypeRefreshLayout = ((SwipeRefreshLayout) fragmentView.findViewById(R.id.swipeRefreshLayout));

        RecyclerView recyclerView = (RecyclerView) fragmentView.findViewById(R.id.itemsRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(jobOffersAdapter);

        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), JobOfferDetailActivity.class);
                intent.putExtra("addMode", "true");
                startActivity(intent);
            }
        });

        search();

        swypeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ParseQuery<JobOffer> query = ParseQuery.getQuery("JobOffer");
                query.setLimit(1000);
                query.orderByDescending("updatedAt");
                query.whereGreaterThan("updatedAt",new Date(PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong("JobOffer_timestamp", 0)));
                query.findInBackground(new FindCallback<JobOffer>() {
                    @Override
                    public void done(final List<JobOffer> objects, ParseException e) {
                        if(objects!=null && objects.size()!=0) {
                            ParseObject.pinAllInBackground(objects, new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putLong("JobOffer_timestamp", objects.get(0).getUpdatedAt().getTime()).commit();

                                }
                            });
                        }

                        searchView.setIconified(true);
                        hideSoftKeyboard();
                        search();
                    }
                });

            }
        });

        //TODO: Capire come cambiare il colore
        swypeRefreshLayout.setColorSchemeColors(R.color.myAccentColor);

        return fragmentView;
    }


    public void hideSoftKeyboard() {
        if( getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow( getActivity().getCurrentFocus().getWindowToken(), 0);
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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Job offers");


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

        if(currentUser.isCompany())
            fab.setVisibility(View.VISIBLE);
        else
            fab.setVisibility(View.GONE);




    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        try {
            currentUser = AccountManager.getCurrentUser();
        } catch (Exception e) {
            e.printStackTrace();
        }

        myOnAttach(getActivity());

    }
   SearchView searchView;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_chat_menu, menu);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search");
        // Catch event on [x] button inside search view
        int searchCloseButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
                searchView.setIconified(true);
                hideSoftKeyboard();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchedText) {

                hideSoftKeyboard();
                String[] stringWords=searchedText.split(" ");
                final List<String> words=new ArrayList<String>();
                for(String s : stringWords){
                    words.add(s.toLowerCase().trim());
                }


                getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
                ParseQuery<JobOffer> query = ParseQuery.getQuery("JobOffer");
                query.fromLocalDatastore();
                query.orderByDescending("createdAt");
                query.setLimit(1000);
                query.findInBackground(new FindCallback<JobOffer>() {
                    @Override
                    public void done(List<JobOffer> objects, ParseException e) {
                        if (objects==null || objects.size()==0) {
                            getActivity().findViewById(R.id.itemsRecyclerView).setVisibility(View.GONE);
                            getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
                            getActivity().findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                            jobOffersAdapter.setJobOffers(new ArrayList<JobOffer>());
                            jobOffersAdapter.notifyDataSetChanged();
                            swypeRefreshLayout.setRefreshing(false);
                        } else {
                            List<JobOffer> filteredObjects=new ArrayList<JobOffer>();
                            for(JobOffer jo : objects){
                                for(String w : words){
                                if(jo.getDescription().toLowerCase().contains(w) || jo.getTitle().toLowerCase().contains(w) ){
                                    filteredObjects.add(jo);
                                    break;
                                }}
                            }
                            if(filteredObjects.size()==0){
                                getActivity().findViewById(R.id.itemsRecyclerView).setVisibility(View.GONE);
                                getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
                                getActivity().findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                            }else{
                                getActivity().findViewById(R.id.itemsRecyclerView).setVisibility(View.VISIBLE);
                                getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
                                getActivity().findViewById(R.id.empty_view).setVisibility(View.GONE);
                            }
                            jobOffersAdapter.setJobOffers(filteredObjects);
                            jobOffersAdapter.notifyDataSetChanged();
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void search(){
        ParseQuery<JobOffer> query = ParseQuery.getQuery("JobOffer");
        query.fromLocalDatastore();
        query.orderByDescending("createdAt");
        query.setLimit(1000);
        query.findInBackground(new FindCallback<JobOffer>() {

            @Override
            public void done(List<JobOffer> objects, ParseException e) {

                if(objects == null || objects.isEmpty()) {
                    getActivity().findViewById(R.id.itemsRecyclerView).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.empty_view).setVisibility(View.GONE);

                    ParseQuery<JobOffer> query = ParseQuery.getQuery("JobOffer");
                    query.setLimit(1000);
                    query.orderByDescending("updatedAt");
                    query.whereGreaterThan("updatedAt",new Date(PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong("JobOffer_timestamp", 0)));
                    query.findInBackground(new FindCallback<JobOffer>() {
                        @Override
                        public void done(final List<JobOffer> objects, ParseException e) {
                            if(objects != null && objects.size()!=0) {
                                ParseObject.pinAllInBackground(objects, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putLong("JobOffer_timestamp", objects.get(0).getUpdatedAt().getTime()).commit();
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

                    jobOffersAdapter.setJobOffers(objects);
                    jobOffersAdapter.notifyDataSetChanged();
                    swypeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void  onResume(){
        super.onResume();
        searchInOnResume(true);
    }



    public void searchInOnResume(final boolean syncFromRemote){
        ParseQuery<JobOffer> query = ParseQuery.getQuery("JobOffer");
        query.fromLocalDatastore();
        query.orderByDescending("createdAt");
        query.setLimit(1000);
        query.findInBackground(new FindCallback<JobOffer>() {
            @Override
            public void done(List<JobOffer> objects, ParseException e) {

                if (objects != null) {
                    jobOffersAdapter.setJobOffers(objects);
                    jobOffersAdapter.notifyDataSetChanged();
                    swypeRefreshLayout.setRefreshing(false);
                    getActivity().findViewById(R.id.itemsRecyclerView).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.empty_view).setVisibility(View.GONE);
                }

                if (syncFromRemote == false) return;


                ParseQuery<JobOffer> query = ParseQuery.getQuery("JobOffer");
                query.setLimit(1000);
                query.orderByDescending("updatedAt");
                query.whereGreaterThan("updatedAt", new Date(PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong("JobOffer_timestamp", 0)));
                query.findInBackground(new FindCallback<JobOffer>() {
                    @Override
                    public void done(final List<JobOffer> objects, ParseException e) {
                        if (objects != null && objects.size() != 0) {
                            ParseObject.pinAllInBackground(objects, new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putLong("JobOffer_timestamp", objects.get(0).getUpdatedAt().getTime()).commit();
                                    searchInOnResume(false);
                                }
                            });
                        }
                    }
                });


            }
        });
    }



    public class JobOffersAdapter extends RecyclerView.Adapter<JobOffersAdapter.ViewHolder> {
        private List<JobOffer> jobOffers;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public LinearLayout cardView;

            public ViewHolder(LinearLayout v) {
                super(v);
                cardView = v;
            }
        }

        public void setJobOffers(List<JobOffer> myDataset) {
            jobOffers = myDataset;
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public JobOffersAdapter() {
            jobOffers = new ArrayList<>();
        }

        // Create new views (invoked by the layout manager)
        @Override
        public JobOffersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            LinearLayout v = (LinearLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.joboffer_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            final JobOffer jobOffer = jobOffers.get(position);


            holder.cardView.findViewById(R.id.card_view).setVisibility(View.VISIBLE);
            if(currentUser.isCompany()){
                if(!jobOffer.getPublisher().getObjectId().equals(currentUser.getObjectId())){
                    holder.cardView.findViewById(R.id.card_view).setVisibility(View.GONE);
                    return;
                }
            }



            ((TextView)holder.cardView.findViewById(R.id.title)).setText(jobOffer.getTitle());
            ((TextView)holder.cardView.findViewById(R.id.location)).setText(jobOffer.getLocation());

            ((TextView)holder.cardView.findViewById(R.id.description)).setText(MyUtils.ellipsize(jobOffer.getDescription(), 150));
            ((TextView)holder.cardView.findViewById(R.id.time_text)).setText(TimeManager.getFormattedTimestamp(jobOffer.getCreatedAt(), "Published"));

            ((TextView) holder.cardView.findViewById(R.id.company)).setText(jobOffer.getCompany());





            User company=jobOffer.getPublisher();
            if(company==null)((ImageView) holder.cardView.findViewById(R.id.category_icon)).setImageResource(R.drawable.default_company_logo);
            else{
                Bitmap b= PoliApp.getModel().getBitmapByUser(getActivity(), company,this);
                if(b!=null){
                    ((ImageView) holder.cardView.findViewById(R.id.category_icon)).setImageBitmap(b);
                }else{
                    ((ImageView) holder.cardView.findViewById(R.id.category_icon)).setImageResource(R.drawable.default_company_logo);
                }
            }




            holder.cardView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExternalIntents.share(getActivity(), "Job offer published on Poliapp: "+jobOffer.getTitle(), jobOffer.getDescription());
                }
            });

            holder.cardView.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), JobOfferDetailActivity.class);
                    intent.putExtra("jobOfferId", jobOffer.getObjectId());
                    startActivity(intent);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {

            return jobOffers.size();
        }
    }

}
