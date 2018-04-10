package hellhound.flamingoplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import android.os.Handler;

public class HomeScreenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "adapter_debug";
    private Context parent;
    private ArrayList<MenuItem> items;
    private Queue<ArrayList<MenuItem>> pendingUpdates = new ArrayDeque<>();

    public HomeScreenAdapter(Context parent, ArrayList<MenuItem> items) {
        this.parent = parent;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        MenuItem.TYPES type = items.get(position).getType();
        switch (type){
            case HOME:
                return 0;

            case ARTIST:
                return 3;

            case PLAY_ALL:
                return 5;
        }
        return 0;
    }


    /*--------------------------------------------------------------------------------------------*/
    /*------------------------------- Update data methods ----------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    public void updateItems(final ArrayList<MenuItem> newList){
        pendingUpdates.add(newList);
        if(pendingUpdates.size() > 1){
            return;
        }
        updateItemsInternal(newList);
    }


    void updateItemsInternal(final ArrayList<MenuItem> newList){
        final ArrayList<MenuItem> oldList = new ArrayList<>(items);

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final DiffUtil.DiffResult res = DiffUtil.calculateDiff(new DiffCallback(oldList, newList), true);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        applyDiffResult(newList, res);
                    }
                });
            }
        }).start();
    }


    protected void applyDiffResult(ArrayList<MenuItem> newList, DiffUtil.DiffResult res){
        pendingUpdates.remove();
        dispatchUpdates(newList, res);
        if(pendingUpdates.size() > 0){
            updateItemsInternal(pendingUpdates.peek());
        }
    }

    protected void dispatchUpdates(ArrayList<MenuItem> newList, DiffUtil.DiffResult res){
        res.dispatchUpdatesTo(this);
        items.clear();
        items.addAll(newList);
    }


    /*--------------------------------------------------------------------------------------------*/
    /* ------------------------------ Create & bind views ----------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder;
        switch (viewType){
            case 0:
                Log.i(TAG, "Creating view holder for home items");
                holder = new ViewHolderHome(inflater.inflate(R.layout.home_item, parent, false));
                ((ViewHolderHome) holder).setListeners();
                return holder;

            case 3:
                Log.i(TAG, "Creating view holder for artist items");
                holder = new ViewHolderArtist(inflater.inflate(R.layout.artist_item, parent, false));
                ((ViewHolderArtist) holder).setListeners();
                return holder;

            case 5:
                Log.i(TAG, "Creating view holder for play_all items");
                holder = new ViewHolderPlayAll(inflater.inflate(R.layout.play_all_item, parent, false));
                return holder;

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MenuItem item = items.get(position);
         switch (item.getType()){
             case HOME:
                 Log.i(TAG, "Binding home item");
                 ((ViewHolderHome) holder).tv.setText(item.getName());
                 break;

             case ARTIST:
                 Log.i(TAG, "Binding artist item");
                 ((ViewHolderArtist) holder).tv.setText(item.getName());
                 break;

             case PLAY_ALL:
                 break;
         }
    }


    /*--------------------------------------------------------------------------------------------*/
    /*----------------------------------- Home View Holder ---------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    public class ViewHolderHome extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        TextView tv;

        public ViewHolderHome(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.home_text);
            cv = (CardView) itemView.findViewById(R.id.home_cv);
        }

        public void setListeners(){
            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Clicked button");
            ArrayList<MenuItem> newItems = ((MainActivity) parent).db.getAllArtists();
            Log.i(TAG, "Got artists");
            updateItems(newItems);
        }
    }


    /*--------------------------------------------------------------------------------------------*/
    /*---------------------------------- Artist View Holder --------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    public class ViewHolderArtist extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        TextView tv;
        TextView songsCnt;
        TextView albumsCnt;
        TextView artistCharts;

        public ViewHolderArtist(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.artist_text);
            cv = (CardView) itemView.findViewById(R.id.artist_cv);
            songsCnt = (TextView) itemView.findViewById(R.id.songs_count);
            albumsCnt = (TextView) itemView.findViewById(R.id.albums_count);
            artistCharts = (TextView) itemView.findViewById(R.id.artist_charts);
        }

        public void setListeners(){
            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ArrayList<MenuItem> newItems = new ArrayList<>();
            newItems.add(items.get(getAdapterPosition()));
            newItems.add(new PlayAllItem());
            songsCnt.setVisibility(View.VISIBLE);
            albumsCnt.setVisibility(View.VISIBLE);
            artistCharts.setVisibility(View.VISIBLE);
            updateItems(newItems);
        }
    }

    public class ViewHolderPlayAll extends RecyclerView.ViewHolder{
        TextView viewAll;
        TextView playShuffle;

        public ViewHolderPlayAll(View itemView) {
            super(itemView);
            this.viewAll = (TextView) itemView.findViewById(R.id.view_tacks);
            this.playShuffle = (TextView) itemView.findViewById(R.id.play_shuffled);
        }
    }
}
