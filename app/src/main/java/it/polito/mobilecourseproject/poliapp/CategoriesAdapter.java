package it.polito.mobilecourseproject.poliapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private String[] categories;
    List<String> categoriesToBeFiltered;
    private Context context;
    private boolean singleChoice;

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
    public CategoriesAdapter(List<String> categoriesToBeFiltered, boolean singleChoice, Context ctx) {

        this.categoriesToBeFiltered=categoriesToBeFiltered;
        this.context=ctx;
        this.singleChoice=singleChoice;
        categories= MyUtils.getCategories();

    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);

        ViewHolder vh = new ViewHolder(ll);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        ((TextView)holder.linearLayout.findViewById(R.id.category_name)).setText(categories[position]);
        ((ImageView)holder.linearLayout.findViewById(R.id.category_icon)).setImageResource(MyUtils.getIconForCategory(categories[position]));

        if(!categoriesToBeFiltered.contains(categories[position])) {
            ((TextView) holder.linearLayout.findViewById(R.id.category_name)).setTextColor(context.getResources().getColor(R.color.myTextSecondaryColor));
            (holder.linearLayout.findViewById(R.id.category_icon)).setAlpha((float) 0.3);
        } else {
            ((TextView) holder.linearLayout.findViewById(R.id.category_name)).setTextColor(context.getResources().getColor(R.color.myTextPrimaryColor));
            (holder.linearLayout.findViewById(R.id.category_icon)).setAlpha((float) 1);
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(singleChoice)
                    categoriesToBeFiltered.clear();

                if(!singleChoice && categoriesToBeFiltered.contains(categories[position]))
                    categoriesToBeFiltered.remove(categories[position]);
                else
                    categoriesToBeFiltered.add(categories[position]);

                notifyDataSetChanged();
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return categories.length;
    }

}