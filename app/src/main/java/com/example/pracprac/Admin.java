package com.example.pracprac;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.net.URI;

public class Admin extends AppCompatActivity{

    private static final int PICK_IMAGE=101;
    ImageView imageView;
    EditText nameEditText ;
    Button submit;
    Uri uriProfileImage;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    ProgressBar ImageProgressBar;
    String profileImageUrl;
    Toolbar toolbar;
    private StorageReference mStorageRef;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        imageView=findViewById(R.id.cameraImageView);
        submit=findViewById(R.id.saveInfoButton);
        nameEditText=findViewById(R.id.userNameEditText);
        progressBar=findViewById(R.id.progressbar);
        mAuth=FirebaseAuth.getInstance();
        ImageProgressBar=findViewById(R.id.Imageprogressbar);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Green Energy");
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
               updateInfo();
           }
       });
    }

    private void updateInfo()
    {
        String name=nameEditText.getText().toString().trim();
        if(name==null)
        {
            nameEditText.setError("Enter a Name");
            nameEditText.requestFocus();
            return;
        }

        //Now we have to store this to firebase for particular Login user

        FirebaseUser user=mAuth.getCurrentUser();
        if(profileImageUrl==null)
        {
            Toast.makeText(this,"Please choose a profile image",Toast.LENGTH_SHORT).show();
            return;
        }
            UserProfileChangeRequest profile=new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build()
                    ;
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        finish();
                        startActivity(new Intent(Admin.this,ChooseEnergy.class));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Admin.this,"Profile cannot be Updated",Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void uploadImage()
    {


       mStorageRef = FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        if(uriProfileImage!=null)
        {

            mStorageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            profileImageUrl = uri.toString();

                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ImageProgressBar.setProgress(0);

                                }
                            },500);
                            Toast.makeText(Admin.this,"Uploaded SUccessfully",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(Admin.this,"Upload Failed",Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(taskSnapshot.getBytesTransferred()*100)/taskSnapshot.getTotalByteCount();
                    ImageProgressBar.setProgress((int)progress);
                }
            });
        }
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
                uploadImage();

            } catch (Exception e) {
                Log.i("ERROR!",Integer.toString(requestCode));
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.select_toolbar,menu);

        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logOutMenu:
                mAuth.signOut();
                finish();
                startActivity(new Intent(Admin.this,SignIn.class));
                break;
            case R.id.helPMenu:
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser().getDisplayName()!=null)
        {
            startActivity(new Intent(Admin.this,ChooseEnergy.class));
        }
    }
}
