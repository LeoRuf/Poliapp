package it.polito.mobilecourseproject.poliapp.time_schedule;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import it.polito.mobilecourseproject.poliapp.ExternalIntents;
import it.polito.mobilecourseproject.poliapp.MyUtils;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.TimeManager;
import it.polito.mobilecourseproject.poliapp.model.Notice;
import it.polito.mobilecourseproject.poliapp.noticeboard.AddNoticeActivity;
import it.polito.mobilecourseproject.poliapp.noticeboard.CategoriesAdapter;


public class TimeScheduleFragment extends android.support.v4.app.Fragment{

    CoordinatorLayout.Behavior behavior;
    int scrollFlags;
    ImageView searchButton;
    CardView searchCardView;
    View fragmentView;
    String[] searchList=null;
    AutoCompleteTextView autoCompleteTextView=null;

    public TimeScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView= inflater.inflate(R.layout.fragment_time_schedule, container, false);
        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);

        searchCardView = (CardView) fragmentView.findViewById(R.id.card_view);
        searchButton = (ImageView) fragmentView.findViewById(R.id.searchButton);
        autoCompleteTextView = (AutoCompleteTextView) fragmentView.findViewById(R.id.autocompleteId);
        searchList = new String[] { "Protocolli e architetture di routing", "Mobile application development","Applicazioni Internet","Tecnologie e servizi di rete","Sicurezza dei sistemi informatici","Ingegneria del software","Architetture dei sistemi di elaborazione","Distributed programming II","Optimization methods and algorithms","Progetto di reti locali","Programmazione di sistema","Programmazione distribuita I","Sistemi per la gestione di basi di dati","Fulvio Giovanni Ottavio Risso","Giovanni Malnati","Mario Baldi","Antonio Lioy","Giorgio Bruno","Marco Mezzalama","Riccardo Sisto","Roberto Tadei","Enrico Masala","Elena Maria Baralis","Informatica", "Fulvio Corno" };
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, searchList);
        autoCompleteTextView.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTypedString();
            }
        });

        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            searchTypedString();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        return fragmentView;
    }

    public void searchTypedString(){
        if(autoCompleteTextView.getText().toString()!=null && !autoCompleteTextView.getText().toString().isEmpty()){

            if (Arrays.asList(searchList).contains(autoCompleteTextView.getText().toString())) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("stringTyped",autoCompleteTextView.getText().toString());
                if(((CheckBox)getActivity().findViewById(R.id.flagConsulting)).isChecked()){
                    editor.putString("consulting","yes");
                }else{
                    editor.putString("consulting","no");
                }
                editor.apply();
                closeKeyboard(getActivity(), autoCompleteTextView.getWindowToken());

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame, new TimeScheduleTimetableFragment(), "TIMESCHEDULE_TIMETABLE_FRAGMENT");
                ft.addToBackStack(null);
                getActivity().getSupportFragmentManager().executePendingTransactions();
                ft.commit();
            }else{
                Toast.makeText(getActivity(), autoCompleteTextView.getText() + " non trovato/a", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), "Search view is empty! " + autoCompleteTextView.getText(), Toast.LENGTH_SHORT).show();
        }
    }
    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Time schedule");

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


        FloatingActionButton fab =(FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

    */

        FloatingActionButton fab =(FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        myOnAttach(getActivity());
    }
}
