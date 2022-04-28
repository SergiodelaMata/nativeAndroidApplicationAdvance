package com.example.nativeandroidapplicationadvance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nativeandroidapplicationadvance.db.DBManager;
import com.example.nativeandroidapplicationadvance.db.Film;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Scanner;

public class CheckModifyDeleteActivity extends AppCompatActivity {
    //Etiqueta logcat
    private static final String LOG = "CMDFilmActivity";
    private CheckModifyDeleteActivity checkModifyDeleteActivity;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkModifyDeleteActivity = this;
        setContentView(R.layout.activity_check_modify_delete);

        DBManager dbManager = new DBManager(this);

        TextView idFilmTextFilm = findViewById(R.id.idFilm);
        TextView titleTextView = findViewById(R.id.titleFilm);
        ImageView posterImageView = findViewById(R.id.imageFilm);

        EditText languageSeenFilmEditText = findViewById(R.id.languageSeenFilm);
        EditText dateSeenFilmEditText = findViewById(R.id.dateSeenFilm);
        EditText citySeenFilmEditText = findViewById(R.id.citySeenFilm);
        EditText countrySeenFilmEditText = findViewById(R.id.countrySeenFilm);

        Button buttonModifyDataFilm = findViewById(R.id.modifyDataFilmButton);
        Button buttonDeleteDataFilm = findViewById(R.id.deleteDataFilmButton);

        EditText yearEditText = findViewById(R.id.yearProductionFilm);
        EditText durationEditText = findViewById(R.id.durationFilm);
        EditText genreEditText = findViewById(R.id.genreFilm);
        EditText listActorEditText = findViewById(R.id.listActorsFilm);
        EditText mainActorEditText = findViewById(R.id.actorFilm);
        EditText otherActorsEditText = findViewById(R.id.actorsFilm);
        EditText directorEditText = findViewById(R.id.directorFilm);
        EditText countryEditText = findViewById(R.id.countryFilm);
        EditText originalLanguageEditText = findViewById(R.id.originalLanguageFilm);

        dateSeenFilmEditText.setHint("dd/mm/yyyy");

        buttonModifyDataFilm.setOnClickListener(view -> {
            String languageSeenFilm = languageSeenFilmEditText.getText().toString();
            String dateSeenFilm = dateSeenFilmEditText.getText().toString();
            String citySeenFilm = citySeenFilmEditText.getText().toString();
            String countrySeenFilm = countrySeenFilmEditText.getText().toString();
            if (languageSeenFilm.isEmpty() || dateSeenFilm.isEmpty() || citySeenFilm.isEmpty() || countrySeenFilm.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.AlertNoRequiredData)
                        .setMessage(R.string.MessageNoRequiredData)
                        .show();
            } else {
                Film film = Singleton.getFilm();
                film.setLanguageSeen(languageSeenFilm);
                film.setDateSeen(dateSeenFilm);
                film.setCitySeen(citySeenFilm);
                film.setCountrySeen(countrySeenFilm);
                dbManager.updateFilm(film);
                Intent intent = new Intent(CheckModifyDeleteActivity.this, MainActivity.class);
                this.startActivity(intent);
                finish();
            }
        });

        buttonDeleteDataFilm.setOnClickListener(view -> {
            Intent intentSaved = getIntent();
            Bundle bundle = intentSaved.getExtras();
            int idFilm = bundle.getInt("idFilm");
            dbManager.deleteFilm(idFilm);
            Intent intent = new Intent(CheckModifyDeleteActivity.this, MainActivity.class);
            this.startActivity(intent);
            finish();
        });

        dateSeenFilmEditText.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mMonth = calendar.get(Calendar.MONTH);
            int mYear = calendar.get(Calendar.YEAR);

            datePickerDialog = new DatePickerDialog(checkModifyDeleteActivity, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    dateSeenFilmEditText.setText(date);
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        Intent intentSaved = getIntent();
        Bundle bundle = intentSaved.getExtras();
        int idFilm = bundle.getInt("idFilm");
        Film film = dbManager.getFilm(idFilm);
        Singleton.setFilm(film);
        idFilmTextFilm.setText(String.valueOf(film.getIdFilm()));
        titleTextView.setText(film.getTitle());
        languageSeenFilmEditText.setText(film.getLanguageSeen());
        dateSeenFilmEditText.setText(film.getDateSeen());
        citySeenFilmEditText.setText(film.getCitySeen());
        countrySeenFilmEditText.setText(film.getCountrySeen());
        yearEditText.setText(film.getYear());
        durationEditText.setText(film.getDuration());
        genreEditText.setText(film.getGenres());
        String listActorsText = film.getActors();
        String[] listActors = listActorsText.split(",");
        StringBuilder listOtherActors = new StringBuilder();
        for (int i = 1; i < listActors.length; i++) {
            if (i == 1) {
                listOtherActors = new StringBuilder(listActors[i]);
            } else {
                listOtherActors.append(",").append(listActors[i]);
            }
        }
        listActorEditText.setText(listActorsText);
        mainActorEditText.setText(listActors[0]);
        otherActorsEditText.setText(listOtherActors.toString());
        directorEditText.setText(film.getDirector());
        countryEditText.setText(film.getCountryMade());
        originalLanguageEditText.setText(film.getOriginalLanguage());

        Singleton.setFinish(false);
        Thread threadImage = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                try {
                    if (!Singleton.getFilm().getImagePoster().equals("N/A")) {
                        url = new URL(Singleton.getFilm().getImagePoster());
                        connection = (HttpURLConnection) url
                                .openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap bmp = BitmapFactory.decodeStream(input);
                        posterImageView.setImageBitmap(bmp);
                    } else {
                        posterImageView.setImageResource(R.drawable.film);
                        posterImageView.setBackground(ContextCompat.getDrawable(checkModifyDeleteActivity, R.drawable.not_rounded_rectangle_white_no_border));
                    }
                } catch (Exception e) {
                    Log.e(LOG, e.getMessage());
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    Singleton.setFinish(true);
                    notifyFinishInsertDataFilm();
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.startActivity(new Intent(CheckModifyDeleteActivity.this, MainActivity.class));
        this.finish();
    }
}