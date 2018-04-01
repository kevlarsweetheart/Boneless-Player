package hellhound.humbleplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private ArrayList<HomeScreenItem> homeItems;
    private Stack<String> state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state = new Stack<String>();

        ListView contentView = (ListView) findViewById(R.id.contentList);
        homeItems = new ArrayList<HomeScreenItem>();
        homeItems.add(new HomeScreenItem("Library", R.drawable.library, HomeScreenItem.Types.HSI));
        homeItems.add(new HomeScreenItem("Folders", R.drawable.folders, HomeScreenItem.Types.HSI));
        homeItems.add(new HomeScreenItem("Charts", R.drawable.charts, HomeScreenItem.Types.HSI));
        homeItems.add(new HomeScreenItem("Queue", R.drawable.queue, HomeScreenItem.Types.HSI));

        adapter = new HomeScreenAdapter(this, homeItems);
        contentView.setAdapter(adapter);

    }
}
