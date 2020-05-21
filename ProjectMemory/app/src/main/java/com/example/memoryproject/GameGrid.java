package com.example.memoryproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memoryproject.Notification.NotifWorker;

import java.util.ArrayList;
import java.util.Collections;

public class GameGrid extends AppCompatActivity {

    Intent intent;
    int gridSize;
    GridLayout gridLayout;
    ImageView imgView;
    ImageView imgViewBack;
    ArrayList<String> uriStringList;
    int screenWidth;
    int screenHeight;
    private GameGrid activity;
    private GamePoints points;
    int imageRevealed = 0;
    ImageView firstImageRevealed;
    ImageView secondImageRevealed;
    MediaPlayer confirmationSound;
    AlphaAnimation animationFadeOut;
    Boolean canSelect;
    int pairsToFind;
    TextView txtPoints;
    Boolean isChoice;
    int firstImageRevealedId;
    int secondImageRevealedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_grid);
        this.activity = this;
        points = new GamePoints();
        points.setPoints(0);
        isChoice = false;
        intent         = getIntent();
        gridSize       = intent.getIntExtra("gridSize", 4);
        pairsToFind    = gridSize / 2;
        uriStringList  = intent.getStringArrayListExtra("pictures");
        gridLayout     = findViewById(R.id.gridLayoutGame);
        confirmationSound = MediaPlayer.create(this, R.raw.confirmationsound);
        txtPoints      = findViewById(R.id.textView_points);
        createAnimations();
        canSelect = true;
        detectScreenSize();
        resizeGrid();
        duplicateImages();
        addImageViews();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeImagesInvisible();
                setImageViewsListeners();
            }
        }, 5000);
    }



    private void createAnimations(){
        animationFadeOut = new AlphaAnimation(1f, 0f);
        animationFadeOut.setDuration(1000);
        animationFadeOut.setFillAfter(true);
    }

    private void setImageViewsListeners(){
        for (int i = 0; i < uriStringList.size(); i++){
            imgView = findViewById(i);
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canSelect){
                        ImageView imageView = v.findViewById(v.getId());

                        Animation animation = new AlphaAnimation(0f, 1f);
                        animation.setDuration(1500);
                        animation.setFillAfter(true);
                        imageView.startAnimation(animation);

                        if (imageRevealed == 1){
                            if (firstImageRevealedId != imageView.getId()){
                                secondImageRevealedId = v.getId();
                                compareImages();
                                imageRevealed = 0;
                            }
                        }else{
                            firstImageRevealedId = v.getId();
                            imageRevealed++;
                        }
                    }
                }
            });
        }
    }

    private void compareImages() {
        canSelect = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (uriStringList.get(firstImageRevealedId) == uriStringList.get(secondImageRevealedId)){
                    playSound();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            removeValidPair();
                        }
                    }, 500);
                }else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideWrongAnswer();
                        }
                    }, 500);
                }
            }
        }, 1500);
    }

    private void removeValidPair(){
        points.AddPoints();
        changePoints();
        firstImageRevealed = findViewById(firstImageRevealedId);
        firstImageRevealed.setImageResource(R.drawable.ic_launcher_foreground);
        firstImageRevealed.setOnClickListener(null);
        firstImageRevealed.setAnimation(null);
        secondImageRevealed = findViewById(secondImageRevealedId);
        secondImageRevealed.setImageResource(R.drawable.ic_launcher_foreground);
        secondImageRevealed.setOnClickListener(null);
        secondImageRevealed.setAnimation(null);
        validateEndGame();
        canSelect = true;
    }

    private void validateEndGame(){
        pairsToFind--;
        if (pairsToFind <= 0){
            popupCreator("Victoire", "Félicitation! Partie terminé avec " + points.getPoints() + " points!");
        }
    }

    private void hideWrongAnswer(){
        points.subtractPoints();
        changePoints();
        Animation animation = new AlphaAnimation(1f, 0f);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        firstImageRevealed = findViewById(firstImageRevealedId);
        firstImageRevealed.startAnimation(animation);
        secondImageRevealed = findViewById(secondImageRevealedId);
        secondImageRevealed.startAnimation(animation);
        canSelect = true;
    }

    private void playSound(){
        if (confirmationSound.isPlaying()){
            confirmationSound.seekTo(0);
        }
        confirmationSound.start();
    }

    private void makeImagesInvisible(){
        for (int x = 0; x < uriStringList.size(); x++){
            imgViewBack = findViewById(x);
            imgViewBack.startAnimation(animationFadeOut);
        }
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

    private void addImageViews() {
        int uriId = 0;
        for (String stringFile: uriStringList ) {
            Uri uri = Uri.parse(stringFile);
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
        gridLayout.addView(imgView);
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        imgView.setPadding(1,1,1,1);
        imgView.getLayoutParams().height = imgView.getMaxWidth();
        imgView.getLayoutParams().width  = imgView.getMaxWidth();
        imgView.invalidate();

        imgView.requestLayout();
    }

    private void duplicateImages(){
        int uriListSize = uriStringList.size();
        for (int i = 0; i < uriListSize; i++){
            uriStringList.add(uriStringList.get(i));
        }

        shuffleImageList();
    }

    private void shuffleImageList(){
        Collections.shuffle(uriStringList);
    }

    private void popupCreator(String title, String message){
        AlertDialog.Builder myPopup = new AlertDialog.Builder(activity);
        myPopup.setTitle(title);
        myPopup.setMessage(message);
        myPopup.show();
    }

    private void changePoints(){
        txtPoints.setText(String.valueOf(points.getPoints()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChoice = false;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        isChoice = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isChoice){
            OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(NotifWorker.class)
                    .build();
            WorkManager.getInstance(this).enqueue(uploadWorkRequest);
        }
    }
}
