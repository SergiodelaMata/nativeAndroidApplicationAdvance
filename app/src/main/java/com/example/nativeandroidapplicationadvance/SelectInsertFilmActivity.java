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
import android.widget.TextView;

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
    private EditText inputTitleFilm;
    private Button buttonSearchFilm;
    private TextView totalResultsTextView;
    private TextView numberPageTextView;
    private Button buttonPreviousPage;
    private Button buttonNextPage;

    /**
     * Método que se ejecuta al crear la actividad
     * @param savedInstanceState Bundle de los datos guardados de la instancia anterior
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_insert_film);
        this.inputTitleFilm = findViewById(R.id.titleFilm);
        this.buttonSearchFilm = findViewById(R.id.insertNewSubjectButton);
        this.numberPageTextView = findViewById(R.id.numberPage);
        this.totalResultsTextView = findViewById(R.id.totalResultsTextView);

        this.buttonPreviousPage = findViewById(R.id.previousPageButton);
        this.buttonPreviousPage.setEnabled(false);

        this.buttonNextPage = findViewById(R.id.nextPageButton);
        this.buttonNextPage.setEnabled(false);

        // Establece el valor inicial con el que se partirá la página de la tabla
        Singleton.setNumberPage(0);
        numberPageTextView.setText(String.valueOf(Singleton.getNumberPage()));

        //Se establece inicialmente la estructura de la tabla solo con las cabeceras
        tableLayout = findViewById(R.id.tableFilms);
        rows = new ArrayList<>();
        SelectInsertFilmDynamicTable dynamicTable = new SelectInsertFilmDynamicTable(this, tableLayout, this.getApplicationContext());
        dynamicTable.addHeader(headerTable);

        /*
            Se establece el listener del botón de búsqueda de manera que busque en la API películas
            con el título introducido y después se muestren en la tabla
         */
        buttonSearchFilm.setOnClickListener(v -> {
            String url = "www.omdbapi.com";
            String apiKey = "c4b05ab";
            String title = inputTitleFilm.getText().toString();
            Singleton.setTitleFilm(title);
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
            String finalTitleText = titleText.toString();
            /*
                Se inicializa el valor del booleano del Singleton para hacer que el hilo principal lo tenga
                en cuenta una vez que se inicie el nuevo hilo y le toque esperar a termine
             */
            Singleton.setFinish(false);
             /*
                Se crea un nuevo hilo para poder obtener de la API el listado de películas a partir
                del título introducido para la búsqueda
             */
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
                        /*
                            Se comprueba si la conexión se ha realizado correctamente.
                            En caso de no tener una conexión autorizada lo indica en el log.
                         */
                        int statusCode = urlConnection.getResponseCode();
                        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            Log.d(LOG, "HTTP_UNAUTHORIZED");
                            Singleton.setNumberPage(0);
                            Singleton.setTotalResults(0);
                            Singleton.setListFilms(new ArrayList<>());
                            Singleton.setListTableRows(new ArrayList<>());
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
                                JSONArray jsonArray = jsonObject.getJSONArray("Search");
                                ArrayList<Film> films = new ArrayList<>();
                                // Se obtiene el listado de películas
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    Film film = new Film();
                                    JSONObject jsonObjectFilm = jsonArray.getJSONObject(i);
                                    film.setTitle(jsonObjectFilm.getString("Title"));
                                    film.setImagePoster(jsonObjectFilm.getString("Poster"));
                                    film.setYear(jsonObjectFilm.getString("Year"));
                                    films.add(film);
                                }
                                /*
                                    Se guarda el listado completo de películas obtenidas en el singleton
                                    al igual que el número de página y el total de resultados de manera
                                    que se puedan realizar más búsquedas en la API para la misma
                                    referencia del título según la separación de páginas que tiene.
                                 */
                                Singleton.setListFilms(films);
                                Singleton.setNumberPage(1);
                                Singleton.setTotalResults(Integer.parseInt(jsonObject.getString("totalResults")));
                            }
                            /*
                                En caso de no encontrarse resultados, se indica en el log y ajusta
                                los valores del número de página, el total de resultados y el listado
                                de películas obtenidas.
                             */
                            else
                            {
                                Log.i(LOG, "No se encontraron películas");
                                Singleton.setNumberPage(0);
                                Singleton.setTotalResults(0);
                                Singleton.setListFilms(new ArrayList<>());
                                Singleton.setListTableRows(new ArrayList<>());
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(LOG, "Error: " + e.getMessage());
                        Singleton.setNumberPage(0);
                        Singleton.setTotalResults(0);
                        Singleton.setListFilms(new ArrayList<>());
                        Singleton.setListTableRows(new ArrayList<>());
                    }
                    finally {
                        // Se cierra la conexión en caso de haberla abierta
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        Singleton.setFinish(true);
                        dynamicTable.notifyFinishBuildStructureTable();
                    }

                }
            });
            // Se inicia el hilo creado para la obtención del listado de películas de la API
            thread.start();
            /*
                Se realiza la limpieza de la tabla anterior, se espera a que termine el hilo creado
                y se crea la nueva tabla para la nueva búsqueda.
             */
            dynamicTable.resetTable();
            dynamicTable.addHeader(headerTable);
            // Se espera a que termine el hilo creado para la obtención del listado de películas de la API
            dynamicTable.waitFinishThread();
            numberPageTextView.setText(String.valueOf(Singleton.getNumberPage()));
            totalResultsTextView.setText(String.valueOf(Singleton.getTotalResults()));
            dynamicTable.addData(getData());

            /*
                Se comprueba el número de resultados obtenidos para habilitar o no los botones para ir
                a la página anterior y la página siguiente.
                En caso de ser superior el número de páginas a la inicial, se habilita el botón para ir
                a la siguiente y se deshabilita el botón para ir a la anterior.
             */
            if(Singleton.getTotalResults() / 10 > 1)
            {
                buttonNextPage.setEnabled(true);
                buttonPreviousPage.setEnabled(false);
            }
            /*
                En caso de quedar algún resultado por mostrar, pero no llegar a rellenar una entera,
                se habilita el botón para ir a la siguiente y se deshabilita el botón para ir a la anterior.
             */
            else if(Singleton.getTotalResults() / 10 == 1 && Singleton.getTotalResults() % 10 != 0)
            {
                buttonNextPage.setEnabled(true);
                buttonPreviousPage.setEnabled(false);
            }
            /*
                En caso de que no haya resultados, se deshabilita el botón para ir a la siguiente y
                el botón para ir a la anterior.
             */
            else
            {
                buttonNextPage.setEnabled(false);
                buttonPreviousPage.setEnabled(false);
            }

        });

        /*
            Se crea el evento para el botón de ir a la página anterior de manera que se puedan obtener
            datos de las películas de la página anterior dentro de la API.
         */
        buttonPreviousPage.setOnClickListener(v -> {
            String url = "www.omdbapi.com";
            String apiKey = "c4b05ab";
            String title = Singleton.getTitleFilm();
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
            String finalTitleText = titleText.toString();
            /*
                Se inicializa el valor del booleano del Singleton para hacer que el hilo principal lo tenga
                en cuenta una vez que se inicie el nuevo hilo y le toque esperar a termine
             */
            Singleton.setFinish(false);
             /*
                Se crea un nuevo hilo para poder obtener de la API el listado de películas a partir
                del título introducido para la búsqueda para la página anterior.
             */
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection urlConnection = null;
                    String content = "";
                    int numberPage = Singleton.getNumberPage() - 1;
                    try {
                        //Realiza una búsqueda del listado de películas de la página anterior que encajan a partir de un título
                        URL urlSearch = new URL("https://" + url + "/?apikey=" + apiKey + "&s=" + finalTitleText + "&type=movie&page=" + numberPage);
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
                            Singleton.setNumberPage(0);
                            Singleton.setTotalResults(0);
                            Singleton.setListFilms(new ArrayList<>());
                            Singleton.setListTableRows(new ArrayList<>());
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
                                JSONArray jsonArray = jsonObject.getJSONArray("Search");
                                ArrayList<Film> films = new ArrayList<>();
                                // Se obtiene el listado de películas
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    Film film = new Film();
                                    JSONObject jsonObjectFilm = jsonArray.getJSONObject(i);
                                    film.setTitle(jsonObjectFilm.getString("Title"));
                                    film.setImagePoster(jsonObjectFilm.getString("Poster"));
                                    film.setYear(jsonObjectFilm.getString("Year"));
                                    films.add(film);
                                }
                                /*
                                    Se guarda el listado completo de películas obtenidas en el singleton
                                    al igual que el número de página y el total de resultados de manera
                                    que se puedan realizar más búsquedas en la API para la misma
                                    referencia del título según la separación de páginas que tiene.
                                 */
                                Singleton.setListFilms(films);
                                Singleton.setNumberPage(numberPage);
                                Singleton.setTotalResults(Integer.parseInt(jsonObject.getString("totalResults")));
                            }
                            /*
                                En caso de no encontrarse resultados, se indica en el log y ajusta
                                los valores del número de página, el total de resultados y el listado
                                de películas obtenidas.
                             */
                            else
                            {
                                Log.i(LOG, "No se encontraron películas");
                                Singleton.setNumberPage(0);
                                Singleton.setTotalResults(0);
                                Singleton.setListFilms(new ArrayList<>());
                                Singleton.setListTableRows(new ArrayList<>());
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(LOG, "Error: " + e.getMessage());
                        Singleton.setNumberPage(0);
                        Singleton.setTotalResults(0);
                        Singleton.setListFilms(new ArrayList<>());
                        Singleton.setListTableRows(new ArrayList<>());
                    }
                    finally {
                        // Se cierra la conexión en caso de haberla abierta
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        Singleton.setFinish(true);
                        dynamicTable.notifyFinishBuildStructureTable();
                    }

                }
            });
            // Se inicia el hilo creado para la obtención del listado de películas de la API
            thread.start();
            /*
                Se realiza la limpieza de la tabla anterior, se espera a que termine el hilo creado
                y se crea la nueva tabla para la nueva búsqueda.
             */
            dynamicTable.resetTable();
            dynamicTable.addHeader(headerTable);
            // Se espera a que termine el hilo creado para la obtención del listado de películas de la API
            dynamicTable.waitFinishThread();
            numberPageTextView.setText(String.valueOf(Singleton.getNumberPage()));
            totalResultsTextView.setText(String.valueOf(Singleton.getTotalResults()));
            dynamicTable.addData(getData());

            /*
                Si se dispone de más de una página de resultados y la página actual no es la última,
                el botón de siguiente página estará habilitado
             */
            if(Singleton.getTotalResults() / 10 > 1 && Singleton.getNumberPage() < Singleton.getTotalResults() / 10)
            {
                buttonNextPage.setEnabled(true);
            }
            /*
                En caso de encontrarse en la última página de resultados, pero aún quedan resultados por ver,
                el botón de siguiente página estará habilitado para acceder a una última página para acceder estos
             */
            else if(Singleton.getTotalResults() / 10 == Singleton.getNumberPage() && Singleton.getTotalResults() % 10 != 0 && Singleton.getNumberPage() < (Singleton.getTotalResults() / 10 + 1))
            {
                buttonNextPage.setEnabled(true);
            }
            /*
                En caso contrario, el botón de siguiente página estará deshabilitado
             */
            else
            {
                buttonNextPage.setEnabled(false);
            }

            /*
                Si la página actual no es la primera, el botón de página anterior estará habilitado
             */
            if(Singleton.getNumberPage() > 1)
            {
                buttonPreviousPage.setEnabled(true);
            }
            /*
                En caso contrario, el botón de página anterior estará deshabilitado
             */
            else
            {
                buttonPreviousPage.setEnabled(false);
            }
        });

        /*
            Se crea el evento para el botón de ir a la página anterior de manera que se puedan obtener
            datos de las películas de la página posterior dentro de la API.
         */
        buttonNextPage.setOnClickListener(v -> {
            String url = "www.omdbapi.com";
            String apiKey = "c4b05ab";
            String title = Singleton.getTitleFilm();
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
            String finalTitleText = titleText.toString();
            /*
                Se inicializa el valor del booleano del Singleton para hacer que el hilo principal lo tenga
                en cuenta una vez que se inicie el nuevo hilo y le toque esperar a termine
             */
            Singleton.setFinish(false);
             /*
                Se crea un nuevo hilo para poder obtener de la API el listado de películas a partir
                del título introducido para la búsqueda
             */
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection urlConnection = null;
                    String content = "";
                    int numberPage = Singleton.getNumberPage() + 1;
                    try {
                        //Realiza una búsqueda del listado de películas de la página posterior que encajan a partir de un título
                        URL urlSearch = new URL("https://" + url + "/?apikey=" + apiKey + "&s=" + finalTitleText + "&type=movie&page=" + numberPage);
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
                            Singleton.setNumberPage(0);
                            Singleton.setTotalResults(0);
                            Singleton.setListFilms(new ArrayList<>());
                            Singleton.setListTableRows(new ArrayList<>());
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
                                JSONArray jsonArray = jsonObject.getJSONArray("Search");
                                ArrayList<Film> films = new ArrayList<>();
                                // Se obtiene el listado de películas
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    Film film = new Film();
                                    JSONObject jsonObjectFilm = jsonArray.getJSONObject(i);
                                    film.setTitle(jsonObjectFilm.getString("Title"));
                                    film.setImagePoster(jsonObjectFilm.getString("Poster"));
                                    film.setYear(jsonObjectFilm.getString("Year"));
                                    films.add(film);
                                }
                                /*
                                    Se guarda el listado completo de películas obtenidas en el singleton
                                    al igual que el número de página y el total de resultados de manera
                                    que se puedan realizar más búsquedas en la API para la misma
                                    referencia del título según la separación de páginas que tiene.
                                 */
                                Singleton.setListFilms(films);
                                Singleton.setNumberPage(numberPage);
                                Singleton.setTotalResults(Integer.parseInt(jsonObject.getString("totalResults")));
                            }
                            /*
                                En caso de no encontrarse resultados, se indica en el log y ajusta
                                los valores del número de página, el total de resultados y el listado
                                de películas obtenidas.
                             */
                            else
                            {
                                Log.i(LOG, "No se encontraron películas");
                                Singleton.setNumberPage(0);
                                Singleton.setTotalResults(0);
                                Singleton.setListFilms(new ArrayList<>());
                                Singleton.setListTableRows(new ArrayList<>());
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(LOG, "Error: " + e.getMessage());
                        Singleton.setNumberPage(0);
                        Singleton.setTotalResults(0);
                        Singleton.setListFilms(new ArrayList<>());
                        Singleton.setListTableRows(new ArrayList<>());
                    }
                    finally {
                        // Se cierra la conexión en caso de haberla abierta
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        Singleton.setFinish(true);
                        dynamicTable.notifyFinishBuildStructureTable();
                    }
                }
            });
            // Se inicia el hilo creado para la obtención del listado de películas de la API
            thread.start();
            /*
                Se realiza la limpieza de la tabla anterior, se espera a que termine el hilo creado
                y se crea la nueva tabla para la nueva búsqueda.
             */
            dynamicTable.resetTable();
            dynamicTable.addHeader(headerTable);
            // Se espera a que termine el hilo creado para la obtención del listado de películas de la API
            dynamicTable.waitFinishThread();
            numberPageTextView.setText(String.valueOf(Singleton.getNumberPage()));
            totalResultsTextView.setText(String.valueOf(Singleton.getTotalResults()));
            dynamicTable.addData(getData());

            /*
                Si se dispone de más de una página de resultados y la página actual no es la última,
                el botón de siguiente página estará habilitado
             */
            if(Singleton.getTotalResults() / 10 > 1 && Singleton.getNumberPage() < Singleton.getTotalResults() / 10)
            {
                buttonNextPage.setEnabled(true);
            }
            /*
                En caso de encontrarse en la última página de resultados, pero aún quedan resultados por ver,
                el botón de siguiente página estará habilitado para acceder a una última página para acceder estos
             */
            else if(Singleton.getTotalResults() / 10 == Singleton.getNumberPage() && Singleton.getTotalResults() % 10 != 0 && Singleton.getNumberPage() < (Singleton.getTotalResults() / 10 + 1))
            {
                buttonNextPage.setEnabled(true);
            }
            /*
                En caso contrario, el botón de siguiente página estará deshabilitado
             */
            else
            {
                buttonNextPage.setEnabled(false);
            }

            /*
                Si la página actual no es la primera, el botón de página anterior estará habilitado
             */
            if(Singleton.getNumberPage() > 1)
            {
                buttonPreviousPage.setEnabled(true);
            }
            /*
                En caso contrario, el botón de página anterior estará deshabilitado
             */
            else
            {
                buttonPreviousPage.setEnabled(false);
            }
        });
    }

    /**
     * Devuelve los datos de la base de datos de las películas
     * @return ArrayList de las películas con los datos de la base de datos
     */
    public ArrayList<Film> getData()
    {
        rows = Singleton.getListFilms();
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