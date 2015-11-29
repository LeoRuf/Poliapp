package it.polito.mobilecourseproject.poliapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.mobilecourseproject.poliapp.utils.imagezoomcrop.GOTOConstants;
import it.polito.mobilecourseproject.poliapp.utils.imagezoomcrop.ImageCropActivity;

public class AddNoticeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private EditText title;
    private EditText description;
    private TextView categoryTextView;

    private ExpandableHeightGridView gridView;
    private GridViewAdapter gridAdapter;
    private List<ParseObject> photos;
    final ArrayList<ImageItem> imageItems = new ArrayList<>();
    private Notice notice;
    private List<String> categoriesSelected; //E' una lista per riutilizzare lo stesso codice, ma la lista avrà sempre un solo elemento
    private List<String> categoriesToBeSelected; //E' una lista per riutilizzare lo stesso codice, ma la lista avrà sempre un solo elemento


    private String[] picMode = {GOTOConstants.PicModes.CAMERA, GOTOConstants.PicModes.GALLERY};
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        categoriesToBeSelected = new LinkedList<>();
        categoriesSelected = new LinkedList<>();

        notice = new Notice();
        photos = new LinkedList<>();

        title = (EditText)findViewById(R.id.editTextTitle);
        description = (EditText)findViewById(R.id.editTextDescription);
        categoryTextView = (TextView)findViewById(R.id.categoryTextView);

        gridView = (ExpandableHeightGridView) findViewById(R.id.photosGridView);
        gridAdapter = new GridViewAdapter(this, R.layout.image_grid_layout, getData());
        gridView.setAdapter(gridAdapter);
        gridView.setExpanded(true);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(AddNoticeActivity.this, ImageDetailActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("objectId", item.getObjectId());

                //Start details activity
                startActivity(intent);
            }
        });

        findViewById(R.id.card_view_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Due righe sotto equivalenti a categoriesToBeSelected = categoriesSelected
                categoriesToBeSelected.clear();
                categoriesToBeSelected.addAll(categoriesSelected);

                final Dialog dialog = new Dialog(AddNoticeActivity.this);
                dialog.setContentView(R.layout.dialog_categories);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setTitle("Choose a category");
                dialog.setCancelable(true);
                SearchView searchView= (SearchView)dialog.findViewById(R.id.searchView);
                searchView.setOnQueryTextListener(AddNoticeActivity.this);

                RecyclerView recyclerView = (RecyclerView)dialog.findViewById(R.id.recyclerView);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddNoticeActivity.this);
                recyclerView.setLayoutManager(layoutManager);

                recyclerView.setAdapter(new CategoriesAdapter(categoriesToBeSelected, true, getApplicationContext()));

                Button okButton = (Button)dialog.findViewById(R.id.ok_button);
                Button cancelButton = (Button)dialog.findViewById(R.id.cancel_button);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        categoriesSelected.clear();
                        categoriesSelected.addAll(categoriesToBeSelected);

                        if(!categoriesSelected.isEmpty()) {
                            categoryTextView.setText(categoriesSelected.get(0));
                            categoryTextView.setTextColor(getResources().getColor(R.color.myTextPrimaryColor));
                            ((ImageView)findViewById(R.id.category_icon)).setImageResource(MyUtils.getIconForCategory(categoriesSelected.get(0)));
                            ((ImageView)findViewById(R.id.category_icon)).setAlpha((float)1);
                        }
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
                dialog.show();
            }
        });

        findViewById(R.id.addPhotoButton).setVisibility(View.VISIBLE);

        if(imageItems!=null && imageItems.size()>0)
            findViewById(R.id.tapAndHoldTextview).setVisibility(View.VISIBLE);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddNoticeActivity.this);
                builder.setTitle("Delete photo")
                        .setMessage("Do you want to delete this photo?\n\nATTENTION: This operation cannot be undone!")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                imageItems.remove(position);
                                photos.get(position).deleteInBackground();
                                ParseRelation<ParseObject> relation = notice.getRelation("photos");
                                relation.remove(photos.get(position));
                                final ActionProcessButton addPhotoButton = (ActionProcessButton) findViewById(R.id.addPhotoButton);
                                addPhotoButton.setProgress(1);
                                addPhotoButton.setText("DELETING...");
                                gridAdapter.notifyDataSetChanged();

                                //TODO: Anziché notice.save c'era user.save (????)
                                notice.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                        addPhotoButton.setProgress(100);
                                        addPhotoButton.setText("DELETED");

                                        addPhotoButton.postDelayed(new Runnable() {

                                            @Override
                                            public void run() {

                                                if (imageItems.size() == 0) {
                                                    findViewById(R.id.noPhotosTextview).setVisibility(View.VISIBLE);
                                                    findViewById(R.id.tapAndHoldTextview).setVisibility(View.GONE);
                                                } else {
                                                    findViewById(R.id.noPhotosTextview).setVisibility(View.GONE);
                                                    findViewById(R.id.tapAndHoldTextview).setVisibility(View.VISIBLE);

                                                }

                                                addPhotoButton.setProgress(0);
                                                addPhotoButton.setText("ADD A PHOTO");
                                            }

                                        }, 2500);


                                    }
                                });
                            }
                        })
                        .setNegativeButton("CANCEL", null);

                builder.create().show();

                return true;
            }
        });

    }

    public void addNotice(View v) {

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

    /********select photo from gallery or camera***********************/
    public void addPhoto(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Mode")
                .setItems(picMode, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String action = picMode[which].equalsIgnoreCase(GOTOConstants.PicModes.CAMERA) ? GOTOConstants.IntentExtras.ACTION_CAMERA : GOTOConstants.IntentExtras.ACTION_GALLERY;
                        Intent intent = new Intent(AddNoticeActivity.this, ImageCropActivity.class);
                        intent.putExtra("ACTION", action);
                        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
                    }
                });
        builder.create().show();

    }

    //handle data returning from camera or gallery
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            performOnActivityResult(requestCode, resultCode, data);

        }

    }

    void performOnActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_UPDATE_PIC) {
            final ActionProcessButton addPhotoButton = (ActionProcessButton) findViewById(R.id.addPhotoButton);
            String imagePath = data.getStringExtra(GOTOConstants.IntentExtras.IMAGE_PATH);
            if (imagePath != null) {
                addPhotoButton.setProgress(1);
                final Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);

                final String photoName = "photo";

                final ParseObject photo = new ParseObject("Photo");
                photo.put("name", photoName);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.PNG, 60, stream);
                byte[] byteArray = stream.toByteArray();
                photo.put("photo", new ParseFile(byteArray));

                photo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        ParseRelation<ParseObject> relation = notice.getRelation("photos");
                        relation.add(photo);
                        notice.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                addPhotoButton.setProgress(100);

                                photos.add(photo);
                                findViewById(R.id.noPhotosTextview).setVisibility(View.GONE);
                                findViewById(R.id.tapAndHoldTextview).setVisibility(View.VISIBLE);
                                imageItems.add(new ImageItem(myBitmap, "", photo.getObjectId()));
                                gridAdapter.notifyDataSetChanged();
                                addPhotoButton.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {

                                        addPhotoButton.setProgress(0);
                                    }

                                }, 2500);
                            }
                        });

                    }
                });

            }

        }

    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement our filter logic
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

}


