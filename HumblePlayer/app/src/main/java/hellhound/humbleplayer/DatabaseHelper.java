package hellhound.humbleplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "Debugging";

    private static final String DATABASE_NAME = "app_database.db";

    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ARTISTS = "artists";

    private static final String KEY_ARTIST_ID = "artist_id";
    private static final String KEY_ARTIST_NAME = "artist_name";
    private static final String KEY_COVER_ART_ID = "cover_art_id";

    private static final String CREATE_TABLE_ARTISTS = "CREATE TABLE " + TABLE_ARTISTS +
            "(" + KEY_ARTIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_ARTIST_NAME + " TEXT NOT NULL UNIQUE, " +
            KEY_COVER_ART_ID + " INTEGER);";     //"FOREIGN KEY(" + KEY_COVER_ART + ") REFERENCES ...

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, CREATE_TABLE_ARTISTS);
        db.execSQL("CREATE TABLE artists(artist_name TEXT);");
        showTables();

        //creating default artist UNKNOWN
        ArtistItem unknown = new ArtistItem("Unknown");
        long pos = addArtist(unknown);
        Log.i(TAG, "Unknown added at position " + String.valueOf(pos));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);
        onCreate(db);
    }

    public void showTables(){
        Log.i(TAG, "Showing tables");
        SQLiteDatabase db = this.getReadableDatabase();
        Log.i(TAG, "Opened db");
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        Log.i(TAG, "created cursor tables");

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                Log.i(TAG, "Table Name=> "+c.getString(0));
                c.moveToNext();
            }
        }
        c.close();
    }

    /*------------------------------ Methods for Artists -----------------------------------------*/
    public long addArtist(ArtistItem item){
        SQLiteDatabase db = this.getWritableDatabase();

        Log.i(TAG, "Putting artist into db");
        ContentValues values = new ContentValues();
        values.put(KEY_ARTIST_NAME, item.getName());
        /*
        if (item.getCoverArtId() != -1){
            values.put(KEY_COVER_ART_ID, item.getCoverArtId());
        }*/

        long artist_id = db.insert(TABLE_ARTISTS, null, values);
        Log.i(TAG, "Put artist, rowid = " + String.valueOf(artist_id));
        db.close();
        return artist_id;
    }

    public ArrayList<MenuItem> getAllArtists(){
        ArrayList<MenuItem> res = new ArrayList<MenuItem>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ARTISTS + " ORDER BY " +
                TABLE_ARTISTS + "." + KEY_ARTIST_NAME + " ASC;";

        Cursor c = db.rawQuery(query, null);

        if(c != null){
            c.moveToFirst();
            do {
                MenuItem item = new ArtistItem(c.getString(c.getColumnIndex(KEY_ARTIST_NAME)));
                ((ArtistItem) item).setCoverArtId(c.getInt(c.getColumnIndex(KEY_COVER_ART_ID)));
                res.add(item);
            } while (!c.isLast());
        }
        c.close();
        db.close();
        return res;
    }
}
