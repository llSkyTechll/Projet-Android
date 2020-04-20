package com.example.memoryproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PictureChoice extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_PERMISSION_CAMERA = 100;
    Button buttonLoadImage;
    Button btn_takePicture;
    ImageView imageView;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_choice);

        imageView = (ImageView)findViewById(R.id.imgView);
        buttonLoadImage = findViewById(R.id.buttonLoadPicture);
        btn_takePicture = findViewById(R.id.btn_takePicture);
        setListener();
    }

    private void setListener(){
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permission();
                openGallery();
            }
        });

        btn_takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });
    }

    private void requestCameraPermission(){
        if (ContextCompat.checkSelfPermission(this , Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this , new String[] {Manifest.permission.CAMERA}, 1);
        }
    }

    private  void permission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RESULT_PERMISSION_CAMERA);
            return;
        }
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select pictures"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMAGE) {
            selectedImage = data.getData();
            imageView.setImageURI(selectedImage);
        }
    }
}
