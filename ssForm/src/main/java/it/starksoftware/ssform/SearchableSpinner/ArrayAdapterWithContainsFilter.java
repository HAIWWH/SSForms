package it.starksoftware.ssform.SearchableSpinner;

import android.app.Activity;

import androidx.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArrayAdapterWithContainsFilter<S> extends ArrayAdapter {

    private List<String> items = null;
    private ArrayList<String> arraylist;

    public ArrayAdapterWithContainsFilter(Activity context, int items_view, ArrayList<String> items, ArrayList<String> itemsOriginals) {
        super(context,items_view,items);
        this.items = items;
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(itemsOriginals);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return super.getFilter();
    }

    // Filter Class
    public void getContainsFilter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        items.clear();
        if (charText.length() == 0) {
            items.addAll(arraylist);
        }
        else
        {
            for (String item : arraylist)
            {
                if (item.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    items.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
