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
import android.widget.ImageView;
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
    public enum ACTIONS {NEXT, BACK}

    public HomeScreenAdapter(Context parent, ArrayList<MenuItem> items) {
        this.parent = parent;
        this.items = new ArrayList<>(items);
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

            case ALBUM:
                return 2;

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

            case 2:
                Log.i(TAG, "Creating view holder for album items");
                holder = new ViewHolderAlbum(inflater.inflate(R.layout.album_item, parent, false));
                ((ViewHolderAlbum) holder).setListeners();
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

             case ALBUM:
                 Log.i(TAG, "Binding album item");
                 ((ViewHolderAlbum) holder).albumName.setText(item.getName());
                 if (((AlbumItem)item).getReleaseYear() != 0){
                     String year = String.valueOf(((AlbumItem)item).getReleaseYear());
                     ((ViewHolderAlbum) holder).releaseYear.setText(year);
                 } else {
                     ((ViewHolderAlbum) holder).releaseYear.setText("");
                 }
                 break;

             case ARTIST:
                 Log.i(TAG, "Binding artist item");
                 ((ViewHolderArtist) holder).tv.setText(item.getName());
                 break;

             case PLAY_ALL:
                 Log.i(TAG, "Binding playall item");
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
            handleClicks(getAdapterPosition(), ACTIONS.NEXT);
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

        public ViewHolderArtist(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.artist_text);
            cv = (CardView) itemView.findViewById(R.id.artist_cv);
            songsCnt = (TextView) itemView.findViewById(R.id.songs_count);
            albumsCnt = (TextView) itemView.findViewById(R.id.albums_count);
        }

        public void setListeners(){
            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ArrayList<MenuItem> newItems;
            int position = getAdapterPosition();
            boolean extended = ((ArtistItem) items.get(position)).extended;
            if (!extended){
                /*
                newItems = new ArrayList<>();
                newItems.add(items.get(getAdapterPosition()));
                newItems.add(new PlayAllItem());*/
                songsCnt.setVisibility(View.VISIBLE);
                albumsCnt.setVisibility(View.VISIBLE);
                ((ArtistItem) items.get(position)).extended= true;
                tv.setSelected(true);
                handleClicks(position, ACTIONS.NEXT);
            }
            else{
                /*
                newItems = ((MainActivity) parent).db.getAllArtists();
                MenuItem currItem = items.get(getAdapterPosition());
                for (int i = 0; i < newItems.size(); i++){
                    if (currItem.getType() == newItems.get(i).getType() &&
                            currItem.getName().equals(newItems.get(i).getName())){
                        newItems.set(i, currItem);
                        break;
                    }
                }*/
                songsCnt.setVisibility(View.GONE);
                albumsCnt.setVisibility(View.GONE);
                ((ArtistItem) items.get(position)).extended = false;
                tv.setSelected(false);
                handleClicks(position, ACTIONS.BACK);

            }
        }
    }


    /*--------------------------------------------------------------------------------------------*/
    /*----------------------------------- Album View Holder --------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    public class ViewHolderAlbum extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView albumName;
        TextView songsNumber;
        TextView releaseYear;
        ImageView cover;
        CardView cv;

        public ViewHolderAlbum(View itemView) {
            super(itemView);
            this.albumName = (TextView) itemView.findViewById(R.id.album_name);
            this.songsNumber = (TextView) itemView.findViewById(R.id.songs_count);
            this.releaseYear = (TextView) itemView.findViewById(R.id.release_year);
            this.cover = (ImageView) itemView.findViewById(R.id.album_image);
            this.cv = (CardView) itemView.findViewById(R.id.album_cv);
        }

        public void setListeners(){
            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }


    /*--------------------------------------------------------------------------------------------*/
    /*---------------------------------- Play All View Holder ------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    public class ViewHolderPlayAll extends RecyclerView.ViewHolder{
        TextView viewAll;
        TextView playShuffle;

        public ViewHolderPlayAll(View itemView) {
            super(itemView);
            this.viewAll = (TextView) itemView.findViewById(R.id.view_tacks);
            this.playShuffle = (TextView) itemView.findViewById(R.id.play_shuffled);
        }
    }




    /*--------------------------------------------------------------------------------------------*/
    /*------------------------------- Change viewed items methods --------------------------------*/
    /*--------------------------------------------------------------------------------------------*/

    public void handleClicks(int viewPosition, ACTIONS action){
        MainActivity.STATES currState = ((MainActivity) parent).getState();
        ArrayList<MenuItem> newItems;
        switch (currState){
            case HOME:
                if (action == ACTIONS.NEXT){
                    switch (viewPosition){
                        case 0:                     //All artists
                            ((MainActivity) parent).changeStateNext(MainActivity.STATES.ARTISTS);
                            newItems = ((MainActivity) parent).db.getAllArtists();
                            updateItems(newItems);
                            break;

                        case 1:                     //All albums
                            ((MainActivity) parent).changeStateNext(MainActivity.STATES.ALBUMS);
                            newItems = ((MainActivity) parent).db.getAllAlbums();
                            Log.i(TAG, "Got albums");
                            for(MenuItem item : newItems){
                                Log.i(TAG, item.getName() + ": " + String.valueOf(((AlbumItem) item).getReleaseYear()));
                            }
                            updateItems(newItems);
                            break;
                    }
                }

                break;

            case ARTISTS:
                if (action == ACTIONS.BACK) {
                    ((MainActivity) parent).changeStateBack();
                    newItems = ((MainActivity) parent).getHomeItems();
                    updateItems(newItems);
                } else {
                    newItems = new ArrayList<>();
                    ((MainActivity) parent).changeStateNext(MainActivity.STATES.ALBUMS);
                    MenuItem item = items.get(viewPosition);
                    newItems.add(item);
                    newItems.add(new PlayAllItem());
                    newItems.addAll(((MainActivity) parent).db.getAlbumsOfArtist((ArtistItem) item));
                    updateItems(newItems);
                }
                break;

            case ALBUMS:
                if (action == ACTIONS.BACK){
                    ((MainActivity) parent).changeStateBack();
                    MainActivity.STATES newState = ((MainActivity) parent).getState();
                            Log.i(TAG, "Clicked back on albums");

                    switch (newState){
                        case HOME:
                            newItems = ((MainActivity) parent).getHomeItems();
                            updateItems(newItems);
                            break;

                        case ARTISTS:
                            MenuItem currItem = items.get(viewPosition);
                            newItems = ((MainActivity) parent).db.getAllArtists();
                            for (int i = 0; i < newItems.size(); i++){
                                if (currItem.getType() == newItems.get(i).getType() &&
                                        currItem.getName().equals(newItems.get(i).getName())){
                                    newItems.set(i, currItem);
                                    break;
                                }
                            }
                            updateItems(newItems);
                            break;

                        default:
                            ((MainActivity) parent).changeStateNext(MainActivity.STATES.ALBUMS);
                            break;
                    }
                }
                break;

            case TRACKS:
                break;

            case PLAYLISTS:
                break;

            case CHARTS:
                break;
        }
    }
}
