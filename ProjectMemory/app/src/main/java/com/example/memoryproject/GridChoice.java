package com.example.memoryproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Picture;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GridChoice extends AppCompatActivity {

    Button btn_2x2;
    Button btn_4x4;
    Button btn_6x6;
    int picturesRequired = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_choice);

        btn_2x2 = findViewById(R.id.btn_2x2);
        btn_4x4 = findViewById(R.id.btn_4x4);
        btn_6x6 = findViewById(R.id.btn_6x6);
        setListener();
    }

    private void setListener(){
        btn_2x2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturesRequired = 2;
                relocatePictureChoice();
            }
        });

        btn_4x4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                picturesRequired = 8;
                relocatePictureChoice();
            }
        });

        btn_6x6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                picturesRequired = 18;
                relocatePictureChoice();
            }
        });
    }

    private void relocatePictureChoice(){
        /*Intent pictureChoiceIntent = new Intent(this, PictureChoice.class);
        pictureChoiceIntent.putExtra("picturesRequired", picturesRequired);
        startActivity(pictureChoiceIntent);*/
    }

    private void showMessage(String message){
        Toast.makeText(GridChoice.this, message, Toast.LENGTH_SHORT).show();
    }
}
