package it.polito.mobilecourseproject.poliapp.noticeboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.ExpandableHeightGridView;
import it.polito.mobilecourseproject.poliapp.GridViewAdapter;
import it.polito.mobilecourseproject.poliapp.ImageDetailActivity;
import it.polito.mobilecourseproject.poliapp.ImageItem;
import it.polito.mobilecourseproject.poliapp.MyUtils;
import it.polito.mobilecourseproject.poliapp.R;
import it.polito.mobilecourseproject.poliapp.model.Notice;
import it.polito.mobilecourseproject.poliapp.utils.imagezoomcrop.GOTOConstants;
import it.polito.mobilecourseproject.poliapp.utils.imagezoomcrop.ImageCropActivity;

public class NoticeDetailActivity extends AppCompatActivity {

    private TextView description;
    private TextView categoryTextView;

    private ExpandableHeightGridView gridView;
    private GridViewAdapter gridAdapter;
    private List<ParseObject> photos;
    final ArrayList<ImageItem> imageItems = new ArrayList<>();
    private Notice notice;
    private String category;
    private String[] picMode = {GOTOConstants.PicModes.CAMERA, GOTOConstants.PicModes.GALLERY};
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vendo treno italo lungo ancora prova ciao");

        description = (TextView)findViewById(R.id.editTextDescription);
        categoryTextView = (TextView)findViewById(R.id.categoryTextView);

        ParseQuery.getQuery("Notice").fromLocalDatastore().getInBackground(getIntent().getStringExtra("noticeId"), new GetCallback<ParseObject>() {
                public void done(ParseObject noticeRetrieved, ParseException e) {
                    if (e == null) {

                        //TODO: Gestire meglio cosa succede mentre carica

                        notice = (Notice)noticeRetrieved;

                        //workaround per aggiornare il title (non ho capito se è un bug o è by design)...
                        //MyUtils.setRefreshToolbarEnable((CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar), false);
                        //getSupportActionBar().setTitle(notice.getTitle());
                        //MyUtils.setRefreshToolbarEnable((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar), true);

                        category = notice.getCategory();
                        categoryTextView.setText(category);
                        ((ImageView)findViewById(R.id.category_icon)).setImageResource(MyUtils.getIconForCategory(category));
                        description.setText(notice.getDescription());

                        //TODO: Loading immagini
                    }
                }
            });


        photos = new LinkedList<>();

        gridView = (ExpandableHeightGridView) findViewById(R.id.photosGridView);
        gridAdapter = new GridViewAdapter(this, R.layout.image_grid_layout, getData());
        gridView.setAdapter(gridAdapter);
        gridView.setExpanded(true);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(NoticeDetailActivity.this, ImageDetailActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("objectId", item.getObjectId());

                //Start details activity
                startActivity(intent);
            }
        });

        findViewById(R.id.addPhotoButton).setVisibility(View.VISIBLE);

        if(!(imageItems!=null && imageItems.size()>0))
            findViewById(R.id.card_view_pictures).setVisibility(View.GONE);


    }

    public void contactPublisher(View v) {

        /*
        findViewById(R.id.drawer_layout).requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(title.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(description.getWindowToken(), 0);

        if(title.getText().toString().trim().equals("")){
            Snackbar.make(findViewById(R.id.drawer_layout), "Title cannot be blank.", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(description.getText().toString().trim().equals("")){
            Snackbar.make(findViewById(R.id.drawer_layout), "Description cannot be blank.", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(categoriesSelected.isEmpty()){
            Snackbar.make(findViewById(R.id.parentView), "Please choose a category.", Snackbar.LENGTH_LONG).show();
            return;
        }

        notice.setTitle(title.getText().toString().trim());
        notice.setDescription(description.getText().toString().trim());
        notice.setCategory(categoriesSelected.get(0));

        notice.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Snackbar.make(findViewById(R.id.drawer_layout), "No network connection.", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addNotice(v);
                        }
                    }).show();
                } else {
                    notice.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Snackbar.make(findViewById(R.id.drawer_layout), "Generic error.", Snackbar.LENGTH_LONG).setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addNotice(v);
                                    }

                                }).show();
                            } else {
                                Snackbar.make(findViewById(R.id.drawer_layout), "Done :)", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        */

    }

    // Prepare some dummy data for gridview
    private ArrayList<ImageItem> getData() {

        if(photos.size()==0) {
            findViewById(R.id.noPhotosTextview).setVisibility(View.VISIBLE);
            findViewById(R.id.tapAndHoldTextview).setVisibility(View.GONE);

        } else {
            findViewById(R.id.noPhotosTextview).setVisibility(View.GONE);
        }

        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < photos.size() ; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(0, -1));
            imageItems.add(new ImageItem(bitmap, "Loading...", photos.get(i).getObjectId()));
        }

        for(int i = 0; i < photos.size() ; i++) {

            final int position = i;

            photos.get(i).getParseFile("photo").getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if(bytes!=null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageItems.get(position).setImage(bitmap);
                        imageItems.get(position).setTitle(photos.get(position).getString("name" + position));
                        gridAdapter.notifyDataSetChanged();
                    }

                }
            });
        }

        return imageItems;
    }


}


