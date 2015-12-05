package it.polito.mobilecourseproject.poliapp.findaroom;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.JSONManager;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.Notice;
import it.polito.mobilecourseproject.poliapp.model.Room;


public class FindARoomFragment extends android.support.v4.app.Fragment implements SearchView.OnQueryTextListener {

    private OnFragmentInteractionListener mListener;
    ArrayList<Notice> outputNotices;
    private GoogleMap gMap;
    private SupportMapFragment fragmentMap;
    private LatLng centerOfCamera=null;

    private HashMap<String,String> objectIDs=new HashMap<String,String>();

    boolean firstTime=true;
    private HashMap<Marker,Room> markers=new HashMap<Marker,Room>();

    CoordinatorLayout.Behavior behavior;
    int scrollFlags;
    List<Room> rooms=null;



    public FindARoomFragment() {
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
        View fragmentView = inflater.inflate(R.layout.fragment_findaroom, container, false);

        return fragmentView;
    }


    public void setMap(){



        MapsInitializer.initialize(getActivity().getApplicationContext());
        fragmentMap = new SupportMapFragment();
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.map, fragmentMap).commit();


        fragmentMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;

                gMap.getUiSettings().setRotateGesturesEnabled(false);
                gMap.getUiSettings().setCompassEnabled(false);
                gMap.setMyLocationEnabled(true);



                //default location
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(45.062759, 7.654563));
                builder.include(new LatLng(45.066699, 7.657482));
                builder.include(new LatLng(45.060470, 7.661065));
                builder.include(new LatLng(45.064578, 7.663747));
                //final LatLng location = new LatLng(45.062759, 7.654563);//polito

                LatLngBounds bounds = builder.build();

                int width=getActivity().findViewById(R.id.map).getWidth();
                int height=getActivity().findViewById(R.id.map).getHeight();
                if(getActivity().findViewById(R.id.map).getWidth()==0 || getActivity().findViewById(R.id.map).getHeight()==0){
                    Point size = new Point();
                    getActivity().getWindowManager().getDefaultDisplay().getSize(size);
                    width = (int)size.x;
                    height = (int)size.y;
                }
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0);


                gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {

                        gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                            @Override
                            public void onCameraChange(CameraPosition cameraPosition) {
                                if(firstTime) {
                                    setDetailMap();
                                    firstTime=false;
                                }
                            }
                        });

                        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(gMap.getCameraPosition().target)
                                .zoom(gMap.getCameraPosition().zoom)
                                .bearing(-61)
                                .build()));



                    }
                });
                gMap.animateCamera(cu);







            }});






    }


    public void setDetailMap(){
        //entrance
        int size=(int)getResources().getDimension(R.dimen.marker_height);
        Bitmap up=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.up), size, size, false);
        BitmapDescriptor markerIcon=BitmapDescriptorFactory.fromBitmap(up);
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.062337, 7.662696)).icon(markerIcon).title("Corso Duca degli Abruzzi"));
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.064598, 7.660029)).icon(markerIcon).title("Corso Castelfidardo, 32") );
        Bitmap down=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.down), size, size, false);
        markerIcon=BitmapDescriptorFactory.fromBitmap(down);
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.062158, 7.658967)).icon(markerIcon).title("Corso Castelfidardo, 39"));
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.063302, 7.659707)).icon(markerIcon).title("Corso Castelfidardo, 39"));
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.065291, 7.656681)).icon(markerIcon).title("Via Pier Carlo Boggio, 36-40"));
        Bitmap right=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.right), size, size, false);
        markerIcon=BitmapDescriptorFactory.fromBitmap(right);
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.060683, 7.659806)).icon(markerIcon).title("Corso Luigi Einaudi, 44"));
        Bitmap left=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.left), size, size, false);
        markerIcon=BitmapDescriptorFactory.fromBitmap(left);
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.064874, 7.661617)).icon(markerIcon).title("Corso Rodolfo Montevecchio, 66"));




        PolylineOptions polylineOptions= getPolyline(new LatLng(45.062599, 7.661781), new LatLng(45.062404, 7.662301));
        gMap.addPolyline(polylineOptions);


        polylineOptions= getPolyline(new LatLng(45.062037, 7.661370),new LatLng(45.063249, 7.662247));
        gMap.addPolyline(polylineOptions);


        polylineOptions= getPolyline(new LatLng(45.062802, 7.661928),new LatLng(45.063128, 7.661051));
        gMap.addPolyline(polylineOptions);


        polylineOptions= getPolyline(new LatLng(45.062431, 7.661662),new LatLng(45.062755, 7.660780));
        gMap.addPolyline(polylineOptions);

        polylineOptions= getPolyline(new LatLng(45.062755, 7.660780),new LatLng(45.063128, 7.661051));
        gMap.addPolyline(polylineOptions);

        polylineOptions= getPolyline(new LatLng(45.061017, 7.659941),new LatLng(45.062635, 7.661094));
        gMap.addPolyline(polylineOptions);

        polylineOptions= getPolyline(new LatLng(45.064371, 7.661984),new LatLng(45.063128, 7.661051));
        gMap.addPolyline(polylineOptions);


        polylineOptions= getPolyline(new LatLng(45.062417, 7.660936),new LatLng(45.062872, 7.659670));
        gMap.addPolyline(polylineOptions);


        polylineOptions= getPolyline(new LatLng(45.062630, 7.659520),new LatLng(45.063995, 7.660475));
        gMap.addPolyline(polylineOptions);


        polylineOptions= getPolyline(new LatLng(45.062629, 7.659509),new LatLng(45.063342, 7.657481));
        gMap.addPolyline(polylineOptions);

        polylineOptions= getPolyline(new LatLng(45.064715, 7.658442),new LatLng(45.063342, 7.657481));
        gMap.addPolyline(polylineOptions);


        polylineOptions= getPolyline(new LatLng(45.065302, 7.656747),new LatLng(45.063995, 7.660491));
        gMap.addPolyline(polylineOptions);



        polylineOptions= getPolyline(new LatLng(45.062963, 7.658549),new LatLng(45.061735, 7.657712));
        gMap.addPolyline(polylineOptions);


        polylineOptions= getPolyline(new LatLng(45.061735, 7.657712),new LatLng(45.062344, 7.656108));
        gMap.addPolyline(polylineOptions);


        polylineOptions= getPolyline(new LatLng(45.062344, 7.656108),new LatLng(45.063704, 7.655792));
        gMap.addPolyline(polylineOptions);



        polylineOptions= getPolyline(new LatLng(45.063704, 7.655792),new LatLng(45.063766, 7.656414));
        gMap.addPolyline(polylineOptions);



        polylineOptions= getPolyline(new LatLng(45.063766, 7.656414),new LatLng( 45.064925, 7.657246));
        gMap.addPolyline(polylineOptions);


        polylineOptions= getPolyline(new LatLng(45.064925, 7.657246),new LatLng( 45.065302, 7.656747));
        gMap.addPolyline(polylineOptions);



        polylineOptions= getPolyline(new LatLng(45.064342, 7.659504),new LatLng( 45.065562, 7.660384 ));
        gMap.addPolyline(polylineOptions);

        polylineOptions= getPolyline(new LatLng(45.065562, 7.660384), new LatLng(45.066517, 7.657648));
        gMap.addPolyline(polylineOptions);



        rooms=(new JSONManager(getActivity())).jsonTORooms();
        for(Room room : rooms){
            markers.put(
                    gMap.addMarker(new MarkerOptions().position(room.getLocation()).title(room.getName()).icon(BitmapDescriptorFactory.defaultMarker(room.getMarkerColor()))),
                    room
            );

        }
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Room room=markers.get(marker);
                if(room==null) return false;
                View descrLayout=getActivity().findViewById(R.id.description);
                TextView nameV=(TextView)getActivity().findViewById(R.id.nameV);
                TextView floorV=(TextView)getActivity().findViewById(R.id.floorV);
                TextView detailsV=(TextView)getActivity().findViewById(R.id.detailsV);
                View labelV=getActivity().findViewById(R.id.labelV);

                nameV.setText(room.getName());
                floorV.setText(room.getFloor());
                String text=room.getType();
                if(room.getSeats()!=0)text=text+", "+room.getSeats()+" seats";
                detailsV.setText(text);
                labelV.setBackgroundColor(room.getColor());

                descrLayout.setVisibility(View.VISIBLE);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(room.getLocation(), 18f));
                return true;
            }
        });


        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                View descrLayout=getActivity().findViewById(R.id.description);
                descrLayout.setVisibility(View.GONE);
            }
        });




    }





    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

        //FloatingActionButton fab =(FloatingActionButton) getActivity().findViewById(R.id.fab);
        //fab.setVisibility(View.VISIBLE);




    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

         myOnAttach(getActivity());

            setMap();




    }

    @Override
    public void onDetach() {
        super.onDetach();

        android.support.v7.widget.Toolbar toolbar =(android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        //Ripristina gli scrollFlags originali
        params.setScrollFlags(scrollFlags);

        //FloatingActionButton fab =(FloatingActionButton) getActivity().findViewById(R.id.fab);
        //fab.setVisibility(View.GONE);



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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.noticeboard_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                //TODO: Aggiungere search

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

                if(getActivity().findViewById(R.id.description).getVisibility()==View.GONE) {
                    gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            getActivity().findViewById(R.id.description).setVisibility(View.VISIBLE);
                        }
                    });
                    gMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(gMap.getCameraPosition().target)
                            .zoom(gMap.getCameraPosition().zoom + (float) 0.5)
                            .bearing(-61)
                            .build()));
                }else
                    getActivity().findViewById(R.id.description).setVisibility(View.GONE);










                /*categoriesToBeFiltered.clear();
                categoriesToBeFiltered.addAll(categoriesFiltered);

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_categories);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setTitle("Filter categories");
                dialog.setCancelable(true);
                SearchView searchView= (SearchView)dialog.findViewById(R.id.searchView);
                searchView.setOnQueryTextListener(this);

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
                dialog.show();*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void search(){
        /*ParseQuery<Notice> query = ParseQuery.getQuery("Notice");
        query.fromLocalDatastore();
        query.orderByDescending("createdAt");
        query.setLimit(1000);
        query.findInBackground(new FindCallback<Notice>() {
            @Override
            public void done(List<Notice> objects, ParseException e) {
                noticesAdapter.setNotices(objects);
                noticesAdapter.notifyDataSetChanged();
                swypeRefreshLayout.setRefreshing(false);
            }
        });*/
    }




    public PolylineOptions getPolyline(LatLng a,LatLng b){
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(5);
        polylineOptions.add(a);
        polylineOptions.add(b);
        return  polylineOptions;
    }




    public boolean onBackPressed(){
        View descrLayout=getActivity().findViewById(R.id.description);
        if(descrLayout.getVisibility()==View.VISIBLE){
            descrLayout.setVisibility(View.GONE);
            return false;
        }
        return true;
    }




}
