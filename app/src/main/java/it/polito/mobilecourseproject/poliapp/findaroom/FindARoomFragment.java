package it.polito.mobilecourseproject.poliapp.findaroom;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.JSONManager;
import it.polito.mobilecourseproject.poliapp.MyUtils;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.Notice;
import it.polito.mobilecourseproject.poliapp.model.Room;


public class FindARoomFragment extends android.support.v4.app.Fragment   {

    private CameraUpdate cu;

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

    ArrayAdapter<String> searchAdapter;



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

                int width = getActivity().findViewById(R.id.map).getWidth();
                int height = getActivity().findViewById(R.id.map).getHeight();
                if (getActivity().findViewById(R.id.map).getWidth() == 0 || getActivity().findViewById(R.id.map).getHeight() == 0) {
                    Point size = new Point();
                    getActivity().getWindowManager().getDefaultDisplay().getSize(size);
                    width = (int) size.x;
                    height = (int) size.y;
                }
                cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0);


                gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        if (firstTime) {
                            setDetailMap();
                            gMap.setOnCameraChangeListener(null);
                            firstTime = false;
                        }

                    }
                });
                gMap.animateCamera(cu);


            }
        });






    }


    public void setDetailMap(){
        //entrance
        int size=(int)getResources().getDimension(R.dimen.marker_height);
        Bitmap up=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.entrance), size, size, false);
        BitmapDescriptor markerIcon=BitmapDescriptorFactory.fromBitmap(up);
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.062337, 7.662696)).icon(markerIcon).title("Corso Duca degli Abruzzi"));
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.064598, 7.660029)).icon(markerIcon).title("Corso Castelfidardo, 32") );
        Bitmap down=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.entrance), size, size, false);
        markerIcon=BitmapDescriptorFactory.fromBitmap(down);
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.062158, 7.658967)).icon(markerIcon).title("Corso Castelfidardo, 39"));
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.063302, 7.659707)).icon(markerIcon).title("Corso Castelfidardo, 39"));
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.065291, 7.656681)).icon(markerIcon).title("Via Pier Carlo Boggio, 36-40"));
        Bitmap right=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.entrance), size, size, false);
        markerIcon=BitmapDescriptorFactory.fromBitmap(right);
        gMap.addMarker(new MarkerOptions().position(new LatLng(45.060683, 7.659806)).icon(markerIcon).title("Corso Luigi Einaudi, 44"));
        Bitmap left=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.entrance), size, size, false);
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
                ImageView img=(ImageView)getActivity().findViewById(R.id.category_icon);

                img.setImageResource(MyUtils.getIconForRoomType(room.getType()));


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


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Find a room");

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

        searchAdapter= new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,android.R.id.text1);
        ListView listView=(ListView)getActivity().findViewById(R.id.listView);
        listView.setAdapter(searchAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value=searchAdapter.getItem(position);
                onQueryTextListener.onQueryTextSubmit(value);
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

    SearchView searchView;
    SearchView.OnQueryTextListener onQueryTextListener;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.findaroom_menu, menu);


        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search");
        onQueryTextListener=new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                getActivity().findViewById(R.id.description).setVisibility(View.GONE);

                searchAdapter.clear();
                searchAdapter.notifyDataSetChanged();

                for (Room room : rooms) {
                    if (room.getName().toLowerCase().equals(s.toLowerCase().trim())) {
                        MarkerOptions mo = new MarkerOptions().position(room.getLocation()).title(room.getName()).icon(BitmapDescriptorFactory.defaultMarker(room.getMarkerColor()));

                        for(Marker marker : markers.keySet()){
                            marker.remove();
                        }

                        markers.put(gMap.addMarker(mo), room);



                        final View descrLayout=getActivity().findViewById(R.id.description);
                        TextView nameV=(TextView)getActivity().findViewById(R.id.nameV);
                        TextView floorV=(TextView)getActivity().findViewById(R.id.floorV);
                        TextView detailsV=(TextView)getActivity().findViewById(R.id.detailsV);
                        View labelV=getActivity().findViewById(R.id.labelV);
                        nameV.setText(room.getName());
                        floorV.setText(room.getFloor());
                        String text=room.getType();
                        ImageView img=(ImageView)getActivity().findViewById(R.id.category_icon);
                        img.setImageResource(MyUtils.getIconForRoomType(room.getType()));
                        if(room.getSeats()!=0)text=text+", "+room.getSeats()+" seats";
                        detailsV.setText(text);

                        hideSoftKeyboard();

                        labelV.setBackgroundColor(room.getColor());



                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(room.getLocation(), 18f));
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                descrLayout.setVisibility(View.VISIBLE);
                            }
                        },200);

                        return false;
                    }
                }

                Toast.makeText(getContext(), "No room found", Toast.LENGTH_LONG).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchAdapter.clear();
                searchAdapter.notifyDataSetChanged();

                if (s.trim().equals("") || (s.trim().length() < 2 && !isNumeric(s.trim()))) {
                    return false;
                }

                for (Room room : rooms) {
                    List<String> words = Arrays.asList(s.toLowerCase().trim().split(" "));
                    for (String w : words) {
                        if (room.getName().toLowerCase().contains(w)) {
                            searchAdapter.add(room.getName());
                            break;
                        }
                    }
                }
                searchAdapter.notifyDataSetChanged();


                return false;
            }
        };
        searchView.setOnQueryTextListener(onQueryTextListener);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.filter:


                DialogFragment f=new DialogFragment() {
                RecyclerView mRecyclerView;
                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                    //inflate layout with recycler view
                    View v = inflater.inflate(R.layout.dialog_room_types, container, false);
                    mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    //setadapter
                    mRecyclerView.setAdapter(new RoomTypesAdapter(this.getDialog(), (new JSONManager(getActivity())).jsonTORoomTypes(), getActivity()));

                    getDialog().setCanceledOnTouchOutside(true);
                    getDialog().setTitle("Room categories");
                    getDialog().setCancelable(true);
                    return v;
                }
                    public void onResume()
                    {
                        super.onResume();
                        mRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                Window window = getDialog().getWindow();
                                int w=mRecyclerView.getWidth();int h= mRecyclerView.getHeight();
                                window.setLayout(w,h);
                            }
                        });

                    }
                };

                f.show(getActivity().getSupportFragmentManager(), "some tag");



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
                recyclerView.setAdapter(new RoomTypesAdapter(dialog, (new JSONManager(getActivity())).jsonTORoomTypes(), getActivity()));

                //dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                dialog.show();*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }






    public PolylineOptions getPolyline(LatLng a,LatLng b){
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(5);
        polylineOptions.add(a);
        polylineOptions.add(b);
        return  polylineOptions;
    }


    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }



    public boolean onBackPressed(){
        View descrLayout=getActivity().findViewById(R.id.description);
        if(descrLayout.getVisibility()==View.VISIBLE){
            descrLayout.setVisibility(View.GONE);
            return false;
        }
        return true;
    }



    public class RoomTypesAdapter extends RecyclerView.Adapter<RoomTypesAdapter.ViewHolder> {

        private List<String> roomTypes;
        private Context context;
        private Dialog dialog;

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
        public RoomTypesAdapter(Dialog dialog,List<String> roomTypes, Context ctx) {
            this.roomTypes = roomTypes;
            this.context=ctx;
            this.dialog=dialog;

        }

        // Create new views (invoked by the layout manager)
        @Override
        public RoomTypesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.room_type_item, parent, false);

            ViewHolder vh = new ViewHolder(ll);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final String roomType=roomTypes.get(position);
            ((TextView)holder.linearLayout.findViewById(R.id.category_name)).setText(roomType);

            int imageId=MyUtils.getIconForRoomType(roomType);
            ((ImageView)holder.linearLayout.findViewById(R.id.category_icon)).setImageResource(imageId);




            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
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
            });




        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {

            return roomTypes.size();
        }

    }







}
