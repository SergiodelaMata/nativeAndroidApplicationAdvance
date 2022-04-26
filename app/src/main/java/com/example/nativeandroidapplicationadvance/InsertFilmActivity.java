package com.example.nativeandroidapplicationadvance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private InsertFilmActivity insertFilmActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        insertFilmActivity = this;
        setContentView(R.layout.activity_insert_film);

        TextView titleTextView = findViewById(R.id.titleFilm);
        ImageView posterImageView = findViewById(R.id.imageFilm);
        EditText yearEditText = findViewById(R.id.yearProductionFilm);
        EditText durationEditText = findViewById(R.id.durationFilm);
        EditText genreEditText = findViewById(R.id.genreFilm);
        EditText listActorEditText = findViewById(R.id.listActorsFilm);
        EditText mainActorEditText = findViewById(R.id.actorFilm);
        EditText otherActorsEditText = findViewById(R.id.actorsFilm);
        EditText directorEditText = findViewById(R.id.directorFilm);
        EditText countryEditText = findViewById(R.id.countryFilm);
        EditText originalLanguageEditText = findViewById(R.id.originalLanguageFilm);


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
                            Singleton.setFilm(film);

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
        yearEditText.setText(Singleton.getFilm().getYear());
        durationEditText.setText(Singleton.getFilm().getDuration());
        genreEditText.setText(Singleton.getFilm().getGenres());
        String listActorsText = Singleton.getFilm().getActors();
        String[] listActors = listActorsText.split(",");
        StringBuilder listOtherActors = new StringBuilder();
        for(int i = 1; i < listActors.length; i++)
        {
            if(i == 1)
            {
                listOtherActors = new StringBuilder(listActors[i]);
            }
            else
            {
                listOtherActors.append(",").append(listActors[i]);
            }
        }
        listActorEditText.setText(listActorsText);
        mainActorEditText.setText(listActors[0]);
        otherActorsEditText.setText(listOtherActors.toString());
        directorEditText.setText(Singleton.getFilm().getDirector());
        countryEditText.setText(Singleton.getFilm().getCountryMade());
        originalLanguageEditText.setText(Singleton.getFilm().getOriginalLanguage());

        Singleton.setFinish(false);
        Thread threadImage = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                try
                {
                    if(!Singleton.getFilm().getImagePoster().equals("N/A"))
                    {
                        url = new URL(Singleton.getFilm().getImagePoster());
                        connection = (HttpURLConnection) url
                                .openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap bmp = BitmapFactory.decodeStream(input);
                        posterImageView.setImageBitmap(bmp);
                    }
                    else
                    {
                        posterImageView.setImageResource(R.drawable.film);
                        posterImageView.setBackground(ContextCompat.getDrawable(insertFilmActivity, R.drawable.not_rounded_rectangle_white_no_border));
                    }
                } catch (Exception e) {
                    Log.e(LOG, e.getMessage());
                }
                finally {
                    if (connection != null) {
                        connection.disconnect();
                        Singleton.setFinish(true);
                        notifyFinishInsertDataFilm();
                    }
                }

            }
        });
        threadImage.start();
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