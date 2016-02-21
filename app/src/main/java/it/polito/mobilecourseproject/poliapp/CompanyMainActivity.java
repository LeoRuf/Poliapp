package it.polito.mobilecourseproject.poliapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import it.polito.mobilecourseproject.poliapp.findaroom.FindARoomFragment;
import it.polito.mobilecourseproject.poliapp.jobs.JobOffersFragment;
import it.polito.mobilecourseproject.poliapp.login.LoginActivity;
import it.polito.mobilecourseproject.poliapp.messages.MessageService;
import it.polito.mobilecourseproject.poliapp.messages.MessagesFragment;
import it.polito.mobilecourseproject.poliapp.model.Chat;
import it.polito.mobilecourseproject.poliapp.model.User;
import it.polito.mobilecourseproject.poliapp.noticeboard.NoticeboardFragment;
import it.polito.mobilecourseproject.poliapp.profile.ProfileActivity;
import it.polito.mobilecourseproject.poliapp.searchstudent.SearchStudentFragment;
import it.polito.mobilecourseproject.poliapp.time_schedule.TimeScheduleFragment;

public class CompanyMainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private String currentFragment;
    private User thisUser;
    private NavigationView navigationView;



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("currentFragment", currentFragment);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_company);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);


        try{
            thisUser=AccountManager.getCurrentUser();
        }catch (Exception e){
            e.printStackTrace();
            this.onBackPressed();
            return;

        }




        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

         navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView,savedInstanceState);
        }




    }



    @Override
    public void onResume(){
        super.onResume();
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_name)).setText(thisUser.getCompanyName());
            final CircleImageView imageView=(( CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.imgAvatar));
            PoliApp.getModel().getProfileBitmap(this, thisUser, new User.OnGetPhoto() {
                @Override
                public void onGetPhoto(Bitmap b) {
                    if (b == null) return;
                    imageView.setImageBitmap(b);
                }
            });

    }
    @Override
    public void onPause() {
        super.onPause();

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
            SearchStudentFragment homeFragment=new  SearchStudentFragment();
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
                            case R.id.nav_students:

                                SearchStudentFragment homeFragment = ( SearchStudentFragment)getSupportFragmentManager().findFragmentByTag( SearchStudentFragment.class.getName());
                                if(homeFragment==null)
                                    homeFragment = new  SearchStudentFragment();

                                if (!homeFragment.isVisible()) {
                                     final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                     fragmentTransaction.replace(R.id.frame, homeFragment,  SearchStudentFragment.class.getName());
                                    startFragment( fragmentTransaction);
                                }
                                currentFragment=homeFragment.getClass().getName();
                                return true;


                            case R.id.nav_joboffers:

                                JobOffersFragment jobOffersFragment = (JobOffersFragment)getSupportFragmentManager().findFragmentByTag(JobOffersFragment.class.getName());
                                if(jobOffersFragment==null)
                                    jobOffersFragment = new JobOffersFragment();

                                if (!jobOffersFragment.isVisible()) {
                                    final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                                    fragmentTransaction.replace(R.id.frame, jobOffersFragment, JobOffersFragment.class.getName());
                                    startFragment(fragmentTransaction);
                                }
                                currentFragment=jobOffersFragment.getClass().getName();
                                return true;


                            case R.id.nav_logout:
                                AccountManager.logout();
                                startActivity(new Intent(CompanyMainActivity.this, LoginActivity.class));
                                finish();
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

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }





        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (f instanceof FindARoomFragment){
            if(!((FindARoomFragment)f).onBackPressed())return;
        }
        super.onBackPressed();
    }


}


