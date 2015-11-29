package it.polito.mobilecourseproject.poliapp;

public class MyUtils {

    private static String[] categories= {
        "Looking for train ticket",
                "Selling train ticket",
                "Selling books",
                "Looking for books",
                "Renting house",
                "Other 1",
                "Other 2",
                "Other 3",
                "Other 4",
                "Other 5",
                "Other 6",
                "Other 7"
    };

    public final static int CATEGORY_TYPE_SEARCH=1;
    public final static int CATEGORY_TYPE_SELL=2;
    public final static int CATEGORY_TYPE_OTHER=0;

    public static String[] getCategories(){
        return categories;
    }

    public static int getIconForCategory(String category){
        return R.drawable.blue_train_2;
    }

    public int getTypeForCategory(String category) {
        return CATEGORY_TYPE_OTHER;
    }
}
