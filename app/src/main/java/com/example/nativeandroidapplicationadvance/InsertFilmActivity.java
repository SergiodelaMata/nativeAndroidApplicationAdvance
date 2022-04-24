package com.example.nativeandroidapplicationadvance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class InsertFilmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_film);
    }

    /*
        Establece la redirección si se presiona el botón para ir hacia atrás a la actividad principal
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.startActivity(new Intent(InsertFilmActivity.this, SelectInsertFilmActivity.class));
        this.finish();
    }

}