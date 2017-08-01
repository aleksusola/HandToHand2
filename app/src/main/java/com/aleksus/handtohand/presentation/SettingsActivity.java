package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aleksus.handtohand.DownloadImageTask;
import com.aleksus.handtohand.R;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    private ImageView iconChange;

    private Button saveButton;
    private Button iconChangeButton;

    private Bitmap avatar;
    static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        iconChange = (ImageView) findViewById(R.id.iconChange);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
        iconChangeButton = (Button) findViewById(R.id.icon_change_button);
        iconChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeButtonClicked();
            }
        });
        BackendlessUser user = Backendless.UserService.CurrentUser();
        if( user != null ) {
            new DownloadImageTask(iconChange).execute(user.getProperty( "avatar" ).toString());
        }
    }

    private void onChangeButtonClicked() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        avatar = null;
        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        avatar = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    iconChange.setImageBitmap(avatar);
                }
        }
    }



    private void onSaveButtonClicked() {

        final BackendlessUser user = Backendless.UserService.CurrentUser();
        Backendless.Files.remove( "icons/" +user.getProperty("login")+ "_user.png", new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
            }
        });
        Backendless.Files.Android.upload( avatar, Bitmap.CompressFormat.PNG, 10, user.getProperty("login") +"_user.png", "icons", new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile backendlessFile) {
            user.setProperty( "avatar", backendlessFile.getFileURL() );
            Backendless.UserService.update( user, new AsyncCallback<BackendlessUser>() {
                public void handleResponse( BackendlessUser user ) {
                    finish();
                }

                public void handleFault( BackendlessFault fault ) {
                    Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
                }
            });
            }
            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(SettingsActivity.this, backendlessFault.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
