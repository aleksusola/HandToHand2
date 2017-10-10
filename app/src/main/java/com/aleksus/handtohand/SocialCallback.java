
package com.aleksus.handtohand;

import android.content.Context;
import android.widget.Toast;

import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;

public abstract class SocialCallback<T> extends BackendlessCallback<T> {
    private Context mContext;

    protected SocialCallback(Context context) {
        this.mContext = context;
    }

    @Override
    public void handleFault(BackendlessFault fault) {
        Toast.makeText(mContext, fault.getMessage(), Toast.LENGTH_LONG).show();
    }
}
            