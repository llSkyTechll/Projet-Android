package com.example.memoryproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.memoryproject.Notification.NotificationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GameGrid extends AppCompatActivity {

    Intent intent;
    int gridSize;
    GridLayout gridLayout;
    ImageView imgView;
    ArrayList<String> uriStringList;
    ArrayList<Uri> uriList;
    int screenWidth;
    int screenHeight;
    private GameGrid activity;
    private GamePoints points;
    private NotificationService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_grid);
        this.activity = this;
        points = new GamePoints();
        notificationService = new NotificationService();
        points.setPoints(0);
        intent         = getIntent();
        gridSize       = intent.getIntExtra("gridSize", 4);
        uriStringList  = intent.getStringArrayListExtra("pictures");
        uriList        = new ArrayList<Uri>();
        gridLayout     = findViewById(R.id.gridLayoutGame);
        restartGrid();
        detectScreenSize();
        createImages();
        resizeGrid();
        duplicateImages();
        addImageViews();
    }
    @Override
    protected void onStop() {
        super.onStop();
        notificationService.NotificationBuilder(this,"Memory project","Get back!!!");
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
            imgView = new ImageView(this);
            imgView.setId(uriId);
            imgView.setImageURI(uri);
            imgView.setMaxWidth(screenWidth / gridLayout.getColumnCount());
            imgView.setMaxHeight(imgView.getMaxWidth());
            imgView.setAdjustViewBounds(true);
            imgView.invalidate();
            gridLayout.addView(imgView);
            uriId++;
        }
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

    private void popupCreator(String title, String message){
        AlertDialog.Builder myPopup = new AlertDialog.Builder(activity);
        myPopup.setTitle(title);
        myPopup.setMessage(message);
        myPopup.show();
    }
}
