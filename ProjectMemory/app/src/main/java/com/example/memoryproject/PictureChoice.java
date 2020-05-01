package com.example.memoryproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PictureChoice extends AppCompatActivity {

    Button btnPickImage;
    Button btnStartGame;
    Button btnCapture;
    ImageView imageView;
    ArrayList<String> uriList;
    ClipData clipData;
    Uri imageUri;
    private static final int PERMISSION_CODE =1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    InputStream inputStream;
    Intent intent;
    int picturesRequired;
    boolean isCaptured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_choice);
        btnPickImage = findViewById(R.id.btn_pickImage);
        btnStartGame = findViewById(R.id.btn_goToGame);
        btnCapture =findViewById(R.id.btn_takeImage);
        imageView = findViewById(R.id.imageView);
        intent = getIntent();
        picturesRequired = intent.getIntExtra("picturesRequired",2);
        uriList = new ArrayList<>();

        setListener();
    }
    private void setListener(){
        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCaptured = false;
                permission();
                intentContent();
            }
        });

        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startGame();
            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCaptured = true;
                permissionCamera();
            }
        });
    }

    private  void permissionCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

                requestPermissions(permission,PERMISSION_CODE);
            } else {
                openCamera();
            }
        }else {
            openCamera();
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        intentCamera();
    }

    private void intentCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);
    }

    private void startGame(){
        Intent startGameIntent = new Intent(this, GameGrid.class);
        startGameIntent.putExtra("gridSize", intent.getIntExtra("gridSize", 4));

        for (int i = picturesRequired; i > 0; i--){
            Resources resources = this.getResources();
            final int resourceId = resources.getIdentifier("tile" + i, "drawable", this.getPackageName());
            Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(resourceId) + '/' + resources.getResourceTypeName(resourceId) + '/' + resources.getResourceEntryName(resourceId) );
            uriList.add(imageUri.toString());
        }
        startGameIntent.putExtra("pictures", uriList);
        startActivity(startGameIntent);
    }

    private void permission(){
        if (ActivityCompat.checkSelfPermission(PictureChoice.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PictureChoice.this,
                    new  String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            return;
        }
    }

    private void intentContent(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setType("image/*");
        startActivityForResult(intent,1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    openCamera();
                } else {
                    showMessage("Permission denied...");
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isCaptured){
            setImage(resultCode);
        } else {
            getImages(requestCode, resultCode, data);
            imageThread();
        }
    }
    private void setImage(int resultCode){
        if (resultCode == RESULT_OK){
            try {
                inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                uriList.add(imageUri.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void getImages(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == 1 && resultCode == RESULT_OK){
            clipData = data.getClipData();
            if (clipData !=null){
                for (int i = 0; i < clipData.getItemCount();i++){
                    if ( clipData.getItemCount() <= picturesRequired){
                        imageUri = clipData.getItemAt(i).getUri();
                        picturesRequired--;
                        try {
                            inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            uriList.add(imageUri.toString());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }else {
                imageUri = data.getData();
                try {
                    inputStream =getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    uriList.add(imageUri.toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void imageThread(){
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                for (final Bitmap b :bitmaps) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(b);
                        }
                    });
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }

    private void showMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
