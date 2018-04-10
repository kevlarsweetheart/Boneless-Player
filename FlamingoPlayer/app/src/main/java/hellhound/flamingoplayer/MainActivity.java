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

    private final static String TAG = "db_debug";
    private static ArrayList<MenuItem> homeItems;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private HomeScreenAdapter adapter;
    public DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "Creating db");
        setHomeItems();
        db = DBHelper.getInstance(getApplicationContext());


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
}
