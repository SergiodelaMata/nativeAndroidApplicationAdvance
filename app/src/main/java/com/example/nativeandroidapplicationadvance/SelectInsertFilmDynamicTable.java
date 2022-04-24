package com.example.nativeandroidapplicationadvance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.widget.ButtonBarLayout;
import androidx.core.content.ContextCompat;

import com.example.nativeandroidapplicationadvance.db.Film;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SelectInsertFilmDynamicTable {
    //Etiqueta logcat
    private static final String LOG = "SIFilmDynTable";
    private final SelectInsertFilmActivity selectInsertFilmActivity;
    private final TableLayout tableLayout;
    private final Context context;
    private int[] header;
    private ArrayList<Film> data;
    private TableRow tableRow;
    private TextView textCell;

    private int indexCell;
    private int indexRow;

    public SelectInsertFilmDynamicTable(SelectInsertFilmActivity selectInsertFilmActivity, TableLayout tableLayout, Context context) {
        this.selectInsertFilmActivity = selectInsertFilmActivity;
        this.tableLayout = tableLayout;
        this.context = context;
    }

    /**
     * Añade los textos que aparecerán en la cabecera de la tabla
     * @param header Textos que aparecerán como cabeceras
     */
    public void addHeader(int[] header)
    {
        this.header = header;
        createHeader();
    }

    /**
     * Añade los datos de las películas que aparecerán en la tabla
     * @param data Datos de las distintas películas que aparecerán en el cuerpo de la tabla
     */
    public void addData(ArrayList<Film> data)
    {
        this.data = data;
        createDataTable();
    }

    /**
     * Crea una nueva fila de la tabla
     */
    public void newRow()
    {
        tableRow = new TableRow(context);
        tableRow.setWeightSum(1);
    }

    /**
     * Crea una nueva celda para la tabla
     */
    public void newCellHeader()
    {
        textCell = new TextView(context);
        textCell.setGravity(Gravity.CENTER);
        textCell.setTextSize(18);
        textCell.setPadding(5,5,5,5);
        textCell.setTextColor(Color.parseColor("#000000"));
    }

    /**
     * Crea una nueva celda para la tabla
     */
    public void newCell()
    {
        textCell = new TextView(context);
        textCell.setGravity(Gravity.CENTER);
        textCell.setTextSize(22);
        textCell.setTextColor(Color.parseColor("#000000"));
        textCell.setWidth((int) (getScreenWidth() * 0.20));
    }

    /**
     * Obtiene el ancho de la pantalla del dispositivo
     * @return Valor en píxeles del ancho de la pantalla
     */
    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        selectInsertFilmActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * Crea la cabecera de la tabla
     */
    public void createHeader()
    {
        indexCell = 0;
        newRow();
        while(indexCell < header.length)
        {
            newCellHeader();

            textCell.setText(context.getResources().getString(header[indexCell++]));
            textCell.setBackground(ContextCompat.getDrawable(selectInsertFilmActivity, R.drawable.not_rounded_rectangle_white_border_gray));
            if(indexCell == 1)
            {
                textCell.setWidth((int) (getScreenWidth() * 0.35));
                textCell.setGravity(Gravity.CENTER);
            }
            else if(indexCell == 2)
            {
                textCell.setWidth((int) (getScreenWidth() * 0.59));
                textCell.setGravity(Gravity.CENTER);
            }
            tableRow.addView(textCell, newTableRowParamsHeader());
        }
        tableLayout.addView(tableRow);
    }

    /**
     * Crea el cuerpo de la tabla
     */
    private void createDataTable() {
        ArrayList<TableRow> listTableRow = new ArrayList<>();
        Singleton.setFinish(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(indexRow = 0; indexRow < data.size(); indexRow++) {
                    newRow();
                    Film film = data.get(indexRow);
                    String finalInfo = String.valueOf(film.getImagePoster());

                    newCell();
                    URL url = null;
                    HttpURLConnection connection = null;
                    try {
                        url = new URL(finalInfo);
                        connection = (HttpURLConnection) url
                                .openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap bmp = BitmapFactory.decodeStream(input);
                        ImageButton imageButton = new ImageButton(context);
                        imageButton.setImageBitmap(bmp);
                        tableRow.addView(imageButton, newTableRowParams());
                    } catch (Exception e) {
                        Log.e(LOG, e.getMessage());
                        ImageButton imageButton = new ImageButton(context);
                        tableRow.addView(imageButton, newTableRowParams());
                    }
                    newCell();
                    Button buttonFilm = new Button(context);
                    buttonFilm.setId(film.getIdFilm());
                    buttonFilm.setText(film.getTitle());
                    buttonFilm.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    buttonFilm.setTextColor(Color.parseColor("#000000"));
                    //buttonFilm.setBackground(ContextCompat.getDrawable(selectInsertFilmActivity, R.drawable.rounded_rectangle_white));
                    buttonFilm.setTextSize(16);
                    buttonFilm.setGravity(Gravity.CENTER);
                    buttonFilm.setWidth((int) (getScreenWidth() * 0.58));
                    buttonFilm.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                    //buttonFilm.setLayoutParams(new ViewGroup.LayoutParams((int) (getScreenWidth() * 0.58), 100));
                    buttonFilm.setBackground(ContextCompat.getDrawable(selectInsertFilmActivity, R.drawable.not_rounded_rectangle_white_no_border));
                    buttonFilm.setAllCaps(false);
                    tableRow.addView(buttonFilm, newTableRowParams());
                    //Se establece el acceso a los datos de la asignatura a través de su botón
                    buttonFilm.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("idFilm", film.getIdFilm());
                        Intent intent = new Intent(selectInsertFilmActivity, InsertFilmActivity.class);
                        intent.putExtras(bundle);
                        selectInsertFilmActivity.startActivity(intent);
                        selectInsertFilmActivity.finish();
                    });
                    tableRow.setGravity(Gravity.CENTER_VERTICAL);
                    tableRow.setBackground(ContextCompat.getDrawable(selectInsertFilmActivity, R.drawable.rounded_rectangle_white_border_gray));

                    listTableRow.add(tableRow);
                }
                Singleton.setFinish(true);
                Singleton.setListTableRows(listTableRow);
                notifyFinishBuildStructureTable();
            }
        });
        thread.start();
        buildTable();
    }

    public synchronized void notifyFinishBuildStructureTable() {
        notifyAll();
    }

    public synchronized void waitFinishThread() {
        try {
            while (!Singleton.isFinish()) {
                wait();
            }
        } catch (InterruptedException e) {
            Log.e(LOG, e.getMessage());
        }
    }

    private synchronized void buildTable()
    {
        ArrayList<TableRow> listTableRow = new ArrayList<>();
        try {
            while(!Singleton.isFinish())
            {
                wait();
            }
            listTableRow = Singleton.getListTableRows();
            for(int i = 0; i < listTableRow.size(); i++) {
                tableLayout.addView(listTableRow.get(i));
            }
        }
        catch (InterruptedException e) {
            Log.e(LOG, e.getMessage());
        }


    }

    /**
     * Establece los márgenes de las filas de la tabla para la cabecera
     * @return Ajustes para los márgenes de la fila de la cabecera de la tabla
     */
    private TableRow.LayoutParams newTableRowParamsHeader()
    {
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setMargins(0,0,0,0);
        params.weight = 1;
        return params;
    }

    /**
     * Establece los márgenes de las filas de la tabla
     * @return Ajustes para los márgenes de las filas de la tabla
     */
    private TableRow.LayoutParams newTableRowParams()
    {
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setMargins(5,5,5,5);
        params.weight = 1;
        return params;
    }

    public void resetTable()
    {
        selectInsertFilmActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tableRow.removeAllViews();
                tableLayout.removeAllViewsInLayout();
            }
        });
    }

    public void addHeader()
    {
        selectInsertFilmActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addHeader(Singleton.getInstance().getHeaderTableTextFilms());
            }
        });
    }

    public void addData()
    {
        selectInsertFilmActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                data = new ArrayList<>();
                selectInsertFilmActivity.setRows(new ArrayList<>());
                addData(selectInsertFilmActivity.getData());
            }
        });
    }

}
