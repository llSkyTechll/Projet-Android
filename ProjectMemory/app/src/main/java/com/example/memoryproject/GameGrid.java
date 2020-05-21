package com.example.memoryproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    ImageView imgViewBack;
    ArrayList<String> uriStringList;
    ArrayList<Uri> uriList;
    int screenWidth;
    int screenHeight;
    private GameGrid activity;
    private GamePoints points;
    private NotificationService notificationService;
    int imageRevealed = 0;
    Animation animFadeOut;
    Animation animFadeIn;
    ImageView firstImageRevealed;
    ImageView secondImageRevealed;
    Bitmap firstBitmap;
    Bitmap secondBitmap;
    MediaPlayer confirmationSound;
    AlphaAnimation animationFadeOut;
    Boolean canSelect;
    int pairsToFind;

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
        pairsToFind    = gridSize / 2;
        uriStringList  = intent.getStringArrayListExtra("pictures");
        uriList        = new ArrayList<Uri>();
        gridLayout     = findViewById(R.id.gridLayoutGame);
        confirmationSound = MediaPlayer.create(this, R.raw.confirmationsound);
        createAnimations();
        canSelect = true;
        detectScreenSize();
        createImages();
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

    @Override
    protected void onStop() {
        super.onStop();
            notificationService.NotificationBuilder(this,"Memory project","Ne part pas trop longtemps");
    }

    private void setImageViewsListeners(){
        for (int i = 0; i < uriList.size(); i++){
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
                            if (firstImageRevealed.getId() != imageView.getId()){
                                secondImageRevealed = imageView;
                                compareImages();
                                imageRevealed = 0;
                            }
                        }else{
                            firstImageRevealed = imageView;
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
                firstBitmap  = ((BitmapDrawable)firstImageRevealed.getDrawable()).getBitmap();
                secondBitmap = ((BitmapDrawable)secondImageRevealed.getDrawable()).getBitmap();
                if (firstBitmap == secondBitmap){
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
        firstImageRevealed.setImageResource(R.drawable.ic_launcher_foreground);
        firstImageRevealed.setOnClickListener(null);
        firstImageRevealed.setAnimation(null);
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
        Animation animation = new AlphaAnimation(1f, 0f);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        firstImageRevealed.startAnimation(animation);
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
        for (int x = 0; x < uriList.size(); x++){
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
        gridLayout.addView(imgView);
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        imgView.setPadding(1,1,1,1);
        imgView.getLayoutParams().height = imgView.getMaxWidth();
        imgView.getLayoutParams().width  = imgView.getMaxWidth();
        imgView.invalidate();

        imgView.requestLayout();
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
