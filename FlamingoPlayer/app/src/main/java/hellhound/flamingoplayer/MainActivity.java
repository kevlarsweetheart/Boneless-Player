package hellhound.flamingoplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
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
    private PlayControls playControls;

    //Service fields
    private MusicService musicService;
    private boolean isBound = false;
    private BroadcastReceiver broadcastReceiver;
    public final static String BROADCAST_ACTION = "playservice";

    public final static int TASK_PROGRESS = 0;
    public final static String PARAM_PROGRESS = "progress";
    public final static String PARAM_MAX = "max";
    public final static String PARAM_TASK = "task";

    public final static int TASK_INFO = 1;
    public final static String PARAM_TRACK_NUM = "track_number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topHeader = (TopHeader) getSupportFragmentManager().findFragmentById(R.id.fragment);
        playControls = (PlayControls) getSupportFragmentManager().findFragmentById(R.id.fragment2);

        //Setting up RecyclerView, Database and States stack
        setHomeItems();
        state = new Stack<>();
        state.push(STATES.HOME);
        db = DBHelper.getInstance(getApplicationContext());


        recyclerView = (RecyclerView) findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomeScreenAdapter(this, homeItems);
        recyclerView.setAdapter(adapter);
        db.close();

        //Setting up MusicService
        Intent musicIntent = new Intent(this, MusicService.class);
        bindService(musicIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int task = intent.getIntExtra(PARAM_TASK, 0);
                switch (task){
                    case TASK_PROGRESS:
                        int progress = intent.getIntExtra(PARAM_PROGRESS, 0);
                        int max = intent.getIntExtra(PARAM_MAX, 0);
                        //TODO
                        break;

                    case TASK_INFO:
                        int num = intent.getIntExtra(PARAM_TRACK_NUM, 0);
                        //TODO
                        break;
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
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


    /*--------------------------------------------------------------------------------------------*/
    /*--------------------------------- Methods for MusicService ---------------------------------*/
    /*--------------------------------------------------------------------------------------------*/

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Connected?");
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
            Log.i(TAG, "onServiceConnected");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
}
