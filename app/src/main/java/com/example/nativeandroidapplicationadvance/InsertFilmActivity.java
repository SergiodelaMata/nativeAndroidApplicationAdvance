package com.example.nativeandroidapplicationadvance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.nativeandroidapplicationadvance.db.Film;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class InsertFilmActivity extends AppCompatActivity {
    //Etiqueta logcat
    private static final String LOG = "InsFilmActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_film);

        TextView titleTextView = findViewById(R.id.titleFilm);

        Intent intentSaved = getIntent();
        Bundle bundle = intentSaved.getExtras();
        Film film = new Film();
        film.setTitle(bundle.getString("title"));
        titleTextView.setText(film.getTitle());

        String url = "www.omdbapi.com";
        String apiKey = "c4b05ab";
        String title = film.getTitle();
        String[] titleArray = title.split(" ");
        StringBuilder titleText = new StringBuilder();
        for (int i = 0; i < titleArray.length; i++) {
            if (i == 0) {
                titleText.append(titleArray[i]);
            } else {
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
                    URL urlSearch = new URL("https://" + url + "/?apikey=" + apiKey + "&t=" + finalTitleText);
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
                            Film film = new Film();
                            film.setImagePoster(jsonObject.getString("Poster"));
                            film.setYear(jsonObject.getString("Year"));
                            film.setDuration(jsonObject.getString("Runtime"));
                            film.setGenres(jsonObject.getString("Genre"));
                            film.setDirector(jsonObject.getString("Director"));
                            film.setActors(jsonObject.getString("Actors"));
                            film.setOriginalLanguage(jsonObject.getString("Language"));
                            film.setCountryMade(jsonObject.getString("Country"));
                        }
                        else
                        {
                            Log.i(LOG, "No se encontraron películas");
                        }
                    }
                } catch (Exception e) {
                    Log.e(LOG, "Error: " + e.getMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                        Singleton.setFinish(true);
                        notifyFinishInsertDataFilm();
                    }
                }

            }
        });
        thread.start();
        waitFinishThread();
    }

    private synchronized void notifyFinishInsertDataFilm() {
        notifyAll();
    }

    private synchronized void waitFinishThread() {
        try {
            while (!Singleton.isFinish()) {
                wait();
            }
        } catch (InterruptedException e) {
            Log.e(LOG, e.getMessage());
        }
    }



    /*
        Establece la redirección si se presiona el botón para ir hacia atrás a la actividad principal
     */
    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        this.startActivity(new Intent(InsertFilmActivity.this, SelectInsertFilmActivity.class));
        this.finish();
    }*/

}