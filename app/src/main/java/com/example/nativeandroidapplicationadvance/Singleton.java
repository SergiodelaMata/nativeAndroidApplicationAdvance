package com.example.nativeandroidapplicationadvance;

import android.widget.TableRow;

import com.example.nativeandroidapplicationadvance.db.Film;

import java.util.ArrayList;

public class Singleton {
    private static Singleton instance;
    private int[] headerTableTextFilms = {R.string.Poster, R.string.TitleFilm};
    private static ArrayList<Film> listFilms;
    private static ArrayList<TableRow> listTableRows;
    private static boolean finish = false;

    public static synchronized Singleton getInstance()
    {
        if(instance == null)
        {
            listFilms = new ArrayList<>();
            instance = new Singleton();
            listFilms = new ArrayList<>();
            listTableRows = new ArrayList<>();
            finish = false;
        }
        return instance;
    }

    public int[] getHeaderTableTextFilms() {
        return headerTableTextFilms;
    }

    public void setHeaderTableTextSubjects(int[] headerTableTextSubjects) {
        this.headerTableTextFilms = headerTableTextSubjects;
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
}
