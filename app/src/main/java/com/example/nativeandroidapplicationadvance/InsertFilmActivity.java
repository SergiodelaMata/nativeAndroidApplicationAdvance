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

public class InsertFilmActivity extends AppCompatActivity {
    //Etiqueta logcat
    private static final String LOG = "InsFilmActivity";
    private InsertFilmActivity insertFilmActivity;
    private DatePickerDialog datePickerDialog;

    /**
     * Método que se ejecuta al crear la actividad
     * @param savedInstanceState Bundle de los datos guardados de la instancia anterior
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        insertFilmActivity = this;
        setContentView(R.layout.activity_insert_film);

        DBManager dbManager = new DBManager(this);

        TextView titleTextView = findViewById(R.id.titleFilm);
        ImageView posterImageView = findViewById(R.id.imageFilm);

        EditText languageSeenFilmEditText = findViewById(R.id.languageSeenFilm);
        EditText dateSeenFilmEditText = findViewById(R.id.dateSeenFilm);
        EditText citySeenFilmEditText = findViewById(R.id.citySeenFilm);
        EditText countrySeenFilmEditText = findViewById(R.id.countrySeenFilm);

        Button buttonAddFilm = findViewById(R.id.addFilmButton);

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
            Establece el comportamiento de comprobación e inserción de los datos de la película una
            vez presiona el botón de inserción de datos
         */
        buttonAddFilm.setOnClickListener(view -> {
            String languageSeenFilm = languageSeenFilmEditText.getText().toString();
            String dateSeenFilm = dateSeenFilmEditText.getText().toString();
            String citySeenFilm = citySeenFilmEditText.getText().toString();
            String countrySeenFilm = countrySeenFilmEditText.getText().toString();
            /*
                Comprobación de que los campos obligatorios no estén vacíos. En caso de estar vacíos,
                se muestra un mensaje de error.
             */
            if (languageSeenFilm.isEmpty() || dateSeenFilm.isEmpty() || citySeenFilm.isEmpty() || countrySeenFilm.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.AlertNoRequiredData)
                        .setMessage(R.string.MessageNoRequiredData)
                        .show();
            }
            // En caso de que los campos obligatorios no estén vacíos, se procede a la inserción de los datos
            else {
                Film film = Singleton.getFilm();
                film.setLanguageSeen(languageSeenFilm);
                film.setDateSeen(dateSeenFilm);
                film.setCitySeen(citySeenFilm);
                film.setCountrySeen(countrySeenFilm);
                boolean verify = dbManager.newFilm(film, null);
                // Si la inserción se ha realizado correctamente, se vuelve a la página principal
                if(verify)
                {
                    Intent intent = new Intent(InsertFilmActivity.this, MainActivity.class);
                    this.startActivity(intent);
                    finish();
                }
                // Si no se ha podido realizar la inserción, se muestra una alerta indicándolo
                else
                {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.AlertErrorSendInsert)
                            .setMessage(R.string.MessageErrorSendInsert)
                            .show();
                }
            }
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
            datePickerDialog = new DatePickerDialog(insertFilmActivity, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    dateSeenFilmEditText.setText(date);
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        /*
            Obtiene los datos guardados de la actividad anterior y se recoge el título de la película
            para buscar información de la misma en la API y posteriormente rellenar todos los campos
            a partir de la información obtenida (salvo los que se deben rellenar)
         */
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
        // Se formatea el título de la película para que se pueda buscar en la API
        for (int i = 0; i < titleArray.length; i++)
        {
            // En caso de que solo sea la primera palabra de la película, solo se añade ésta
            if (i == 0)
            {
                titleText.append(titleArray[i]);
            }
            /*
                En caso de que no sea la primera palabra de la película, se añade un '+' delante de
                la palabra y se añade la palabra
             */
            else
            {
                titleText.append("+").append(titleArray[i]);
            }
        }
        String content = "";
        HttpURLConnection urlConnection = null;
        String finalTitleText = titleText.toString();
        /*
            Se inicializa el valor del booleano del Singleton para hacer que el hilo principal lo tenga
            en cuenta una vez que se inicie el nuevo hilo y le toque esperar a termine
         */
        Singleton.setFinish(false);
         /*
            Se crea un nuevo hilo para poder obtener de la API toda la información de la película a partir
            del título de la misma
         */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                String content = "";
                try {
                    //Realiza una búsqueda de la película a partir de un título en la API
                    URL urlSearch = new URL("https://" + url + "/?apikey=" + apiKey + "&t=" + finalTitleText);
                    urlConnection = (HttpURLConnection) urlSearch.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(15000);

                    /*
                        Se comprueba si la conexión se ha realizado correctamente.
                        En caso de no tener una conexión autorizada lo indica en el log.
                     */
                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        Log.d(LOG, "HTTP_UNAUTHORIZED");
                    }
                    /*
                        En caso de poder realizar la conexión, se obtiene el contenido de la respuesta
                     */
                    else if (statusCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = urlConnection.getInputStream();
                        content = new Scanner(inputStream).useDelimiter("\\A").next();
                        JSONObject jsonObject = new JSONObject(content);
                        /*
                            Se comprueba si se han obtenido resultados de la búsqueda.
                            En caso de encontrarse, se obtiene la información de la película.
                         */
                        if(jsonObject.getString("Response").equals("True"))
                        {
                            Log.i(LOG, "Response: " + jsonObject.getString("Response"));
                            Film film = new Film();
                            film.setTitle(jsonObject.getString("Title"));
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
                        // En caso de no encontrarse resultados, se indica en el log.
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
                finally
                {
                    // Se cierra la conexión en caso de haberla abierta
                    if (urlConnection != null)
                    {
                        urlConnection.disconnect();
                    }
                    Singleton.setFinish(true);
                    notifyFinishInsertDataFilm();
                }
            }
        });
        // Se inicia el hilo creado para la obtención de la información de la película
        thread.start();
        // El hilo principal se queda esperando a que el hilo creado termine
        waitFinishThread();
        //Se rellenan los campos con los datos de la película
        yearEditText.setText(Singleton.getFilm().getYear());
        durationEditText.setText(Singleton.getFilm().getDuration());
        genreEditText.setText(Singleton.getFilm().getGenres());
        String listActorsText = Singleton.getFilm().getActors();
        String[] listActors = listActorsText.split(",");
        StringBuilder listOtherActors = new StringBuilder();
        /*
            A partir del listado completo de actores, se separa el primer actor del resto para que
            el primero aparezca en uno de los campos como actor principal y el resto en otro campo
            como actores secundarios u otros miembros del reparto
         */
        for(int i = 1; i < listActors.length; i++)
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
        directorEditText.setText(Singleton.getFilm().getDirector());
        countryEditText.setText(Singleton.getFilm().getCountryMade());
        originalLanguageEditText.setText(Singleton.getFilm().getOriginalLanguage());
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
                try
                {
                    /*
                        Se comprueba si el campo relacionado a la imagen de la película cuenta con
                        una URL o no. En caso de haber una URL, se establece la conexión y se
                        establece la imagen a través de la URL
                     */
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
                    /*
                        En caso de no haber una URL, se establece la imagen por defecto
                     */
                    else
                    {
                        posterImageView.setImageResource(R.drawable.film);
                        posterImageView.setBackground(ContextCompat.getDrawable(insertFilmActivity, R.drawable.not_rounded_rectangle_white_no_border));
                    }
                } catch (Exception e) {
                    Log.e(LOG, e.getMessage());
                }
                finally {
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
}