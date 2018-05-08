package hellhound.flamingoplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
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
    private final static String KEY_RELEASE_YEAR = "release_year";

    private final static String TABLE_COVERS = "covers";
    private final static String KEY_COVER_ID = "cover_id";
    private final static String KEY_COVER_PATH = "cover_path";

    private final static String TABLE_TRACKS = "tracks";
    private final static String KEY_TRACK_ID = "track_id";
    private final static String KEY_TRACK_NAME = "track_name";
    private final static String KEY_TRACK_PATH = "path";
    private final static String KEY_TRACK_NUMBER = "track_number";
    private final static String KEY_TRACK_LENGTH = "track_length";


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
                KEY_COVER_ID + " INTEGER, " +
                KEY_RELEASE_YEAR + " INTEGER, " +
                "FOREIGN KEY (" + KEY_COVER_ID + ") REFERENCES " + TABLE_COVERS + "(" + KEY_COVER_ID + "), " +
                "FOREIGN KEY (" + KEY_ARTIST_ID + ") REFERENCES " + TABLE_ARTISTS + "(" + KEY_ARTIST_ID +"));";

        String createCovers = "CREATE TABLE " + TABLE_COVERS + "(" +
                KEY_COVER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_COVER_PATH + " TEXT NOT NULL);";

        String createTracks = "CREATE TABLE " + TABLE_TRACKS + "(" +
                KEY_TRACK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TRACK_NAME + " TEXT NOT NULL, " +
                KEY_TRACK_PATH + " TEXT, " +
                KEY_TRACK_NUMBER + " INTEGER, " +
                KEY_TRACK_LENGTH + " INTEGER, " +
                KEY_ALBUM_ID + " INTEGER, " +
                KEY_ARTIST_ID + " INTEGER, " +
                "FOREIGN KEY (" + KEY_ALBUM_ID + ") REFERENCES " + TABLE_ALBUMS + "(" + KEY_ALBUM_ID + "), " +
                "FOREIGN KEY (" + KEY_ARTIST_ID + ") REFERENCES " + TABLE_ARTISTS + "(" + KEY_ARTIST_ID + "));";

        Log.i(TAG, createArtists);
        Log.i(TAG, createCovers);
        Log.i(TAG, createAlbums);
        Log.d(TAG, createTracks);

        db.execSQL(createArtists);
        Log.i(TAG, "Created Artists table");
        db.execSQL(createCovers);
        Log.i(TAG, "Created Covers table");
        db.execSQL(createAlbums);
        Log.i(TAG, "Created Albums table");
        db.execSQL(createTracks);
        Log.i(TAG, "Create Tracks table");
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



    private int itemCounter(String query){
        int result = 0;
        SQLiteDatabase db = this.getReadableDatabase();

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
    /*------------------------------------ Artist methods ----------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    public long addArtist(ArtistItem item){

        long artist_id = artistExists(item);

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

    public long artistExists(ArtistItem item){
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

    public ArtistItem getArtistBy(AlbumItem album){
        if(albumExists(album) >= 0){
            String query = "SELECT " + KEY_ARTIST_NAME + " FROM " +TABLE_ARTISTS +
                    " WHERE " + KEY_ARTIST_ID + " = " + album.getArtistId();
            return getArtistBy(query);
        } else {
            return null;
        }
    }


    public ArtistItem getArtistBy(TrackItem track){
        if(trackExists(track) >= 0){
            String query = "SELECT " + KEY_ARTIST_NAME + " FROM " +TABLE_ARTISTS +
                    " WHERE " + KEY_ARTIST_ID + " = " + track.getArtistId();
            return getArtistBy(query);
        } else {
            return null;
        }
    }

    private ArtistItem getArtistBy(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()){
            String name = c.getString(c.getColumnIndex(KEY_ARTIST_NAME));
            c.close();
            db.close();
            return new ArtistItem(name);
        } else {
            db.close();
            return null;
        }
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
            values.put(KEY_COVER_ID, album.getCoverId());

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
                long coverId = c.getLong(c.getColumnIndex(KEY_COVER_ID));
                Log.i(TAG, "Added " + name);
                MenuItem item = new AlbumItem(name);
                ((AlbumItem) item).setArtistId(artist_id);
                ((AlbumItem) item).setReleaseYear(year);
                ((AlbumItem) item).setCoverId(coverId);
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
                long coverId = c.getLong(c.getColumnIndex(KEY_COVER_ID));
                Log.i(TAG, "Added " + name);
                MenuItem item = new AlbumItem(name);
                ((AlbumItem) item).setArtistId(artist_id);
                ((AlbumItem) item).setReleaseYear(year);
                ((AlbumItem) item).setCoverId(coverId);
                res.add(item);
                c.moveToNext();
            }
            c.close();
        }
        db.close();
        return res;
    }


    public int getAlbumCount(){
        String query = "SELECT COUNT(" + KEY_ALBUM_NAME + ") FROM " + TABLE_ALBUMS;
        return itemCounter(query);
    }

    public int getAlbumsCount(ArtistItem artist){
        long artistId = artistExists(artist);

        if(artistId == -1){
            return 0;
        } else {
            String query = "SELECT COUNT(" + KEY_ALBUM_NAME + ") FROM " + TABLE_ALBUMS +
                    " WHERE " + KEY_ARTIST_ID + " = " + artistId;
            return itemCounter(query);
        }
    }


    public AlbumItem getAlbumBy(TrackItem track){
        if(trackExists(track) >= 0){
            String query = "SELECT " + KEY_ARTIST_NAME + " FROM " +TABLE_ARTISTS +
                    " WHERE " + KEY_TRACK_ID + " = " + track.getArtistId();
            return getAlbumBy(query);
        } else {
            return null;
        }
    }


    private AlbumItem getAlbumBy(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()){
            String name = c.getString(c.getColumnIndex(KEY_ARTIST_NAME));
            long coverId = c.getLong(c.getColumnIndex(KEY_COVER_ID));
            long artistId = c.getLong(c.getColumnIndex(KEY_ARTIST_ID));
            int year = c.getInt(c.getColumnIndex(KEY_RELEASE_YEAR));
            AlbumItem album = new AlbumItem(name);
            album.setCoverId(coverId);
            album.setArtistId(artistId);
            album.setReleaseYear(year);
            c.close();
            db.close();
            return album;
        } else {
            db.close();
            return null;
        }
    }


    public long updateAlbumCover(AlbumItem album, long coverId){
        long album_id = albumExists(album);
        Log.i(TAG, "Album found at " + String.valueOf(album_id));
        if(album_id >= 0){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_COVER_ID, coverId);
            db.update(TABLE_ALBUMS, values, KEY_ALBUM_ID + " = " + String.valueOf(album_id), null);
            Log.i(TAG, "Updated album art");
            return album_id;
        } else {
            return -1;
        }
    }


    /*--------------------------------------------------------------------------------------------*/
    /*----------------------------------Cover art methods ----------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/

    public long addCover(String path){
        long cover_id = coverExists(path);
        if(cover_id == -1){
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_COVER_PATH, path);

            cover_id = db.insert(TABLE_COVERS, null, values);
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
        String query = "SELECT * FROM " + TABLE_COVERS +
                " WHERE \"" + path + "\" = " + KEY_COVER_PATH;

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


    public String getCoverById(long id){
        String res = "None";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COVERS +
                " WHERE " + KEY_COVER_ID + " = " +id;

        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()){
            res = c.getString(c.getColumnIndex(KEY_COVER_PATH));
            Log.i(TAG, res + " found");
        }
        c.close();
        db.close();
        return res;
    }


    /*--------------------------------------------------------------------------------------------*/
    /*------------------------------------- Track methods ----------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/

    public long addTrack(TrackItem track){
        long trackId = trackExists(track);
        if(trackId == -1){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(KEY_TRACK_NAME, track.getName());
            values.put(KEY_TRACK_PATH, track.getPath());
            values.put(KEY_TRACK_LENGTH, track.getLength());
            values.put(KEY_TRACK_NUMBER, track.getTrackNumber());
            values.put(KEY_ALBUM_ID, track.getAlbumId());
            values.put(KEY_ARTIST_ID, track.getArtistId());

            trackId = db.insert(TABLE_TRACKS, null, values);
            db.close();
        }
        Log.i(TAG, "Put track at " + String.valueOf(trackId));
        return trackId;
    }


    public long trackExists(TrackItem track){
        long res = -1;
        Log.i(TAG, "Searching for " + track.getName());
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + KEY_TRACK_ID + " FROM " + TABLE_TRACKS +
                " WHERE " + KEY_TRACK_NAME + " = \"" + track.getName() + "\"" +
                " AND " + KEY_ALBUM_ID + " = " + track.getAlbumId() +
                " AND " + KEY_ARTIST_ID + " = " + track.getArtistId() + ";";

        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()){
            res = c.getLong(c.getColumnIndex(KEY_TRACK_ID));
        }
        c.close();
        db.close();
        Log.i(TAG, "Track found at " + String.valueOf(res));
        return  res;
    }


    public ArrayList<MenuItem> getTracksOf(){
        String query = "SELECT * FROM " + TABLE_TRACKS +
                " ORDER BY " + KEY_TRACK_NAME + " ASC;";
        return tracksProvider(query);
    }


    public ArrayList<MenuItem> getTracksOf(AlbumItem album){
        long albumId = albumExists(album);

        if (albumId == -1){
            return null;
        } else {
            String query = "SELECT * FROM " + TABLE_TRACKS +
                    " WHERE " + KEY_ALBUM_ID + " = " + albumId +
                    " ORDER BY " + KEY_TRACK_NUMBER + " ASC;";
            return tracksProvider(query);
        }
    }


    public ArrayList<MenuItem> getTracksOf(ArtistItem artist){
        long artistId = artistExists(artist);

        if (artistId == -1){
            return null;
        } else {
            String query = "SELECT * FROM " + TABLE_TRACKS +
                    " WHERE " + KEY_ARTIST_ID + " = " + artistId +
                    " ORDER BY " + KEY_TRACK_NAME + " ASC;";
            return tracksProvider(query);
        }
    }


    private ArrayList<MenuItem> tracksProvider(String query){
        Log.i(TAG, query);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c != null){
            c.moveToFirst();
            ArrayList<MenuItem> res = new ArrayList<>();
            while (!c.isAfterLast()) {
                String name = c.getString(c.getColumnIndex(KEY_TRACK_NAME));
                String path = c.getString(c.getColumnIndex(KEY_TRACK_PATH));
                int length = c.getInt(c.getColumnIndex(KEY_TRACK_LENGTH));
                int trackNumber = c.getInt(c.getColumnIndex(KEY_TRACK_NUMBER));
                long artistId = c.getLong(c.getColumnIndex(KEY_ARTIST_ID));
                long albumId = c.getLong(c.getColumnIndex(KEY_ALBUM_ID));

                Log.i(TAG, "Added " + name);
                MenuItem item = new TrackItem(name);
                ((TrackItem) item).setPath(path);
                ((TrackItem) item).setLength(length);
                ((TrackItem) item).setTrackNumber(trackNumber);
                ((TrackItem) item).setAlbumId(albumId);
                ((TrackItem) item).setArtistId(artistId);
                res.add(item);
                c.moveToNext();
            }
            c.close();
            db.close();
            return res;
        } else {
            db.close();
            return null;
        }
    }


    public int getTracksCount(){
        String query = "SELECT COUNT(" + KEY_TRACK_ID + ") FROM " + TABLE_TRACKS;
        return itemCounter(query);
    }

    public int getTracksCount(AlbumItem album){
        long albumId = albumExists(album);

        if (albumId == -1){
            return 0;
        } else {
            Log.i(TAG, "Counting tracks of " + album.getName());
            String query = "SELECT COUNT(" + KEY_TRACK_ID + ") FROM " + TABLE_TRACKS +
                    " WHERE " + KEY_ALBUM_ID + " = " + albumId;
            return itemCounter(query);
        }
    }

    public int getTracksCount(ArtistItem artist){
        long artistId = artistExists(artist);

        if(artistId == -1){
            return 0;
        } else {
            String query = "SELECT COUNT(" + KEY_TRACK_ID + ") FROM " + TABLE_TRACKS +
                    " WHERE " + KEY_ARTIST_ID + " = " + artistId;
            return itemCounter(query);
        }
    }
}
