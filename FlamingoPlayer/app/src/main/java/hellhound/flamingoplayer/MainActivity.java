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
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements TopHeader.TopHeaderListener{

    private final static String TAG = "db_debug";
    private static ArrayList<MenuItem> homeItems;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private HomeScreenAdapter adapter;
    public DBHelper db;
    public enum STATES {HOME, ARTISTS, ALBUMS, TRACKS, PLAYLISTS, CHARTS}
    private Stack<STATES> state;
    private TopHeader topHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setHomeItems();
        topHeader = (TopHeader) getSupportFragmentManager().findFragmentById(R.id.fragment);
        state = new Stack<>();
        state.push(STATES.HOME);
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
        homeItems.add(new HomeScreenItem("Tracks"));
        homeItems.add(new HomeScreenItem("Playlists"));
        homeItems.add(new HomeScreenItem("Queue"));
    }

    public  ArrayList<MenuItem> getHomeItems(){
        return homeItems;
    }


    /*--------------------------------------------------------------------------------------------*/
    /*------------------------------- Methods for state managing ---------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    public STATES getState(){
        return state.peek();
    }

    public STATES changeStateBack(){
        STATES oldState = getState();
        if (oldState != STATES.HOME){
            oldState = state.pop();
        }

        STATES currState = getState();
        topHeader.switchBackButton(currState);
        topHeader.changeTitleText(currState);
        return oldState;
    }

    public void changeStateNext(STATES newState){
        state.push(newState);
        topHeader.switchBackButton(newState);
        topHeader.changeTitleText(newState);
    }

    @Override
    public void backButtonClicked() {
        while (getState() != STATES.HOME){
            changeStateBack();
        }
        changeStateNext(STATES.ARTISTS);
        adapter.handleClicks(1, HomeScreenAdapter.ACTIONS.BACK);
    }
}
