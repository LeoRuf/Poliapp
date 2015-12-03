package it.polito.mobilecourseproject.poliapp;

import android.support.design.widget.CollapsingToolbarLayout;

import java.lang.reflect.Field;
import java.util.Random;

public class MyUtils {

    private final static String SEARCH_TRAIN = "Looking for train ticket";
    private final static String SELL_TRAIN = "Selling train ticket";
    private final static String SELL_BOOK = "Selling books";
    private final static String SEARCH_BOOK = "Looking for books";
    private final static String RENT_HOUSE = "Renting house";


    private static String[] categories= {
            SEARCH_TRAIN,
            SELL_TRAIN,
            SELL_BOOK,
            SEARCH_BOOK,
            RENT_HOUSE,
                "Other 1",
                "Other 2",
                "Other 3",
                "Other 4",
                "Other 5",
                "Other 6",
                "Other 7"
    };

    public static String[] getCategories(){
        return categories;
    }

    public static int getIconForCategory(String category){

        if(category==null)
            return R.drawable.cat_no_category;

        switch(category) {
            case SEARCH_TRAIN: return R.drawable.cat_train_purple;
            case SELL_TRAIN: return R.drawable.cat_train_green;
            case SELL_BOOK: return R.drawable.cat_book_blue;
            case SEARCH_BOOK: return R.drawable.cat_book_green;
            case RENT_HOUSE: return R.drawable.cat_house_red;
            default: return R.drawable.cat_no_category;
        }

    }

    public static void setRefreshToolbarEnable(CollapsingToolbarLayout collapsingToolbarLayout,
                                               boolean refreshToolbarEnable) {
        try {
            Field field = CollapsingToolbarLayout.class.getDeclaredField("mRefreshToolbar");
            field.setAccessible(true);
            field.setBoolean(collapsingToolbarLayout, refreshToolbarEnable);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
