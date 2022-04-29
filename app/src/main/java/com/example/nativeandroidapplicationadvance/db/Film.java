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

    /**
     * Contructor de la clase la película
     */
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

    /**
     * Constructor de la clase la película
     * @param idFilm id de la película
     * @param title título de la película
     * @param year año de la película
     * @param duration duración de la película
     * @param genres géneros de la película
     * @param actors actores de la película
     * @param director director de la película
     * @param countryMade país de producción de la película
     * @param originalLanguage idiomas en los que originalmente se encuentra la película
     * @param imagePoster imagen de la película
     * @param languageSeen idioma en el que se ha visto la película
     * @param citySeen ciudad en la que se ha visto la película
     * @param countrySeen país en el que se ha visto la película
     * @param dateSeen fecha en la que se ha visto la película
     */
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

    /**
     * Devuelve el id de la película
     * @return id de la película
     */
    public int getIdFilm() {
        return idFilm;
    }

    /**
     * Establece el id de la película
     * @param idFilm id de la película
     */
    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }

    /**
     * Devuelve el título de la película
     * @return título de la película
     */
    public String getTitle() {
        return title;
    }

    /**
     * Establece el título de la película
     * @param title título de la película
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Devuelve el año de la película
     * @return año de la película
     */
    public String getYear() {
        return year;
    }

    /**
     * Establece el año de la película
     * @param year año de la película
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Devuelve la duración de la película
     * @return duración de la película
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Establece la duración de la película
     * @param duration duración de la película
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Devuelve los géneros de la película
     * @return géneros de la película
     */
    public String getGenres() {
        return genres;
    }

    /**
     * Establece los géneros de la película
     * @param genres géneros de la película
     */
    public void setGenres(String genres) {
        this.genres = genres;
    }

    /**
     * Devuelve los actores de la película
     * @return actores de la película
     */
    public String getActors() {
        return actors;
    }

    /**
     * Establece los actores de la película
     * @param actors actores de la película
     */
    public void setActors(String actors) {
        this.actors = actors;
    }

    /**
     * Devuelve el director de la película
     * @return director de la película
     */
    public String getDirector() {
        return director;
    }

    /**
     * Establece el director de la película
     * @param director director de la película
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * Devuelve el país de origen de la película
     * @return país de origen de la película
     */
    public String getCountryMade() {
        return countryMade;
    }

    /**
     * Establece el país de origen de la película
     * @param countryMade país de origen de la película
     */
    public void setCountryMade(String countryMade) {
        this.countryMade = countryMade;
    }

    /**
     * Devuelve los idiomas en los que originalmente se encuentra la película
     * @return idiomas en los que originalmente se encuentra la película
     */
    public String getOriginalLanguage() {
        return originalLanguage;
    }

    /**
     * Establece los idiomas en los que originalmente se encuentra la película
     * @param originalLanguage idiomas en los que originalmente se encuentra la película
     */
    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    /**
     * Devuelve la imagen/poster de la película
     * @return imagen/poster de la película
     */
    public String getImagePoster() {
        return imagePoster;
    }

    /**
     * Establece la imagen/poster de la película
     * @param imagePoster imagen/poster de la película
     */
    public void setImagePoster(String imagePoster) {
        this.imagePoster = imagePoster;
    }

    /**
     * Devuelve el idioma en el que se vio la película
     * @return idioma en el que se vio la película
     */
    public String getLanguageSeen() {
        return languageSeen;
    }

    /**
     * Establece el idioma en el que se vio la película
     * @param languageSeen idioma en el que se vio la película
     */
    public void setLanguageSeen(String languageSeen) {
        this.languageSeen = languageSeen;
    }

    /**
     * Devuelve la ciudad en la que se vio la película
     * @return ciudad en la que se vio la película
     */
    public String getCitySeen() {
        return citySeen;
    }

    /**
     * Establece la ciudad en la que se vio la película
     * @param citySeen ciudad en la que se vio la película
     */
    public void setCitySeen(String citySeen) {
        this.citySeen = citySeen;
    }

    /**
     * Devuelve el país en el que se vio la película
     * @return país en el que se vio la película
     */
    public String getCountrySeen() {
        return countrySeen;
    }

    /**
     * Establece el país en el que se vio la película
     * @param countrySeen país en el que se vio la película
     */
    public void setCountrySeen(String countrySeen) {
        this.countrySeen = countrySeen;
    }

    /**
     * Devuelve la fecha en la que se vio la película
     * @return fecha en la que se vio la película
     */
    public String getDateSeen() {
        return dateSeen;
    }

    /**
     * Establece la fecha en la que se vio la película
     * @param dateSeen fecha en la que se vio la película
     */
    public void setDateSeen(String dateSeen) {
        this.dateSeen = dateSeen;
    }
}
