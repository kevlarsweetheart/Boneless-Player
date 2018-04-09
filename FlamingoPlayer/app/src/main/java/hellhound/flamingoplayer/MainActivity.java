package hellhound.flamingoplayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static ArrayList<MenuItem> homeItems;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private HomeScreenAdapter adapter;
    public DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setHomeItems();
        db = new DBHelper(getApplicationContext());
        for (ArtistItem item : searchForArtists()){
            db.addArtist(item);
        }


        recyclerView = (RecyclerView) findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomeScreenAdapter(this, homeItems);
        recyclerView.setAdapter(adapter);

        db.close();
    }


    private void setHomeItems(){
        homeItems = new ArrayList<>();
        homeItems.add(new HomeScreenItem("Artists"));
        homeItems.add(new HomeScreenItem("Albums"));
        homeItems.add(new HomeScreenItem("Songs"));
        homeItems.add(new HomeScreenItem("Playlists"));
        homeItems.add(new HomeScreenItem("Queue"));
        homeItems.add(new HomeScreenItem("Queue"));
        homeItems.add(new HomeScreenItem("Queue"));
        homeItems.add(new HomeScreenItem("Queue"));
    }


    private ArrayList<ArtistItem> searchForArtists(){
        ArrayList<ArtistItem> res = new ArrayList<>();

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
