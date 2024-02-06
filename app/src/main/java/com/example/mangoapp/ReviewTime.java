package com.example.mangoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;

public class ReviewTime extends AppCompatActivity {

    SeekBar reviewSeekbar;
    ImageView ReviewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_time);

        reviewSeekbar=findViewById(R.id.seekBar);
        ReviewImage=findViewById(R.id.reViewImage);

        reviewSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i<=33){
                    ReviewImage.setImageResource(R.drawable.reviewemoji1);
                }
                else if (i>33&i<=66){
                    ReviewImage.setImageResource(R.drawable.reviewemoji2);
                }
                else {
                    ReviewImage.setImageResource(R.drawable.reviewemoji3);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}