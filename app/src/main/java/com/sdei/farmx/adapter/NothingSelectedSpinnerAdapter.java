package com.sdei.farmx.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.sdei.farmx.R;

public class NothingSelectedSpinnerAdapter implements SpinnerAdapter, ListAdapter {

    private static final int EXTRA = 1;
    private SpinnerAdapter adapter;
    private Context context;
    private int nothingSelectedLayout;
    private LayoutInflater layoutInflater;

    private String hintText;
    private int textColor;

    /**
     * Use this constructor to have NO 'Select One...' item, instead use
     * the standard prompt or nothing at all.
     *
     * @param spinnerAdapter        wrapped Adapter.
     * @param nothingSelectedLayout layout for nothing selected, perhaps
     *                              you want text grayed out like a prompt...
     * @param context
     */
    public NothingSelectedSpinnerAdapter(
            SpinnerAdapter spinnerAdapter,
            int nothingSelectedLayout,
            String hintText,
            int textColor,
            Context context) {
        this.textColor = textColor;
        this.hintText = hintText;
        this.adapter = spinnerAdapter;
        this.context = context;
        this.nothingSelectedLayout = nothingSelectedLayout;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        // This provides the View for the Selected Item in the Spinner, not
        // the dropdown (unless dropdownView is not set).
        if (position == 0) {
            return getNothingSelectedView(parent);
        }
        return adapter.getView(position - EXTRA, null, parent); // Could re-use
        // the convertView if possible.
    }

    /**
     * View to show in Spinner with Nothing Selected
     * Override this to do something dynamic... e.g. "37 Options Found"
     *
     * @param parent
     * @return
     */
    private View getNothingSelectedView(ViewGroup parent) {
        View view = layoutInflater.inflate(nothingSelectedLayout, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.text1);
        textView.setText(hintText);

        if (textColor != 0)
            textView.setTextColor(ContextCompat.getColor(context, textColor));

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Android BUG! http://code.google.com/p/android/issues/detail?id=17128 -
        // Spinner does not support multiple view types
        if (position == 0) {
            return new View(context);
        }

        // Could re-use the convertView if possible, use setTag...
        return adapter.getDropDownView(position - EXTRA, null, parent);
    }

    @Override
    public int getCount() {
        int count = adapter.getCount();
        return count == 0 ? 0 : count + EXTRA;
    }

    @Override
    public Object getItem(int position) {
        return position == 0 ? null : adapter.getItem(position - EXTRA);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position >= EXTRA ? adapter.getItemId(position - EXTRA) : position - EXTRA;
    }

    @Override
    public boolean hasStableIds() {
        return adapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return adapter.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        adapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        adapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0; // Don't allow the 'nothing selected'
        // item to be picked.
    }

    public ArrayAdapter<String> getAdapter(){
        return (ArrayAdapter<String>) adapter;
    }

}
