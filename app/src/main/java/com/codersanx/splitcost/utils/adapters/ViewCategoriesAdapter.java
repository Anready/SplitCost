package com.codersanx.splitcost.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codersanx.splitcost.R;

import java.math.BigDecimal;
import java.util.List;


public class ViewCategoriesAdapter extends ArrayAdapter<Category> {
    private final Context context;
    private final List<Category> items;

    public ViewCategoriesAdapter(Context context, List<Category> items) {
        super(context, R.layout.category_item_layout, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.category_item_layout, parent, false);

        Category currentItem = items.get(position);

        TextView title = listItem.findViewById(R.id.textViewTitle);
        title.setText(currentItem.getTitle());

        TextView description = listItem.findViewById(R.id.textViewDescription);
        description.setText(currentItem.getDescription());

        TextView expense = listItem.findViewById(R.id.textViewExpense);
        expense.setText(String.valueOf(currentItem.getCategoryValue()));

        BigDecimal totalValue = currentItem.getTotalValue();
        BigDecimal categoryValue = currentItem.getCategoryValue();

        double totalDouble = totalValue.doubleValue();
        double categoryDouble = categoryValue.doubleValue();

        int maxProgress = (int) (totalDouble / categoryDouble * Integer.MAX_VALUE);
        int currentProgress = (int) (categoryDouble / totalDouble * Integer.MAX_VALUE);

        ProgressBar progressBar = listItem.findViewById(R.id.progressBar);
        progressBar.setMax(maxProgress);
        progressBar.setProgress(currentProgress);


        return listItem;
    }
}
