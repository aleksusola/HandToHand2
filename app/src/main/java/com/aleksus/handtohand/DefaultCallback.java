package com.aleksus.handtohand;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;


public class DefaultCallback<T> extends BackendlessCallback<T> {
    private Context mContext;
    private ProgressDialog mProgressDialog;


    protected DefaultCallback(Context context) {
        this.mContext = context;
        mProgressDialog = ProgressDialog.show(context, "", "Подождите...", true);
    }

    protected DefaultCallback(Context context, String message) {
        this.mContext = context;
        mProgressDialog = ProgressDialog.show(context, "", message, true);
    }

    @Override
    public void handleResponse(T response) {
        mProgressDialog.cancel();
    }

    @Override
    public void handleFault(BackendlessFault fault) {
        mProgressDialog.cancel();
        Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_LONG).show();
    }
}
