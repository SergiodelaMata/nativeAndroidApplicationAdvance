package com.example.nativeandroidapplicationadvance;

import com.example.nativeandroidapplicationadvance.db.Film;

import java.util.ArrayList;

public class Singleton {
    private static Singleton instance;
    private int[] headerTableTextFilms = {R.string.Poster, R.string.TitleFilm};
    private static ArrayList<Film> listFilms;

    public static synchronized Singleton getInstance()
    {
        if(instance == null)
        {
            listFilms = new ArrayList<>();
            instance = new Singleton();
            Film film1 = new Film();
            listFilms.add(film1);
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
}
