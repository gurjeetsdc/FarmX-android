package com.sdei.farmx.callback;

import com.sdei.farmx.apimanager.ApiResponse;

public interface ApiServiceCallback {

    void onSuccess(int apiIndex, ApiResponse response);

    void onException(int apiIndex, Throwable t);

    void onError(int apiIndex, String message);

}
