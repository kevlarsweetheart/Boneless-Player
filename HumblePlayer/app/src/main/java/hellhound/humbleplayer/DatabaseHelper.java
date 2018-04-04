package hellhound.humbleplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_database.db";

    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ARTISTS = "artists";

    private static final String KEY_ARTIST_ID = "artist_id";
    private static final String KEY_ARTIST_NAME = "artist_name";
    private static final String KEY_COVER_ART_ID = "cover_art_id";

    private static final String CREATE_TABLE_ARTISTS = "CREATE TABLE " + TABLE_ARTISTS +
            " (" + KEY_ARTIST_NAME + " TEXT UNIQUE NOT NULL, " +
            KEY_COVER_ART_ID + " INTEGER)";     //"FOREIGN KEY(" + KEY_COVER_ART + ") REFERENCES ...

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ARTISTS);

        //creating default artist UNKNOWN
        ArtistItem unknown = new ArtistItem("Unknown");
        addArtist(unknown);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);
        onCreate(db);
    }


    /*------------------------------ Methods for Artists -----------------------------------------*/
    public long addArtist(ArtistItem item){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ARTIST_NAME, item.getName());
        /*
        if (item.getCoverArtId() != -1){
            values.put(KEY_COVER_ART_ID, item.getCoverArtId());
        }*/

        long artist_id = db.insert(TABLE_ARTISTS, null, values);
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
        db.close();
        return res;
    }
}
