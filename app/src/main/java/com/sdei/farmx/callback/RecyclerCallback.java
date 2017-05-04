package com.sdei.farmx.callback;

import android.view.View;

public interface RecyclerCallback {

    void onItemClick(int position);

    void onChildItemClick(int parentIndex, int childIndex);

}
