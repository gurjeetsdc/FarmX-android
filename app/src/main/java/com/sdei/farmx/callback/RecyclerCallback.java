package com.sdei.farmx.callback;

public interface RecyclerCallback {

    void onItemClick(int position);

    void onChildItemClick(int parentIndex, int childIndex);

}
