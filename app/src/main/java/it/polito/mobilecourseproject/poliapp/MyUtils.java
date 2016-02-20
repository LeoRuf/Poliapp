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

    public final static String CUSTOM_DELIMITER = "£$$%Cust!&2&3Delim£$$%";
    public final static String CUSTOM_DELIMITER_2 = "£$$%Cust!&1&2dELim£$$%";


    private final static String NON_THIN = "[^iIl1\\.,']";

    private static int textWidth(String str) {
        return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
    }

    public static String ellipsize(String text, int max) {

        if (textWidth(text) <= max)
            return text;

        // Start by chopping off at the word before max
        // This is an over-approximation due to thin-characters...
        int end = text.lastIndexOf(' ', max - 3);

        // Just one long word. Chop it off.
        if (end == -1)
            return text.substring(0, max-3) + "...";

        // Step forward as long as textWidth allows.
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);

            // No more spaces.
            if (newEnd == -1)
                newEnd = text.length();

        } while (textWidth(text.substring(0, newEnd) + "...") < max);

        return text.substring(0, end) + "...";
    }

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


    public static int getIconForRoomType(String roomType){

        if(roomType==null)
            return R.drawable.cat_no_category;

        int imageId=-1;
        switch(roomType){
            case "Bar":
                imageId=R.drawable.bar;
                break;
            case "Library":
                imageId=R.drawable.library;
                break;
            case "Laboratory":
                imageId=R.drawable.laboratory;
                break;
            case "Computer Room":
                imageId=R.drawable.computer;
                break;
            case "Classroom":
                imageId=R.drawable.classroom;
                break;
        }
        return imageId;

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
