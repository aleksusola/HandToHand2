package com.aleksus.handtohand;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;


public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView mImageView;

    public DownloadImageTask(ImageView imageView) {
        this.mImageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String avatar = urls[0];
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(avatar).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        mImageView.setImageBitmap(result);
    }
}

