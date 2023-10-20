package com.app.tunamusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ConnexionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);



        // connexion si info correct !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! compte user a implementer
        Button btConncter = findViewById(R.id.bt_connecter);
        btConncter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AccueilActivity.class);
                startActivity(intent);
            }
        });


        // recuperation du mdp
        TextView mdpOublie = findViewById(R.id.mdp_oublie);
        mdpOublie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecupCompteActivity.class);
                startActivity(intent);
            }
        });

        ImageView retour = findViewById(R.id.bt_retour2);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}