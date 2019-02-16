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
import android.widget.Button;
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
    EditText nameEditText ;
    Button submit;
    Uri uriProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        imageView=findViewById(R.id.cameraImageView);
        submit=findViewById(R.id.saveInfoButton);
        nameEditText=findViewById(R.id.userNameEditText);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });


       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               saveInfo();
           }
       });
    }

    private void saveInfo()
    {
        String name=nameEditText.getText().toString().trim();
        if(name==null)
        {
            nameEditText.setError("Enter a Name");
            nameEditText.requestFocus();
            return;
        }
        //Now we have to store this to firebase for particular Login user
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.i("ERROR!",Integer.toString(requestCode));
                e.printStackTrace();
            }
        }
    }


}
