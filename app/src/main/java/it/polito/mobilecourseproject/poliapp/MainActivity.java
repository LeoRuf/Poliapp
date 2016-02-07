package it.polito.mobilecourseproject.poliapp;


import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Constructor;

import it.polito.mobilecourseproject.poliapp.findaroom.FindARoomFragment;
import it.polito.mobilecourseproject.poliapp.messages.MessagesFragment;
import it.polito.mobilecourseproject.poliapp.noticeboard.NoticeboardFragment;
import it.polito.mobilecourseproject.poliapp.profile.ProfileActivity;
import it.polito.mobilecourseproject.poliapp.time_schedule.TimeScheduleFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private String currentFragment;



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("currentFragment", currentFragment);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView,savedInstanceState);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView,Bundle savedInstanceState)  {


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(savedInstanceState != null) {
            try {
                Class<?> c = Class.forName(savedInstanceState.getString("currentFragment"));
                Constructor<?> cons = c.getConstructor(String.class);
                Fragment fragment =(Fragment) cons.newInstance();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, savedInstanceState.getString("currentFragment"));
                fragmentTransaction.commit();
            } catch ( Exception e) {
                e.printStackTrace();
            }
        }else{
            HomeFragment homeFragment=new HomeFragment();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, homeFragment, homeFragment.getClass().getName());
            fragmentTransaction.commit();
            navigationView.getMenu().getItem(0).setChecked(true);
        }
 




        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);


                        //Check to see which item was being clicked and perform appropriate action
                        switch (menuItem.getItemId()){

                            //Replacing the main content with ContentFragment Which is our Inbox View;
                            case R.id.nav_home:

                                HomeFragment homeFragment = (HomeFragment)getSupportFragmentManager().findFragmentByTag(HomeFragment.class.getName());
                                if(homeFragment==null)
                                    homeFragment = new HomeFragment();

                                if (!homeFragment.isVisible()) {
                                     final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                     fragmentTransaction.replace(R.id.frame, homeFragment, HomeFragment.class.getName());
                                    startFragment( fragmentTransaction);
                                }
                                currentFragment=homeFragment.getClass().getName();
                                return true;

                            case R.id.nav_noticeboard:

                                NoticeboardFragment noticeboardFragment = (NoticeboardFragment)getSupportFragmentManager().findFragmentByTag(NoticeboardFragment.class.getName());
                                if(noticeboardFragment==null)
                                    noticeboardFragment = new NoticeboardFragment();

                                if (!noticeboardFragment.isVisible()) {
                                    final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                     fragmentTransaction.replace(R.id.frame, noticeboardFragment, NoticeboardFragment.class.getName());
                                    startFragment(fragmentTransaction);
                                }
                                currentFragment=noticeboardFragment.getClass().getName();
                                return true;


                            case R.id.nav_messages:

                                MessagesFragment messagesFragment = (MessagesFragment)getSupportFragmentManager().findFragmentByTag(MessagesFragment.class.getName());
                                if(messagesFragment==null)
                                    messagesFragment = new MessagesFragment();

                                if (!messagesFragment.isVisible()) {
                                    final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                    fragmentTransaction.replace(R.id.frame, messagesFragment, MessagesFragment.class.getName());
                                    startFragment(fragmentTransaction);
                                }
                                currentFragment=messagesFragment.getClass().getName();
                                return true;

                            case R.id.nav_findaroom:

                                FindARoomFragment findARoomFragment = (FindARoomFragment)getSupportFragmentManager().findFragmentByTag(FindARoomFragment.class.getName());
                                //Toast.makeText(getApplicationContext(),findARoomFragment+"",Toast.LENGTH_LONG).show();

                                if(findARoomFragment==null)
                                    findARoomFragment = new FindARoomFragment();


                                if (! findARoomFragment.isVisible()) {
                                   final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                    fragmentTransaction.replace(R.id.frame, findARoomFragment, FindARoomFragment.class.getName());
                                    startFragment( fragmentTransaction);
                                }
                                currentFragment=findARoomFragment.getClass().getName();
                                return true;

                            case R.id.nav_time_schedule:
                                TimeScheduleFragment timeScheduleFragment = (TimeScheduleFragment)getSupportFragmentManager().findFragmentByTag(TimeScheduleFragment.class.getName());
                                if(timeScheduleFragment==null)
                                    timeScheduleFragment = new TimeScheduleFragment();

                                if (!timeScheduleFragment.isVisible()) {
                                    final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                    fragmentTransaction.replace(R.id.frame, timeScheduleFragment, TimeScheduleFragment.class.getName());
                                    startFragment( fragmentTransaction);
                                }
                                currentFragment=timeScheduleFragment.getClass().getName();
                                return true;

                            case R.id.nav_profile:

                                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                                startActivity(i);
                                return true;

                            default:
                                Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                                return true;

                        }

                    }


                });



    }




    private void startFragment(final FragmentTransaction fragmentTransaction){
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}
            @Override
            public void onDrawerOpened(View drawerView) {}
            @Override
            public void onDrawerClosed(View drawerView) {
                try{fragmentTransaction.commit();}catch(Exception e){e.printStackTrace();}}
            @Override
            public void onDrawerStateChanged(int newState) {}
        });
      drawerLayout.closeDrawers();
    }



    @Override
    public void onBackPressed(){
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (f instanceof FindARoomFragment){
            if(!((FindARoomFragment)f).onBackPressed())return;
        }
        super.onBackPressed();
    }


}


