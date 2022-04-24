package com.example.nativeandroidapplicationadvance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;

import com.example.nativeandroidapplicationadvance.db.DBManager;
import com.example.nativeandroidapplicationadvance.db.Film;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Etiqueta logcat
    private static final String LOG = "MainActivity";
    private TableLayout tableLayout;
    private int[] headerTable = Singleton.getInstance().getHeaderTableTextFilms();
    private ArrayList<Film> rows;
    private DBManager dbManager;
    private Button buttonAddNewFilm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbManager = new DBManager(this.getApplicationContext());
        tableLayout = findViewById(R.id.tableFilms);
        rows = new ArrayList<>();
        MainDynamicTable dynamicTable = new MainDynamicTable(this, tableLayout, this.getApplicationContext());
        dynamicTable.addHeader(headerTable);
        dynamicTable.addData(getData());

        buttonAddNewFilm = findViewById(R.id.addNewFilm);
        buttonAddNewFilm.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SelectInsertFilmActivity.class);
            this.startActivity(intent);
            finish();
        });

    }

    public ArrayList<Film> getData()
    {
        rows = dbManager.getListFilms();
        return rows;
    }

    public ArrayList<Film> getRows() {
        return rows;
    }

    public void setRows(ArrayList<Film> rows)
    {
        this.rows = rows;
    }

}