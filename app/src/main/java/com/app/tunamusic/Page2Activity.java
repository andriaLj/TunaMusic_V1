package com.app.tunamusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Page2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);


        // passage dans l'activity connexion
        Button connexion = findViewById(R.id.bt_connexion);
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConnexionActivity.class);
                startActivity(intent);
            }
        });



        // passage dans l'activity inscription
        Button inscrire = findViewById(R.id.bt_inscrire);
        inscrire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InscriptionActivity.class);
                startActivity(intent);
            }
        });

        // quitte l'application
        ImageView retour = findViewById(R.id.bt_retour);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                onDestroy();
            }
        });




    }
}