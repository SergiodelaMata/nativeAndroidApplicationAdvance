package com.example.nativeandroidapplicationadvance.db;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Film {
    private int idFilm;
    private String title;
    private String year;
    private String duration;
    private String genres;
    private String actors;
    private String director;
    private String countryMade;
    private String originalLanguage;
    private String imagePoster;
    private String languageSeen;
    private String citySeen;
    private String countrySeen;
    private String dateSeen;

    public Film() {
        this.idFilm = 0;
        this.title = "";
        this.year = "";
        this.duration = "";
        this.genres = "";
        this.actors = "";
        this.director = "";
        this.countryMade = "";
        this.originalLanguage = "";
        this.imagePoster = "";
        this.languageSeen = "";
        this.citySeen = "";
        this.countrySeen = "";
        this.dateSeen = "";
    }

    public Film(int idFilm, String title, String year, String duration, String genres, String actors, String director, String countryMade, String originalLanguage, String imagePoster, String languageSeen, String citySeen, String countrySeen, String dateSeen) {
        this.idFilm = idFilm;
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.genres = genres;
        this.actors = actors;
        this.director = director;
        this.countryMade = countryMade;
        this.originalLanguage = originalLanguage;
        this.imagePoster = imagePoster;
        this.languageSeen = languageSeen;
        this.citySeen = citySeen;
        this.countrySeen = countrySeen;
        this.dateSeen = dateSeen;
    }

    public int getIdFilm() {
        return idFilm;
    }

    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCountryMade() {
        return countryMade;
    }

    public void setCountryMade(String countryMade) {
        this.countryMade = countryMade;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getImagePoster() {
        return imagePoster;
    }

    public void setImagePoster(String imagePoster) {
        this.imagePoster = imagePoster;
    }

    public String getLanguageSeen() {
        return languageSeen;
    }

    public void setLanguageSeen(String languageSeen) {
        this.languageSeen = languageSeen;
    }

    public String getCitySeen() {
        return citySeen;
    }

    public void setCitySeen(String citySeen) {
        this.citySeen = citySeen;
    }

    public String getCountrySeen() {
        return countrySeen;
    }

    public void setCountrySeen(String countrySeen) {
        this.countrySeen = countrySeen;
    }

    public String getDateSeen() {
        return dateSeen;
    }

    public void setDateSeen(String dateSeen) {
        this.dateSeen = dateSeen;
    }
}
