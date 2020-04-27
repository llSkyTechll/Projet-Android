package com.example.memoryproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class GameGrid extends AppCompatActivity {

    Intent intent;
    int gridSize;
    GridLayout gridLayout;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_grid);

        intent = getIntent();
        gridSize = intent.getIntExtra("gridSize", 4);
        gridLayout = findViewById(R.id.gridLayoutGame);
        createImages();
        resizeGrid();
    }

    private void resizeGrid(){
        gridLayout.setColumnCount((int) Math.sqrt(gridSize));
    }

    private void createImages(){
        for (int i = 0; i < gridSize; i++){
            imgView = new ImageView(this);
            gridLayout.addView(imgView);
        }
    }
}
