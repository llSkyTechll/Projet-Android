package com.example.memoryproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GameGrid extends AppCompatActivity {

    Intent intent;
    int gridSize;
    GridLayout gridLayout;
    ImageView imgView;
    ImageView imgViewBack;
    ArrayList<String> uriStringList;
    ArrayList<Uri> uriList;
    int screenWidth;
    int screenHeight;
    int imageRevealed = 0;
    Animation animFadeOut;
    Animation animFadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_grid);

        intent         = getIntent();
        gridSize       = intent.getIntExtra("gridSize", 4);
        uriStringList  = intent.getStringArrayListExtra("pictures");
        uriList        = new ArrayList<Uri>();
        gridLayout     = findViewById(R.id.gridLayoutGame);
        animFadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        animFadeIn  = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        restartGrid();
        detectScreenSize();
        createImages();
        resizeGrid();
        duplicateImages();
        addImageViews();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeImagesInvisible();
            }
        }, 5000);
    }

    private void makeImagesInvisible(){
        for (int x = 0; x < uriList.size(); x++){
            imgViewBack = findViewById(x);
            imgViewBack.setImageAlpha(0);
        }
    }

    private void restartGrid() {
           gridLayout.removeAllViews();
    }

    private void detectScreenSize(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth  = displayMetrics.widthPixels;
    }

    private void resizeGrid(){
        gridLayout.setColumnCount((int) Math.sqrt(gridSize));
        gridLayout.setBackgroundColor(Color.RED);
    }

    private void createImages(){
        for (String stringFile: uriStringList) {
            Uri uri = Uri.parse(stringFile);
            uriList.add(uri);
        }
    }
    
    private void addImageViews() {
        int uriId = 0;
        for (Uri uri: uriList ) {
            configureImageView(uri, uriId);
            uriId++;
        }
    }

    private void configureImageView(Uri uri, int uriId){
        imgView = new ImageView(this);
        imgView.setId(uriId);
        imgView.setImageURI(uri);
        imgView.setMaxWidth(screenWidth / gridLayout.getColumnCount());
        imgView.setMaxHeight(imgView.getMaxWidth());
        imgView.setAdjustViewBounds(true);
        imgView.setPadding(1,1,1,1);
        imgView.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           ImageView imageView = v.findViewById(v.getId());
                                           //imageView.setImageAlpha(255);
                                           Log.d("test", "onClick: TEST");
                                           imgView.startAnimation(animFadeIn);
                                           if (getImageRevealed() >= 2){

                                           }
                                           else{

                                           }
                                       }
                                   });
        imgView.invalidate();
        gridLayout.addView(imgView);
    }

    private int getImageRevealed(){
        return imageRevealed++;
    }

    private void duplicateImages(){
        int uriListSize = uriList.size();
        for (int i = 0; i < uriListSize; i++){
            uriList.add(uriList.get(i));
        }
        shuffleImageList();
    }

    private void shuffleImageList(){
        Collections.shuffle(uriList);
    }
    
}
