package com.example.pracprac;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.io.IOException;
import java.net.URI;

public class Admin extends AppCompatActivity{

    private static final int PICK_IMAGE=101;
    ImageView imageView;
    EditText editText ;
    Uri uriProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        findViewById(R.id.cameraImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        editText=findViewById(R.id.userNameEditText);
        findViewById(R.id.saveInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
                uriProfileImage=data.getData();
                Log.i("value",uriProfileImage.getEncodedPath().toString());
           // mStorageRef = FirebaseStorage.getInstance().getReference();
            try {
               Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
               imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.i("ERROR!",Integer.toString(requestCode));
                e.printStackTrace();
            }
        }
    }


}
