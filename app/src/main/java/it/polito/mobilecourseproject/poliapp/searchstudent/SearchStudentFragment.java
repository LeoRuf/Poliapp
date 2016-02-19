package it.polito.mobilecourseproject.poliapp.searchstudent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.User;


public class SearchStudentFragment extends android.support.v4.app.Fragment   {


    CoordinatorLayout.Behavior behavior;
    int scrollFlags;
    ArrayList<User> students =new ArrayList<User>();







    public SearchStudentFragment() {
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
        View fragmentView = inflater.inflate(R.layout.fragment_search_student, container, false);

        return fragmentView;
    }



    @Override
    public void onResume(){
        super.onResume();

    }
    @Override
    public void onPause() {
        super.onPause();

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search students");

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


            }
        });
        fab.setVisibility(View.GONE);




    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myOnAttach(getActivity());


        final StudentsAdapter studentsAdapter = new StudentsAdapter(students, getActivity());
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(studentsAdapter);
        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.no_result).setVisibility(View.GONE);
        getActivity().findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);





        final AppCompatEditText skillsEdit=(AppCompatEditText)getActivity().findViewById(R.id.skills);

        getActivity().findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                String text = skillsEdit.getText().toString().trim();
                ArrayList<String> words = getWordsFromText(text);


                getActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.no_result).setVisibility(View.GONE);
                getActivity().findViewById(R.id.recyclerView).setVisibility(View.GONE);
                User.searchStudentsBySkills(getActivity(),words, new User.OnUsersDownloadedCallback() {
                    @Override
                    public void onUsersDownloaded(List<User> users) {
                        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if(users==null || users.size()==0){
                            getActivity().findViewById(R.id.no_result).setVisibility(View.VISIBLE);
                        }else{
                            getActivity().findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
                            students.clear();
                            students.addAll(users);
                            studentsAdapter.setUsers(students);
                            studentsAdapter.notifyDataSetChanged();

                        }
                    }
                });





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


    public ArrayList<String> getWordsFromText(String text) {
        if(text.trim().equals("")){
            return new ArrayList<>();
        }
        text=text.toLowerCase();
        String[] ws0 = text.split(";");
        ArrayList<String> words = new ArrayList<String>();
        for (String w : ws0) {
            w = w.trim();
            String[] ws = w.split(" ");
            for (String ww : ws) {
                ww = ww.trim();
                words.add(ww);
            }
        }
        return  words;

    }



    public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {

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
        public StudentsAdapter(List<User> users, Context ctx) {
            this.users = users;
            this.context=ctx;

        }

        public void setUsers(ArrayList<User> users){
            this.users = users;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public StudentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
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
