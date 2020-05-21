package com.example.memoryproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Picture;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.memoryproject.Notification.NotificationService;

public class GridChoice extends AppCompatActivity {

    Button btn_2x2;
    Button btn_4x4;
    Button btn_6x6;
    int picturesRequired = 0;
    int gridSize = 0;
    private NotificationService notificationService;
    Boolean isChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_choice);

        notificationService = new NotificationService();
        isChoice = false;
        btn_2x2 = findViewById(R.id.btn_2x2);
        btn_4x4 = findViewById(R.id.btn_4x4);
        btn_6x6 = findViewById(R.id.btn_6x6);
        setListener();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (!isChoice){
            notificationService.NotificationBuilder(this,"Memory project","Get back!!!");
        }
        isChoice = false;
    }
    private void setListener(){
        btn_2x2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChoice = true;
                adjustGridChoice(2, 4);
            }
        });

        btn_4x4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isChoice = true;
                adjustGridChoice(8, 16);

            }
        });

        btn_6x6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isChoice = true;
                adjustGridChoice(18, 36);
            }
        });
    }

    private void adjustGridChoice(int inPicturesRequired, int inGridSize){
        picturesRequired = inPicturesRequired;
        gridSize         = inGridSize;
        relocatePictureChoice();
    }

    private void relocatePictureChoice(){
        Intent pictureChoiceIntent = new Intent(this, PictureChoice.class);
        pictureChoiceIntent.putExtra("picturesRequired", picturesRequired);
        pictureChoiceIntent.putExtra("gridSize", gridSize);
        startActivity(pictureChoiceIntent);
    }

    private void showMessage(String message){
        Toast.makeText(GridChoice.this, message, Toast.LENGTH_SHORT).show();
    }
}
