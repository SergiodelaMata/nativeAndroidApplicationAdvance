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

    public int[] getHeaderTableTextFilms() {
        return headerTableTextFilms;
    }

    public void setHeaderTableTextSubjects(int[] headerTableTextSubjects) {
        this.headerTableTextFilms = headerTableTextSubjects;
    }

    public static Film getFilm() {
        return film;
    }

    public static void setFilm(Film film) {
        Singleton.film = film;
    }

    public static ArrayList<Film> getListFilms() {
        return listFilms;
    }

    public static void setListFilms(ArrayList<Film> listFilms) {
        Singleton.listFilms = listFilms;
    }

    public static ArrayList<TableRow> getListTableRows() {
        return listTableRows;
    }

    public static void setListTableRows(ArrayList<TableRow> listTableRows) {
        Singleton.listTableRows = listTableRows;
    }

    public static boolean isFinish() {
        return finish;
    }

    public static void setFinish(boolean finish) {
        Singleton.finish = finish;
    }

    public static String getTitleFilm() {
        return titleFilm;
    }

    public static void setTitleFilm(String titleFilm) {
        Singleton.titleFilm = titleFilm;
    }

    public static synchronized int getNumberPage() {
        return numberPage;
    }

    public static synchronized void setNumberPage(int numberPage) {
        Singleton.numberPage = numberPage;
    }

    public static synchronized int getTotalResults() {
        return totalResults;
    }

    public static synchronized void setTotalResults(int totalResults) {
        Singleton.totalResults = totalResults;
    }
}
