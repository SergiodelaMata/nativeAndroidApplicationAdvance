package com.example.nativeandroidapplicationadvance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

import com.example.nativeandroidapplicationadvance.db.DBManager;
import com.example.nativeandroidapplicationadvance.db.Film;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SelectInsertFilmActivity extends AppCompatActivity {
    //Etiqueta logcat
    private static final String LOG = "SelInsFilmActivity";
    private static final ExecutorService execService = Executors.newSingleThreadExecutor();
    private TableLayout tableLayout;
    private int[] headerTable = Singleton.getInstance().getHeaderTableTextFilms();
    private ArrayList<Film> rows;
    private DBManager dbManager;
    private EditText inputTitleFilm;
    private Button buttonSearchFilm;
    private Button buttonPreviousPage;
    private Button buttonNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_insert_film);
        this.inputTitleFilm = findViewById(R.id.titleFilm);
        this.buttonSearchFilm = findViewById(R.id.insertNewSubjectButton);
        this.buttonPreviousPage = findViewById(R.id.previousPageButton);
        this.buttonNextPage = findViewById(R.id.nextPageButton);
        dbManager = new DBManager(this.getApplicationContext());
        tableLayout = findViewById(R.id.tableFilms);
        rows = new ArrayList<>();
        SelectInsertFilmDynamicTable dynamicTable = new SelectInsertFilmDynamicTable(this, tableLayout, this.getApplicationContext());
        dynamicTable.addHeader(headerTable);
        //dynamicTable.addData(getData());

        buttonSearchFilm.setOnClickListener(v -> {
            String url = "www.omdbapi.com";
            String apiKey = "c4b05ab";
            String title = inputTitleFilm.getText().toString();
            String[] titleArray = title.split(" ");
            StringBuilder titleText = new StringBuilder();
            for (int i = 0; i < titleArray.length; i++)
            {
                if(i == 0)
                {
                    titleText.append(titleArray[i]);
                }
                else
                {
                    titleText.append("+").append(titleArray[i]);
                }
            }
            String content = "";
            HttpURLConnection urlConnection = null;
            String finalTitleText = titleText.toString();
            Log.i(LOG, titleText.toString());
            Singleton.setFinish(false);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection urlConnection = null;
                    String content = "";
                    try {
                        //Realiza una búsqueda inicial del listado de películas que encajan a partir de un título
                        URL urlSearch = new URL("https://" + url + "/?apikey=" + apiKey + "&s=" + finalTitleText + "&type=movie&page=1");
                        urlConnection = (HttpURLConnection) urlSearch.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setRequestProperty("Content-Type", "application/json");
                        urlConnection.setRequestProperty("Accept", "application/json");
                        urlConnection.setReadTimeout(10000);
                        urlConnection.setConnectTimeout(15000);

                        int statusCode = urlConnection.getResponseCode();
                        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            Log.d(LOG, "HTTP_UNAUTHORIZED");
                        }
                        else if (statusCode == HttpURLConnection.HTTP_OK) {
                            InputStream inputStream = urlConnection.getInputStream();
                            content = new Scanner(inputStream).useDelimiter("\\A").next();
                            JSONObject jsonObject = new JSONObject(content);
                            if(jsonObject.getString("Response").equals("True"))
                            {
                                Log.i(LOG, "Response: " + jsonObject.getString("Response"));
                                JSONArray jsonArray = jsonObject.getJSONArray("Search");
                                ArrayList<Film> films = new ArrayList<>();
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    Film film = new Film();
                                    JSONObject jsonObjectFilm = jsonArray.getJSONObject(i);
                                    film.setTitle(jsonObjectFilm.getString("Title"));
                                    film.setImagePoster(jsonObjectFilm.getString("Poster"));
                                    film.setYear(jsonObjectFilm.getString("Year"));
                                    films.add(film);
                                }
                                Singleton.setListFilms(films);
                            }
                            else
                            {
                                Log.i(LOG, "No se encontraron películas");
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(LOG, "Error: " + e.getMessage());
                    }
                    finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                            Singleton.setFinish(true);
                            dynamicTable.notifyFinishBuildStructureTable();
                        }
                    }

                }
            });
            dynamicTable.resetTable();
            dynamicTable.addHeader(headerTable);
            thread.start();
            dynamicTable.waitFinishThread();
            dynamicTable.addData(getData());
        });
    }


    public ArrayList<Film> getData()
    {
        rows = Singleton.getListFilms();
        return rows;
    }

    public ArrayList<Film> getRows() {
        return rows;
    }

    public void setRows(ArrayList<Film> rows)
    {
        this.rows = rows;
    }



    /*
        Establece la redirección si se presiona el botón para ir hacia atrás a la actividad principal
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.startActivity(new Intent(SelectInsertFilmActivity.this, MainActivity.class));
        this.finish();
    }
}