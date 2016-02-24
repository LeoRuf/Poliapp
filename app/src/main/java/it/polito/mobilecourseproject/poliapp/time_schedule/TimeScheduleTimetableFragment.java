package it.polito.mobilecourseproject.poliapp.time_schedule;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import it.polito.mobilecourseproject.poliapp.R;

import static android.graphics.Color.parseColor;

public class TimeScheduleTimetableFragment extends android.support.v4.app.Fragment implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener{

    ImageView searchButton;
    int scrollFlags;
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    List<WeekViewEvent> eventsToShow = null;
    String stringTyped=null;
    String flagConsulting=null;
    View fragmentView=null;
    WeekViewEvent newEvent;
    Map<String,Integer> mappingColor;

    public TimeScheduleTimetableFragment() {
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
        fragmentView= inflater.inflate(R.layout.fragment_time_schedule_timetable, container, false);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        stringTyped = sharedPreferences.getString("stringTyped", "DEFAULT");
        flagConsulting = sharedPreferences.getString("consulting", "DEFAULT");

        mappingColor = new HashMap<>();

        eventsToShow = new ArrayList<WeekViewEvent>();

        mWeekView = (WeekView) fragmentView.findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener(TimeScheduleTimetableFragment.this);
        mWeekView.setMonthChangeListener(TimeScheduleTimetableFragment.this);
        mWeekView.setEventLongPressListener(TimeScheduleTimetableFragment.this);
        setupDateTimeInterpreter(false);
        getEventsParse();

        return fragmentView;
    }

    public void setWeekView(){
        if (mWeekViewType != TYPE_WEEK_VIEW) {
            mWeekViewType = TYPE_WEEK_VIEW;
            mWeekView.setNumberOfVisibleDays(7);

            mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
            mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
            mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean result = menu.findItem(R.id.action_week_view).isChecked();
        int numberOfDays = mWeekView.getNumberOfVisibleDays();
        if(result == true && numberOfDays!=7){
            mWeekViewType= TYPE_DAY_VIEW;
            setWeekView();
        }
        return ;
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        LinearLayout noEventLinear= (LinearLayout) getActivity().findViewById(R.id.noEventLinear);

        ArrayList<WeekViewEvent> currentMonthList=new ArrayList<WeekViewEvent>();
        for(WeekViewEvent we : eventsToShow){
            if(we.getStartTime().get(Calendar.MONTH)==(newMonth-1) && we.getStartTime().get(Calendar.YEAR)== newYear){
                currentMonthList.add(we);
            }
        }

        return currentMonthList;
    }

    private void getEventsParse(){
        ParseQuery<ParseObject> queryProf = ParseQuery.getQuery("Event");
        queryProf.whereEqualTo("professore",stringTyped);

        ParseQuery<ParseObject> querySubject = ParseQuery.getQuery("Event");
        querySubject.whereEqualTo("materia",stringTyped);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(queryProf);
        queries.add(querySubject);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for(ParseObject obj : objects){
                    WeekViewEvent newEvent;

                    Calendar startTime;
                    Calendar endTime;

                    String data= obj.getString("data");
                    String oreMinuti= obj.getString("oreMinuti");
                    int durata= Integer.parseInt(obj.getString("durata"));

                    String[] tokens = data.split("/");
                    int giorno = Integer.parseInt(tokens[0]);
                    int mese = Integer.parseInt(tokens[1]);
                    int anno = Integer.parseInt(tokens[2]);

                    String[] tokens1 = oreMinuti.split(":");
                    int ore = Integer.parseInt(tokens1[0]);
                    int minuti = Integer.parseInt(tokens1[1]);


                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.DAY_OF_MONTH, giorno);
                    startTime.set(Calendar.MONTH, mese-1);
                    startTime.set(Calendar.YEAR, anno);
                    startTime.set(Calendar.HOUR_OF_DAY, ore);
                    startTime.set(Calendar.MINUTE, minuti);

                    endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.MINUTE, durata*90);

                    newEvent = new WeekViewEvent(obj.getObjectId(),obj.getString("materia"),obj.getString("aula"), obj.getString("professore"), startTime, endTime,obj.getString("flagConsulting"));
                    //newEvent.setColor(chooseColor(obj.getString("materia")));
                    newEvent.setColor(parseColor(obj.getString("Colour")));
                    if(flagConsulting.equals("no") && obj.getString("flagConsulting").equals("yes")) {
                        //don't show
                    }else{
                        eventsToShow.add(newEvent);
                    }
                }

                mWeekView = (WeekView) fragmentView.findViewById(R.id.weekView);
                mWeekView.notifyDatasetChanged();
            }
        });
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Intent i = new Intent(getActivity(), InfoLecture.class);
        i.putExtra("color", event.getColor()+"");
        i.putExtra("date",event.getData());
        i.putExtra("professore",event.getProfessore());
        i.putExtra("aula",event.getAula());
        i.putExtra("event_title",event.getMateria());
        i.putExtra("favourite","no");
        i.putExtra("event_id", event.getId());
        i.putExtra("flagConsulting", event.getFlag());
        i.putExtra("id", event.getId());
        startActivity(i);
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity(), "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
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

        FloatingActionButton fab =(FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

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
*/


    }

    @Override
    public void onResume() {
        super.onResume();
        myOnAttach(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_schedule_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
