package com.sdei.farmx.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sdei.farmx.BR;
import com.sdei.farmx.callback.RecyclerCallback;

abstract  class MyBindingBaseAdapter extends RecyclerView.Adapter<MyBindingBaseAdapter.MyViewHolder> {

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;
        private RecyclerCallback callback;

        MyViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.callback = getRecyclerCallback();
        }

        void bind(int position, Object obj) {
            binding.setVariable(BR.item, obj);
            binding.setVariable(BR.callback, callback);
            binding.setVariable(BR.position, position);
            binding.executePendingBindings();
        }

    }

    /**
     * create a new RecyclerView.ViewHolder and initializes
     * some fields to be used by RecyclerView
     * @param parent
     * @param viewType
     * @return the holder
     */
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(
                layoutInflater, viewType, parent, false);
        return new MyViewHolder(binding);
    }

    /**
     * update the RecyclerView.ViewHolder contents with the item at the given position
     * @param holder to update
     * @param position position on which holder to update
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Object obj = getObjForPosition(position);
        holder.binding.getRoot().setTag(position);
        holder.bind(position, obj);
    }

    /**
     * @param position item position
     * @return the view type of the item at position for the purposes of view recycling
     */
    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    protected abstract Object getObjForPosition(int position);

    protected abstract int getLayoutIdForPosition(int position);

    protected abstract RecyclerCallback getRecyclerCallback();

}
