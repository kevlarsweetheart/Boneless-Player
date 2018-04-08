package hellhound.flamingoplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static ArrayList<MenuItem> homeItems;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private HomeScreenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setHomeItems();

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomeScreenAdapter(this, homeItems);
        recyclerView.setAdapter(adapter);

    }

    private void setHomeItems(){
        homeItems = new ArrayList<MenuItem>();
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
