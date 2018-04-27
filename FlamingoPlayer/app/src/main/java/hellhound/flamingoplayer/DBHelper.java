package hellhound.flamingoplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper INSTANCE;
    private final static String TAG = "db_debug";

    private final static String DB_NAME = "flamingo.db";
    private final static int DATABASE_VERSION = 1;

    private final static String TABLE_ARTISTS = "artists";
    private final static String KEY_ARTIST_ID = "artist_id";
    private final static String KEY_ARTIST_NAME = "artist_name";

    private final static String TABLE_ALBUMS = "albums";
    private final static String KEY_ALBUM_ID = "album_id";
    private final static String KEY_ALBUM_NAME = "album_name";
    private final static String KEY_COVER_ID = "cover_art_id";
    private final static String KEY_RELEASE_YEAR = "release_year";

    private final static String TABLE_COVER_ARTS = "cover_arts";
    private final static String KEY_COVER_ART_PATH = "cover_art_path";


    public static synchronized DBHelper getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new DBHelper(context.getApplicationContext());
        }
        return INSTANCE;
    }


    private DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createArtists = "CREATE TABLE " + TABLE_ARTISTS + "(" +
                KEY_ARTIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ARTIST_NAME + " TEXT NOT NULL)";

        String createAlbums = "CREATE TABLE " + TABLE_ALBUMS + "(" +
                KEY_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ALBUM_NAME + " TEXT NOT NULL, " +
                KEY_ARTIST_ID + " INTEGER, " +
                KEY_COVER_ID + "INTEGER, " +
                KEY_RELEASE_YEAR + " INTEGER, " +
                "FOREIGN KEY (" + KEY_COVER_ID + ") REFERENCES " + TABLE_COVER_ARTS + "(" + KEY_COVER_ID +"), " +
                "FOREIGN KEY (" + KEY_ARTIST_ID + ") REFERENCES " + TABLE_ARTISTS + "(" + KEY_ARTIST_ID +"));";

        String createCoverArts = "CREATE TABLE " + TABLE_COVER_ARTS + "(" +
                KEY_COVER_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_COVER_ART_PATH + "TEXT);";


        Log.i(TAG, "Created Artists table");
        db.execSQL(createArtists);
        db.execSQL(createAlbums);
        db.execSQL(createCoverArts);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);

        onCreate(db);
    }


    public void showTables(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                Log.i(TAG, "Table Name=> "+c.getString(0));
                c.moveToNext();
            }
        }
        c.close();
        db.close();
    }

    public void showColumnsOF(String table){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(table, null, null, null, null, null, null);
        String[] columnNames = c.getColumnNames();
        for (String column : columnNames){
            Log.i(TAG, column + " in " + table);
        }
        db.close();
        c.close();
    }


    /*--------------------------------------------------------------------------------------------*/
    /*------------------------------------ Artist methods ----------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    public long addArtist(ArtistItem item){

        long artist_id = artistExists(item, false);

        if(artist_id == -1){
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_ARTIST_NAME, item.getName());

            artist_id = db.insert(TABLE_ARTISTS, null, values);
            Log.i(TAG, "Put artist, rowid = " + String.valueOf(artist_id));
            db.close();
        }
        Log.i(TAG, item.getName() + " returns " + String.valueOf(artist_id));
        return artist_id;
    }

    public long artistExists(ArtistItem item, boolean updateFound){
        Log.i(TAG, "Searching for " + item. getName());
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_ARTIST_ID + " FROM " + TABLE_ARTISTS +
                " WHERE \"" + item.getName() + "\" = " + KEY_ARTIST_NAME + ";";
        Cursor c = db.rawQuery(query, null);

        long res = -1;
        if(c.moveToFirst()){
            res = c.getLong(c.getColumnIndex(KEY_ARTIST_ID));
            Log.i(TAG, item.getName() + " found at " + String.valueOf(res));
        }
        c.close();
        db.close();
        Log.i(TAG, "Artist found at position " + String.valueOf(res));
        return res;
    }

    public ArrayList<MenuItem> getAllArtists(){
        ArrayList<MenuItem> res = new ArrayList<MenuItem>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ARTISTS + " ORDER BY " +
                KEY_ARTIST_NAME + " ASC;";

        Cursor c = db.rawQuery(query, null);
        Log.i(TAG, "Created cursor");

        if(c != null){
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String name = c.getString(c.getColumnIndex(KEY_ARTIST_NAME));
                Log.i(TAG, "Added " + name);
                MenuItem item = new ArtistItem(name);
                res.add(item);
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        return res;
    }


    /*--------------------------------------------------------------------------------------------*/
    /*------------------------------------ Album methods -----------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/

    public long addAlbum(AlbumItem album){
        long album_id = albumExists(album);
        if(album_id == -1){
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_ALBUM_NAME, album.getName());
            values.put(KEY_ARTIST_ID, album.getArtistId());
            values.put(KEY_RELEASE_YEAR, album.getReleaseYear());

            album_id = db.insert(TABLE_ALBUMS, null, values);
            Log.i(TAG, "Put album, rowid = " + String.valueOf(album_id));
            db.close();
        }
        Log.i(TAG, album.getName() + " returns " + String.valueOf(album_id));

        return album_id;
    }


    public long albumExists(AlbumItem album){
        long res = -1;
        Log.i(TAG, "Searching for " + album.getName());
        SQLiteDatabase db = this.getReadableDatabase();
        Log.i(TAG, "Album's artist id = " + String.valueOf(album.getArtistId()));
        String query = "SELECT " + KEY_ALBUM_ID + " FROM " + TABLE_ALBUMS +
                " WHERE " + KEY_ALBUM_NAME + " = \"" + album.getName() +
                "\" AND " + KEY_ARTIST_ID + " = " + album.getArtistId() + ";";

        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()){
            res = c.getLong(c.getColumnIndex(KEY_ALBUM_ID));
            Log.i(TAG, album.getName() + " found at " + String.valueOf(res));
        }
        c.close();
        db.close();
        Log.i(TAG, "Album found at position " + String.valueOf(res));
        return res;
    }


    public ArrayList<MenuItem> getAlbumsOfArtist(ArtistItem artist){
        ArrayList<MenuItem> res = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ALBUMS +
                " WHERE " + KEY_ARTIST_ID + " IN " +
                "( SELECT " + KEY_ARTIST_ID + " FROM " + TABLE_ARTISTS +
                " WHERE " + KEY_ARTIST_NAME + " = \"" + artist.getName() + "\");";
        Cursor c = db.rawQuery(query, null);

        if(c != null){
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String name = c.getString(c.getColumnIndex(KEY_ALBUM_NAME));
                int year = c.getInt(c.getColumnIndex(KEY_RELEASE_YEAR));
                long artist_id = c.getLong(c.getColumnIndex(KEY_ARTIST_ID));
                Log.i(TAG, "Added " + name);
                MenuItem item = new AlbumItem(name);
                ((AlbumItem) item).setArtistId(artist_id);
                ((AlbumItem) item).setReleaseYear(year);
                res.add(item);
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        return res;
    }


    public ArrayList<MenuItem> getAllAlbums(){
        ArrayList<MenuItem> res = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ALBUMS +
                " ORDER BY " + KEY_ALBUM_NAME + " ASC;";
        Cursor c = db.rawQuery(query, null);
        if(c != null){
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String name = c.getString(c.getColumnIndex(KEY_ALBUM_NAME));
                int year = c.getInt(c.getColumnIndex(KEY_RELEASE_YEAR));
                long artist_id = c.getLong(c.getColumnIndex(KEY_ARTIST_ID));
                Log.i(TAG, "Added " + name);
                MenuItem item = new AlbumItem(name);
                ((AlbumItem) item).setArtistId(artist_id);
                ((AlbumItem) item).setReleaseYear(year);
                res.add(item);
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        return res;
    }

    public int getAlbumCount(){
        return getAlbumsCount(null);
    }

    public int getAlbumsCount(ArtistItem artist){
        int result = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(" + KEY_ALBUM_NAME + ") FROM " + TABLE_ALBUMS;

        if (artist != null){
            query += " WHERE "+ KEY_ARTIST_ID + " IN " +
                    "( SELECT " + KEY_ARTIST_ID + " FROM " + TABLE_ARTISTS +
                    " WHERE " + KEY_ARTIST_NAME + " = \"" + artist.getName() + "\");";
        }

        Cursor c = db.rawQuery(query, null);

        if(c != null){
            c.moveToFirst();
            result = c.getInt(0);
            c.close();
        }
        db.close();
        return result;
    }


    /*--------------------------------------------------------------------------------------------*/
    /*----------------------------------Cover art methods ----------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/

    public long addCover(String path){
        long cover_id = coverExists(path);
        if(cover_id == -1){
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_COVER_ART_PATH, path);

            cover_id = db.insert(TABLE_ALBUMS, null, values);
            Log.i(TAG, "Put cover at rowid = " + String.valueOf(cover_id));
            db.close();
        }
        Log.i(TAG, path + " returns " + String.valueOf(cover_id));

        return cover_id;
    }

    public long coverExists(String path){
        long res = -1;
        Log.i(TAG, "Searching for " + path);
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COVER_ARTS +
                " WHERE " + KEY_COVER_ART_PATH + " = " + path;

        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()){
            res = c.getLong(c.getColumnIndex(KEY_COVER_ID));
            Log.i(TAG, path + " found at " + String.valueOf(res));
        }
        c.close();
        db.close();
        Log.i(TAG, "Cover found at position " + String.valueOf(res));
        return res;
    }

    public String getCoverOfAlbum(AlbumItem album){
        String res = "None";
        SQLiteDatabase db = this.getReadableDatabase();
        Log.i(TAG, "Album's artist id = " + String.valueOf(album.getArtistId()));
        String query = "SELECT * FROM " + TABLE_COVER_ARTS +
                " WHERE " + KEY_COVER_ID + " IN (SELECT " + KEY_COVER_ID +
                " FROM " + TABLE_ALBUMS + " WHERE " +
                KEY_ARTIST_ID + " = " + album.getArtistId();

        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()){
            res = c.getString(c.getColumnIndex(KEY_COVER_ART_PATH));
            Log.i(TAG, res + " found");
        }
        c.close();
        db.close();
        return res;
    }
}
