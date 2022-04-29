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

    /**
     * Método que se ejecuta al crear la actividad
     * @param savedInstanceState Bundle de los datos guardados de la instancia anterior
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbManager = new DBManager(this.getApplicationContext());
        tableLayout = findViewById(R.id.tableFilms);
        rows = new ArrayList<>();
        //Se elabora la estructura de la tabla con las cabeceras y los datos de la base de datos de las películas
        MainDynamicTable dynamicTable = new MainDynamicTable(this, tableLayout, this.getApplicationContext());
        dynamicTable.addHeader(headerTable);
        dynamicTable.addData(getData());

        /*
            Se crea el listener para el botón de añadir una nueva película para acceder a la actividad centrada
            en la que se puede añadir una nueva película
         */
        buttonAddNewFilm = findViewById(R.id.addNewFilm);
        buttonAddNewFilm.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SelectInsertFilmActivity.class);
            this.startActivity(intent);
            finish();
        });
    }

    /**
     * Devuelve los datos de la base de datos de las películas
     * @return ArrayList de las películas con los datos de la base de datos
     */
    public ArrayList<Film> getData()
    {
        rows = dbManager.getListFilms();
        return rows;
    }

    /**
     * Obtiene el listado de filas de las películas que se dispone para introducir en la tabla
     * @return listado de filas de las películas que se dispone para introducir en la tabla
     */
    public ArrayList<Film> getRows() {
        return rows;
    }

    /**
     * Inserta el listado de filas de las películas que se dispondrá en la tabla
     * @param rows listado de las películas que se dispondrá en la tabla
     */
    public void setRows(ArrayList<Film> rows)
    {
        this.rows = rows;
    }

}