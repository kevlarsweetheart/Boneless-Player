package hellhound.humbleplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state = new Stack<String>();
        setHomeItems();

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HomeScreenAdapter(this, homeItems);
        recyclerView.setAdapter(adapter);
    }

    private void setHomeItems(){
        homeItems = new ArrayList<MenuItem>();
        homeItems.add(new HomeScreenItem("Albums", R.drawable.background_album));
        homeItems.add(new HomeScreenItem("Artists", R.drawable.background_artists));
        homeItems.add(new HomeScreenItem("Songs", R.drawable.background_song));
        homeItems.add(new HomeScreenItem("Playlists", R.drawable.background_playlist));
        homeItems.add(new HomeScreenItem("Queue", R.drawable.background_queue));
    }
}
