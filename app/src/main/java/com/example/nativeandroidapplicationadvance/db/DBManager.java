package com.example.nativeandroidapplicationadvance.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper
{
    //Etiqueta logcat
    private static final String LOG = "DBManager";

    //Información de la base de datos
    private static final String nameDB = "films.sqlite";
    private static final int versionDB = 1;

    //Tabla de las películas
    private static final String tableFilm = "Film";
    private static final String createTableFilmQuery = "CREATE TABLE " + tableFilm +
            "(idFilm INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT, " +
            "year TEXT, " +
            "duration TEXT, " +
            "genres TEXT, " +
            "actors TEXT, " +
            "director TEXT, " +
            "countryMade TEXT, " +
            "originalLanguage TEXT, " +
            "imagePoster TEXT, " +
            "languageSeen TEXT, " +
            "citySeen TEXT, " +
            "countrySeen TEXT, " +
            "dateSeen DATE);";

    //Estructura de la consulta para eliminar una tabla
    private static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

    //Estructura para realizar una consulta de una tabla completa
    private static final String SELECT_FROM = "SELECT * FROM ";

    //Estructura para realizar una inserción a una tabla
    private static final String INSERT_INTO = "INSERT INTO ";

    public DBManager(Context context) {
        super(context, nameDB, null, versionDB);
    }


    /**
     * Realiza la creación de las tablas de la base de datos en el móvil
     * @param db Base de datos de SQLite
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            Log.i(LOG, "Creando la base de datos de las películas");
            //Se crea la tabla de las películas
            db.execSQL(createTableFilmQuery);
            //insertInitData(db);
        }
        catch(final SQLException e)
        {
            e.printStackTrace();
        }
        catch(RuntimeException e)
        {
            Log.e(LOG, "Error al crear la base de datos: " + e);
        }
        Log.i(LOG, "Base de datos creada");
    }

    /**
     * Realiza la actualización de la base de datos en el dispositivo móvil
     * @param db Base de datos de SQLite
     * @param oldVersion Número de la versión anterior de la base de datos
     * @param newVersion Número de la versión actual de la base de datos
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        try
        {
            Log.i(LOG, "Actualizando la base de datos");
            //Se elimina el contenido de las tablas para volverlas a crear
            db.execSQL(DROP_TABLE_IF_EXISTS + tableFilm);
            onCreate(db);
        }
        catch(final SQLException e)
        {
            e.printStackTrace();
        }
        catch (RuntimeException e)
        {
            Log.i(LOG, "Error al actualizar la base de datos: " + e);
        }
    }

    /**
     * Elimina el contenido de la tabla de las películas
     */
    private void deleteTableSubject()
    {
        try (SQLiteDatabase db = getReadableDatabase()) {
            db.execSQL(DROP_TABLE_IF_EXISTS + tableFilm);
            db.execSQL(createTableFilmQuery);
        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            Log.i(LOG, "Error al eliminar la tabla: " + e);
        }
    }

    /**
     * Realiza la inserción de los datos de una película a la base de datos
     * @param film Datos de la película
     * @param pDB Base de datos de SQLite
     * @return Valor booleano indicando si se ha introducido o no la película a la base de datos
     */
    public boolean newFilm(Film film, SQLiteDatabase pDB)
    {
        boolean verify = false;
        SQLiteDatabase db = pDB;

        try
        {
            //Comprueba si la base datos ya tiene una conexión activa
            if(null == db)
            {
                db = getWritableDatabase();
            }
            final String sql = INSERT_INTO + tableFilm +
                    "(title, year, duration, genres, actors, director, countryMade, originalLanguage, imagePoster, languageSeen, citySeen, countrySeen, dateSeen) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);";
            SQLiteStatement statement = db.compileStatement(sql);

            statement.bindString(1, film.getTitle());
            statement.bindString(2, film.getYear());
            statement.bindString(3, film.getDuration());
            statement.bindString(4, film.getGenres());
            statement.bindString(5, film.getActors());
            statement.bindString(6, film.getDirector());
            statement.bindString(7, film.getCountryMade());
            statement.bindString(8, film.getOriginalLanguage());
            statement.bindString(9, film.getImagePoster());
            statement.bindString(10, film.getLanguageSeen());
            statement.bindString(11, film.getCitySeen());
            statement.bindString(12, film.getCountrySeen());
            statement.bindString(13, film.getDateSeen());
            statement.executeInsert();
            verify = true;
        }
        catch(final SQLException e)
        {
            e.printStackTrace();
        }
        catch(RuntimeException e)
        {
            Log.e(LOG, "Error al insertar la película = " + e);
            verify = false;
        }
        finally
        {
            if(db != null)
            {
                db.close();
            }
        }
        return verify;
    }

    /**
     * Obtiene el listado de películas disponible en la base de datos
     * @return Listado de películas completo disponible
     */
    public ArrayList<Film> getListFilms()
    {
        ArrayList<Film> listFilms = new ArrayList<>();
        SQLiteDatabase db = null;
        try
        {
            String selectQuery = SELECT_FROM + tableFilm;
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if(cursor.moveToFirst())
            {
                /*
                    Se recorre el cursor hasta que no haya más registros de las asignaturas en la base de datos,
                    y se añade a la lista de películas los datos almacenados en la misma
                 */
                do
                {
                    Film film = new Film();
                    film.setIdFilm(cursor.getInt(cursor.getColumnIndexOrThrow("idFilm")));
                    film.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                    film.setYear(cursor.getString(cursor.getColumnIndexOrThrow("year")));
                    film.setDuration(cursor.getString(cursor.getColumnIndexOrThrow("duration")));
                    film.setGenres(cursor.getString(cursor.getColumnIndexOrThrow("genres")));
                    film.setActors(cursor.getString(cursor.getColumnIndexOrThrow("actors")));
                    film.setDirector(cursor.getString(cursor.getColumnIndexOrThrow("director")));
                    film.setCountryMade(cursor.getString(cursor.getColumnIndexOrThrow("countryMade")));
                    film.setOriginalLanguage(cursor.getString(cursor.getColumnIndexOrThrow("originalLanguage")));
                    film.setImagePoster(cursor.getString(cursor.getColumnIndexOrThrow("imagePoster")));
                    film.setLanguageSeen(cursor.getString(cursor.getColumnIndexOrThrow("languageSeen")));
                    film.setCitySeen(cursor.getString(cursor.getColumnIndexOrThrow("citySeen")));
                    film.setCountrySeen(cursor.getString(cursor.getColumnIndexOrThrow("countrySeen")));
                    film.setDateSeen(cursor.getString(cursor.getColumnIndexOrThrow("dateSeen")));
                    listFilms.add(film);
                }
                while(cursor.moveToNext());
            }
            cursor.close();
        }
        catch(RuntimeException e)
        {
            Log.e(LOG, "Error al obtener el listado de películas: " + e);
            listFilms = new ArrayList<>();
        }
        finally
        {
            if(null != db)
            {
                db.close();
            }
        }
        return listFilms;
    }

    /**
     * Obtiene los datos de una película de la base de datos
     * @param idFilm identificador de una película
     * @return Datos del una película
     */
    public Film getFilm(int idFilm)
    {
        Film film = new Film();
        SQLiteDatabase db = null;
        try
        {
            String selectQuery = SELECT_FROM + tableFilm + " WHERE idFilm = " + idFilm;

            db = getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if(cursor.moveToFirst())
            {
                /*
                    Se obtiene los datos de la película a partir de la consulta a la base de datos
                 */
                do
                {
                    film.setIdFilm(cursor.getInt(cursor.getColumnIndexOrThrow("idFilm")));
                    film.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                    film.setYear(cursor.getString(cursor.getColumnIndexOrThrow("year")));
                    film.setDuration(cursor.getString(cursor.getColumnIndexOrThrow("duration")));
                    film.setGenres(cursor.getString(cursor.getColumnIndexOrThrow("genres")));
                    film.setActors(cursor.getString(cursor.getColumnIndexOrThrow("actors")));
                    film.setDirector(cursor.getString(cursor.getColumnIndexOrThrow("director")));
                    film.setCountryMade(cursor.getString(cursor.getColumnIndexOrThrow("countryMade")));
                    film.setOriginalLanguage(cursor.getString(cursor.getColumnIndexOrThrow("originalLanguage")));
                    film.setImagePoster(cursor.getString(cursor.getColumnIndexOrThrow("imagePoster")));
                    film.setLanguageSeen(cursor.getString(cursor.getColumnIndexOrThrow("languageSeen")));
                    film.setCitySeen(cursor.getString(cursor.getColumnIndexOrThrow("citySeen")));
                    film.setCountrySeen(cursor.getString(cursor.getColumnIndexOrThrow("countrySeen")));
                    film.setDateSeen(cursor.getString(cursor.getColumnIndexOrThrow("dateSeen")));
                }
                while(cursor.moveToNext());
            }
            cursor.close();
        }
        catch(RuntimeException e)
        {
            Log.e(LOG, "Error al obtener la película: " + e);
            film = new Film();
        }
        finally
        {
            if(null != db)
            {
                db.close();
            }
        }
        return film;
    }

    /**
     * Actualizar los datos de una película de la base de datos
     * @param film Datos de una asignatura
     */
    public void updateFilm(Film film)
    {
        try
        {
            // Se ejecuta la consulta para actualizar los datos de la película a partir del id de la película
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("title", film.getTitle());
            values.put("year", film.getYear());
            values.put("duration", film.getDuration());
            values.put("genres", film.getGenres());
            values.put("actors", film.getActors());
            values.put("director", film.getDirector());
            values.put("countryMade", film.getCountryMade());
            values.put("originalLanguage", film.getOriginalLanguage());
            values.put("imagePoster", film.getImagePoster());
            values.put("languageSeen", film.getLanguageSeen());
            values.put("citySeen", film.getCitySeen());
            values.put("countrySeen", film.getCountrySeen());
            values.put("dateSeen", film.getDateSeen());

            db.update(tableFilm, values, "idFilm=" + film.getIdFilm(), null);

        }
        catch(final NumberFormatException e)
        {
            e.printStackTrace();
        }
        catch(RuntimeException e)
        {
            Log.e(LOG, "Error al actualizar los datos de la película: " + e);
        }
    }

    /**
     * Eliminar una película de la base de datos
     * @param idFilm identificador de una asignatura
     */
    public void deleteFilm(int idFilm)
    {
        try
        {
            // Se ejecuta la consulta para eliminar la película a partir del id de la película
            SQLiteDatabase db = getReadableDatabase();
            db.delete(tableFilm, "idFilm=" + idFilm, null);

        }
        catch(final NumberFormatException e)
        {
            e.printStackTrace();
        }
        catch(RuntimeException e)
        {
            Log.e(LOG, "Error al eliminar una película: " + e);
        }
    }
}
