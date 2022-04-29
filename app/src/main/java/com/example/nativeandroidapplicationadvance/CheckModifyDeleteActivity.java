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

        /*
            Establece el comportamiento de comprobación y modificación de los datos de la película una
            vez presiona el botón para actualizar de datos
         */
        buttonModifyDataFilm.setOnClickListener(view -> {
            String languageSeenFilm = languageSeenFilmEditText.getText().toString();
            String dateSeenFilm = dateSeenFilmEditText.getText().toString();
            String citySeenFilm = citySeenFilmEditText.getText().toString();
            String countrySeenFilm = countrySeenFilmEditText.getText().toString();
            /*
                Comprobación de que los campos obligatorios no estén vacíos. En caso de estar vacíos,
                se muestra un mensaje de error.
             */
            if (languageSeenFilm.isEmpty() || dateSeenFilm.isEmpty() || citySeenFilm.isEmpty() || countrySeenFilm.isEmpty())
            {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.AlertNoRequiredData)
                        .setMessage(R.string.MessageNoRequiredData)
                        .show();
            }
            // En caso de que los campos obligatorios no estén vacíos, se procede a la modificación de los datos
            else
            {
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

        /*
            Establece el funcionamiento del botón de borrar datos de la película y establece la
            redirección a la actividad principal una vez se haya borrado la película.
         */
        buttonDeleteDataFilm.setOnClickListener(view -> {
            Intent intentSaved = getIntent();
            Bundle bundle = intentSaved.getExtras();
            int idFilm = bundle.getInt("idFilm");
            dbManager.deleteFilm(idFilm);
            Intent intent = new Intent(CheckModifyDeleteActivity.this, MainActivity.class);
            this.startActivity(intent);
            finish();
        });

        /*
            Establece el comportamiento del campo para insertar la fecha en la que se vió la película
            para se pueda introducir una fecha válida a través de un calendario
         */
        dateSeenFilmEditText.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mMonth = calendar.get(Calendar.MONTH);
            int mYear = calendar.get(Calendar.YEAR);

            //Establece el formato en el que se verá la fecha una vez seleccionada la fecha
            datePickerDialog = new DatePickerDialog(checkModifyDeleteActivity, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    dateSeenFilmEditText.setText(date);
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        /*
            Obtiene los datos guardados de la actividad anterior y se recoge el id de la película
            para buscar información de la base de datos y posteriormente rellenar todos los campos
            a partir de la información obtenida
         */
        Intent intentSaved = getIntent();
        Bundle bundle = intentSaved.getExtras();
        int idFilm = bundle.getInt("idFilm");
        Film film = dbManager.getFilm(idFilm);
        //Se guardan los datos de la película en el Singleton desde cualquier parte de la actividad
        Singleton.setFilm(film);
        //Se rellenan los campos con los datos de la película
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
        /*
            A partir del listado completo de actores, se separa el primer actor del resto para que
            el primero aparezca en uno de los campos como actor principal y el resto en otro campo
            como actores secundarios u otros miembros del reparto
         */
        StringBuilder listOtherActors = new StringBuilder();
        for (int i = 1; i < listActors.length; i++)
        {
            // Si es el primero, se guarda solo el campo del nombre del actor
            if(i == 1)
            {
                listOtherActors = new StringBuilder(listActors[i]);
            }
            // Si no es el primero, se guarda el nombre del actor y una coma para separarlo del resto
            else
            {
                listOtherActors.append(",").append(listActors[i]);
            }
        }
        listActorEditText.setText(listActorsText);
        mainActorEditText.setText(listActors[0]);
        otherActorsEditText.setText(listOtherActors.toString());
        directorEditText.setText(film.getDirector());
        countryEditText.setText(film.getCountryMade());
        originalLanguageEditText.setText(film.getOriginalLanguage());

        /*
            Se inicializa el valor del booleano del Singleton para hacer que el hilo principal lo tenga
            en cuenta una vez que se inicie el nuevo hilo y le toque esperar a termine
         */
        Singleton.setFinish(false);
        /*
            Se crea un nuevo hilo para poder generar la imagen de la película de forma asíncrona a partir de
            su URL
         */
        Thread threadImage = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                try {
                    /*
                        Se comprueba si el campo relacionado a la imagen de la película cuenta con
                        una URL o no. En caso de haber una URL, se establece la conexión y se
                        establece la imagen a través de la URL
                     */
                    if (!Singleton.getFilm().getImagePoster().equals("N/A")) {
                        url = new URL(Singleton.getFilm().getImagePoster());
                        connection = (HttpURLConnection) url
                                .openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap bmp = BitmapFactory.decodeStream(input);
                        posterImageView.setImageBitmap(bmp);
                    }
                    /*
                        En caso de no haber una URL, se establece la imagen por defecto
                     */
                    else
                    {
                        posterImageView.setImageResource(R.drawable.film);
                        posterImageView.setBackground(ContextCompat.getDrawable(checkModifyDeleteActivity, R.drawable.not_rounded_rectangle_white_no_border));
                    }
                }
                catch (Exception e)
                {
                    Log.e(LOG, e.getMessage());
                }
                finally
                {
                    // Se cierra la conexión en caso de haberla abierta
                    if (connection != null) {
                        connection.disconnect();
                    }
                    Singleton.setFinish(true);
                    notifyFinishInsertDataFilm();
                }
            }
        });
        // Se inicia el hilo creado para obtener la imagen de la película
        threadImage.start();
        // El hilo principal se queda esperando a que el hilo creado termine
        waitFinishThread();
    }

    /**
     * Notifica al hilo principal que ya se han obtenido los datos de la película e introducido en
     * los distintos campos de la actividad
     */
    private synchronized void notifyFinishInsertDataFilm() {
        notifyAll();
    }

    /**
     * Establece la espera del hilo principal hasta que el hilo creado termine de obtener los datos
     * de la película y los introduzca en los distintos campos de la actividad
     */
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