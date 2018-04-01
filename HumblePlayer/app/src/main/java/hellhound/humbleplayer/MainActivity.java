package hellhound.humbleplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private ArrayList<HomeScreenItem> homeItems;
    private RecyclerView recyclerView;
    private HomeScreenAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Stack<String> state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state = new Stack<String>();

        recyclerView = (RecyclerView) findViewById(R.id.rv);


    }
}
