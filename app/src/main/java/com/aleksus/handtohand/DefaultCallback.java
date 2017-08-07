package com.aleksus.handtohand;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;


public class DefaultCallback<T> extends BackendlessCallback<T> {
    private Context context;
    private ProgressDialog progressDialog;


    protected DefaultCallback(Context context) {
        this.context = context;
        progressDialog = ProgressDialog.show(context, "", "Подождите...", true);
    }

    protected DefaultCallback(Context context, String message) {
        this.context = context;
        progressDialog = ProgressDialog.show(context, "", message, true);
    }

    @Override
    public void handleResponse(T response) {
        progressDialog.cancel();
    }

    @Override
    public void handleFault(BackendlessFault fault) {
        progressDialog.cancel();
        Toast.makeText(context, fault.getMessage(), Toast.LENGTH_LONG).show();
    }
}
