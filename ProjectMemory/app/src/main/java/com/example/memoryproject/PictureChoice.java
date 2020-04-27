package com.example.memoryproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
    ImageView imageView;
    List<Bitmap> bitmaps;
    ClipData clipData;
    Uri imageUri;
    InputStream inputStream;
    Intent intent;
    int picturesRequired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_choice);
        btnPickImage = findViewById(R.id.btn_pickImage);
        btnStartGame = findViewById(R.id.btn_goToGame);
        intent = getIntent();
        picturesRequired = intent.getIntExtra("picturesRequired",2);
        bitmaps = new ArrayList<>();

        setListener();
    }
    private void setListener(){
        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

    private void startGame(){
        Intent startGameIntent = new Intent(this, GameGrid.class);
        startGameIntent.putExtra("gridSize", intent.getIntExtra("gridSize", 4));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getImages(requestCode, resultCode, data);
        imageThread();
    }

    private void getImages(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == 1 && resultCode == RESULT_OK){
            imageView = findViewById(R.id.imageView);

            clipData = data.getClipData();
            if (clipData !=null){
                for (int i = 0; i < clipData.getItemCount();i++){
                    if ( clipData.getItemCount() <= picturesRequired){
                        imageUri = clipData.getItemAt(i).getUri();
                        picturesRequired--;
                    try {
                        inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        bitmaps.add(bitmap);
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
                    bitmaps.add(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void imageThread(){
        new Thread(new Runnable() {
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
        }).start();
    }

    private void showMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
