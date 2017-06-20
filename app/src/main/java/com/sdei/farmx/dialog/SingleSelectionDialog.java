package com.sdei.farmx.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;

import com.sdei.farmx.R;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.dataobject.SingleSelectionItem;
import com.sdei.farmx.databinding.DialogSingleSelectionBinding;

import java.util.ArrayList;

public class SingleSelectionDialog extends Dialog {

    private RecyclerBindingList<SingleSelectionItem> bindingList;
    private RecyclerCallback callback;

    private String title = null;

    public SingleSelectionDialog(@NonNull Context context,
                                 ArrayList<SingleSelectionItem> arrayList,
                                 RecyclerCallback callback) {
        super(context);
        bindingList = new RecyclerBindingList<>();
        bindingList.setItemsList(arrayList);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogSingleSelectionBinding binding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.dialog_single_selection, null, false);
        setContentView(binding.getRoot());
        binding.setItem(bindingList);
        binding.setHeader(title);
        binding.setItemClickListener(callback);

    }

    public void setHeader(String title) {
        this.title = title;
    }

}
