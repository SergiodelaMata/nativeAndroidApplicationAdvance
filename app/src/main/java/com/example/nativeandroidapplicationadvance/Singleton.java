package com.example.nativeandroidapplicationadvance;

import android.widget.TableRow;

import com.example.nativeandroidapplicationadvance.db.Film;

import java.util.ArrayList;

public class Singleton {
    private static Singleton instance;
    private int[] headerTableTextFilms = {R.string.Poster, R.string.TitleFilm};
    private static Film film;
    private static ArrayList<Film> listFilms;
    private static ArrayList<TableRow> listTableRows;
    private static boolean finish = false;
    private static String titleFilm;
    private static int numberPage;
    private static int totalResults;

    public static synchronized Singleton getInstance()
    {
        // Establece los datos iniciales con los que contará el Singleton si no existía previamente su objeto
        if(instance == null)
        {
            listFilms = new ArrayList<>();
            instance = new Singleton();
            film = new Film();
            listFilms = new ArrayList<>();
            listTableRows = new ArrayList<>();
            finish = false;
            titleFilm = "";
            numberPage = 0;
            totalResults = 0;
        }
        return instance;
    }

    /**
     * Devuelve el array de textos de los encabezados de la tabla de películas
     * @return array de textos de los encabezados de la tabla de películas
     */
    public int[] getHeaderTableTextFilms() {
        return headerTableTextFilms;
    }

    /**
     * Introduce el array de textos de los encabezados de la tabla de películas
     * @param headerTableTextFilms array de textos de los encabezados de la tabla de películas
     */
    public void setHeaderTableTextFilms(int[] headerTableTextFilms) {
        this.headerTableTextFilms = headerTableTextFilms;
    }

    /**
     * Devuelve el objeto de la película guardado y/o que se está empleando para la actividad activa
     * @return objeto de la película guardado y/o que se está empleando para la actividad activa
     */
    public static Film getFilm() {
        return film;
    }

    /**
     * Introduce el objecto de la película que se está empleando para la actividad activa
     * @param film objeto de la película que se está empleando para la actividad activa
     */
    public static void setFilm(Film film) {
        Singleton.film = film;
    }

    /**
     * Devuelve el listado de películas guardado y/o que se está empleando para la actividad activa
     * @return listado de películas guardado y/o que se está empleando para la actividad activa
     */
    public static ArrayList<Film> getListFilms() {
        return listFilms;
    }

    /**
     * Introduce el listado de la película que se está empleando para la actividad activa
     * @param listFilms listado de películas que se está empleando para la actividad activa
     */
    public static void setListFilms(ArrayList<Film> listFilms) {
        Singleton.listFilms = listFilms;
    }

    /**
     * Devuelve la estructura de filas con las que va a contar la tabla de películas
     * @return estructura de filas con las que va a contar la tabla de películas
     */
    public static ArrayList<TableRow> getListTableRows() {
        return listTableRows;
    }

    /**
     * Introduce la estructura de filas con las que va a contar la tabla de películas
     * @param listTableRows estructura de filas con las que va a contar la tabla de películas
     */
    public static void setListTableRows(ArrayList<TableRow> listTableRows) {
        Singleton.listTableRows = listTableRows;
    }

    /**
     * Devuelve si ha finalizado o no la ejecución de un hilo que se está ejecutando
     * @return booleano que indica si ha finalizado o no la ejecución de un hilo que se está ejecutando
     */
    public static boolean isFinish() {
        return finish;
    }

    /**
     * Introduce si ha finalizado o no la ejecución de un hilo que se está ejecutando
     * @param finish booleano que indica si ha finalizado o no la ejecución de un hilo que se está ejecutando
     */
    public static void setFinish(boolean finish) {
        Singleton.finish = finish;
    }

    /**
     * Devuelve el título de la película guardado y/o que se está empleando para la actividad activa
     * @return título de la película guardado y/o que se está empleando para la actividad activa
     */
    public static String getTitleFilm() {
        return titleFilm;
    }

    /**
     * Introduce el título de la película guardado y/o que se está empleando para la actividad activa
     * @param titleFilm título de la película guardado y/o que se está empleando para la actividad activa
     */
    public static void setTitleFilm(String titleFilm) {
        Singleton.titleFilm = titleFilm;
    }

    /**
     * Devuelve el número de página en la que se encuentra la tabla de películas de la búsqueda de la película vista
     * @return número de página en la que se encuentra la tabla de películas de la búsqueda de la película vista
     */
    public static synchronized int getNumberPage() {
        return numberPage;
    }

    /**
     * Introduce el número de página en la que se encuentra la tabla de películas de la búsqueda de la película vista
     * @param numberPage número de página en la que se encuentra la tabla de películas de la búsqueda de la película vista
     */
    public static synchronized void setNumberPage(int numberPage) {
        Singleton.numberPage = numberPage;
    }

    /**
     * Devuelve el número de resultados totales guardado de la búsqueda de la película realizado en la actividad actual
     * @return número de resultados totales guardado de la búsqueda de la película realizado en la actividad actual
     */
    public static synchronized int getTotalResults() {
        return totalResults;
    }

    /**
     * Introduce el número de resultados totales guardado de la búsqueda de la película realizado en la actividad actual
     * @param totalResults número de resultados totales guardado de la búsqueda de la película realizado en la actividad actual
     */
    public static synchronized void setTotalResults(int totalResults) {
        Singleton.totalResults = totalResults;
    }
}
