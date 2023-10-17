package com.app.tunamusic;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Animation du logo et le nom pendant 2s
        ImageView logo = findViewById(R.id.logo);
        logo.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));

        TextView title = findViewById(R.id.appTitle);
        title.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));

        // Passage vers Page2Activity apres 2,5 secondes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), Page2Activity.class);
                startActivity(intent);
            }
        },2500);



    }
}