package hellhound.humbleplayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Debugging";
    private ArrayList<MenuItem> homeItems;
    private RecyclerView recyclerView;
    private HomeScreenAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Stack<String> state;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state = new Stack<String>();
        db = new DatabaseHelper(getApplicationContext());
        db.addArtist(new ArtistItem("Unknown"));
        db.addArtist(new ArtistItem("Unknown1"));
        db.addArtist(new ArtistItem("Unknown2"));
        db.addArtist(new ArtistItem("Unknown3"));
        db.addArtist(new ArtistItem("Unknown4"));
        db.addArtist(new ArtistItem("Unknown5"));
        db.addArtist(new ArtistItem("Unknown6"));
        db.addArtist(new ArtistItem("Unknown7"));
        db.addArtist(new ArtistItem("Unknown8"));


        for (MenuItem artist : searchForArtists()){
            db.addArtist((ArtistItem) artist);
        }

        ArrayList<MenuItem> artists = db.getAllArtists();
        setHomeItems();
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HomeScreenAdapter(this, homeItems, db);
        recyclerView.setAdapter(adapter);
    }


    private void setHomeItems(){
        homeItems = new ArrayList<MenuItem>();
        homeItems.add(new HomeScreenItem("Artists", R.drawable.background_artists));
        homeItems.add(new HomeScreenItem("Albums", R.drawable.background_album));
        homeItems.add(new HomeScreenItem("Songs", R.drawable.background_song));
        homeItems.add(new HomeScreenItem("Playlists", R.drawable.background_playlist));
        homeItems.add(new HomeScreenItem("Queue", R.drawable.background_queue));
    }


    private ArrayList<MenuItem> searchForArtists(){
        ArrayList<MenuItem> res = new ArrayList<>();

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA};
        Cursor cursor = null;

        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = getContentResolver().query(uri, projection, selection, null, null);
            if( cursor != null){
                cursor.moveToFirst();

                while( !cursor.isAfterLast() ){
                    String artist = cursor.getString(1);
                    cursor.moveToNext();
                    res.add(new ArtistItem(artist));
                }

            }

            // print to see list of mp3 files
            for( MenuItem artist : res) {
                Log.i("TAG", artist.getName());
            }

        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }finally{
            if( cursor != null){
                cursor.close();
            }
        }

        return res;
    }
}
