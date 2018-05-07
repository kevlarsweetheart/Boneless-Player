package hellhound.flamingoplayer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.widget.Toast;

import org.json.JSONObject;

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

            case TRACK:
                return 4;

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

            case 4:
                Log.i(TAG, "Creating view holder for track items");
                holder = new ViewHolderTrack(inflater.inflate(R.layout.track_item, parent, false));
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
        Resources res = ((MainActivity) parent).getResources();
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
                 String cover = ((MainActivity) parent).db.getCoverById(((AlbumItem) item).getCoverId());
                 Log.i(TAG, cover);
                 GlideApp.with(parent)
                         .load(cover)
                         .placeholder(R.mipmap.default_album)
                         .into(((ViewHolderAlbum) holder).cover);
                 String _tracksCnt = String.format(Locale.getDefault(), res.getString(R.string.songs_cnt),
                         ((MainActivity) parent).db.getTracksCount((AlbumItem) item));
                 Log.i(TAG, _tracksCnt);
                 ((ViewHolderAlbum) holder).songsNumber.setText(_tracksCnt);
                 break;

             case ARTIST:
                 Log.i(TAG, "Binding artist item");
                 ((ViewHolderArtist) holder).tv.setText(item.getName());
                 String albumsCnt = String.format(res.getString(R.string.albums_cnt),
                         ((ArtistItem) item).getAlbumsCnt());
                 ((ViewHolderArtist) holder).albumsCnt.setText(albumsCnt);
                 String tracksCnt = String.format(Locale.getDefault(), res.getString(R.string.songs_cnt),
                         ((MainActivity) parent).db.getTracksCount((ArtistItem) item));
                 ((ViewHolderArtist) holder).songsCnt.setText(tracksCnt);
                 break;

             case TRACK:
                 Log.i(TAG, "Binding track item");
                 ((ViewHolderTrack) holder).trackName.setText(item.getName());
                 String number = String.valueOf(((TrackItem) item).getTrackNumber());
                 ((ViewHolderTrack) holder).trackNumber.setText(number);
                 long _length = ((TrackItem) item).getLength();
                 String length = String.format(Locale.getDefault(), "%d:%02d", TimeUnit.MILLISECONDS.toMinutes(_length),
                         TimeUnit.MILLISECONDS.toSeconds(_length) -
                                 TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(_length)));
                 ((ViewHolderTrack) holder).lengthTV.setText(length);
                 break;

             case PLAY_ALL:
                 Log.i(TAG, "Binding playall item");
                 if(((PlayAllItem) item).isPlayAllVisible()){
                     ((ViewHolderPlayAll) holder).viewAll.setVisibility(View.VISIBLE);
                 } else {
                     ((ViewHolderPlayAll) holder).viewAll.setVisibility(View.GONE);
                 }
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
                songsCnt.setVisibility(View.VISIBLE);
                albumsCnt.setVisibility(View.VISIBLE);
                ((ArtistItem) items.get(position)).extended= true;
                tv.setSelected(true);
                handleClicks(position, ACTIONS.NEXT);
            }
            else{
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
    public class ViewHolderAlbum extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{
        TextView albumName;
        TextView songsNumber;
        TextView releaseYear;
        ImageView cover;
        ImageButton more;
        CardView cv;
        DownloadArt task;

        public ViewHolderAlbum(View itemView) {
            super(itemView);
            this.albumName = (TextView) itemView.findViewById(R.id.album_name);
            this.songsNumber = (TextView) itemView.findViewById(R.id.num_tracks);
            this.releaseYear = (TextView) itemView.findViewById(R.id.release_year);
            this.cover = (ImageView) itemView.findViewById(R.id.album_image);
            this.cv = (CardView) itemView.findViewById(R.id.album_cv);
        }

        public void setListeners(){
            cv.setOnClickListener(this);
            cv.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Clicked on album");
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select action");
            android.view.MenuItem fetch = menu.add(this.getAdapterPosition(), 0, 0, "Fetch album art");
            android.view.MenuItem cake = menu.add(this.getAdapterPosition(), 1, 0, "Get a cake");
            fetch.setOnMenuItemClickListener(listener);
            cake.setOnMenuItemClickListener(listener);
        }

        private final android.view.MenuItem.OnMenuItemClickListener listener = new android.view.MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                Toast toast;
                switch (item.getItemId()){
                    case 0:
                        toast = Toast.makeText(parent, "Downloading album art from web", Toast.LENGTH_SHORT);
                        toast.show();
                        task = new DownloadArt();
                        task.execute((AlbumItem) items.get(getAdapterPosition()));
                        break;

                    case 1:
                        toast = Toast.makeText(parent, "Here is your cake, enjoy!", Toast.LENGTH_LONG);
                        toast.show();
                        break;
                }
                return true;
            }
        };

        class DownloadArt extends AsyncTask<AlbumItem, Void, String>{
            @Override
            protected String doInBackground(AlbumItem... albums) {
                AlbumItem album = albums[0];
                ArtistItem artist = ((MainActivity) parent).db.getArtistBy(album);
                String url = downloadAndSaveCover(artist, album);
                if(url != null){
                    return url;
                } else {
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                GlideApp.with(parent).load(result)
                        .placeholder(R.mipmap.default_album)
                        .into(cover);

            }

            private String downloadAndSaveCover(ArtistItem artist, AlbumItem album){
                String albumName = album.getName();
                String artistName = artist.getName();
                String url = "https://www.googleapis.com/customsearch/v1?key=AIzaSyC-FAI2A9BeOSIdpKral0LO3Z2lqZoStHk&cx=018060032051945042082:ooluxsoos3m&num=1&q=";
                url += artistName.replaceAll(" ", "+") + "+" + albumName.replaceAll(" ", "+");
                try {
                    URL obj = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                    connection.setRequestMethod("GET");
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    JSONObject json = new JSONObject(response.toString());
                    String res = json.getJSONArray("items").getJSONObject(0)
                            .getJSONObject("pagemap").getJSONArray("cse_image")
                            .getJSONObject(0).getString("src");
                    connection.disconnect();

                    // Saving file to DCIM/Flamingo Player
                    connection = (HttpURLConnection) new URL(res).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bmp = BitmapFactory.decodeStream(input);

                    String coverName = albumName + "_" + artistName;
                    coverName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" +
                            ((MainActivity)parent).getResources().getString(R.string.app_name) + "/" + coverName + ".png";
                    File outFile = new File(coverName);
                    FileOutputStream output = new FileOutputStream(outFile, false);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, output);

                    if(album.getCoverId() == -1){
                        long coverId = ((MainActivity) parent).db.addCover(coverName);
                        ((MainActivity) parent).db.updateAlbumCover(album, coverId);
                    }
                    Log.i(TAG, res);
                    return res;
                } catch (Exception e) {
                    Log.i(TAG, e.getMessage());
                    return null;
                }
            }
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
    /*----------------------------------- Track View Holder --------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    public class ViewHolderTrack extends RecyclerView.ViewHolder{
        TextView trackNumber;
        TextView trackName;
        TextView lengthTV;

        public ViewHolderTrack(View itemView){
            super(itemView);
            trackNumber = (TextView) itemView.findViewById(R.id.track_number);
            trackName = (TextView) itemView.findViewById(R.id.track_name);
            lengthTV = (TextView) itemView.findViewById(R.id.length);
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
                            for(MenuItem item : newItems){
                                ((ArtistItem) item).setAlbumsCnt(((MainActivity) parent).db.getAlbumsCount((ArtistItem) item));
                            }
                            updateItems(newItems);
                            break;

                        case 1:                     //All albums
                            ((MainActivity) parent).changeStateNext(MainActivity.STATES.ALBUMS);
                            Log.i(TAG, "Fetching albums");
                            newItems = ((MainActivity) parent).db.getAllAlbums();
                            Log.i(TAG, "Got albums");
                            for(MenuItem item : newItems){
                                Log.i(TAG, item.getName() + ": " + String.valueOf(((AlbumItem) item).getReleaseYear()));
                            }
                            updateItems(newItems);
                            break;

                        case 2:
                            ((MainActivity) parent).changeStateNext(MainActivity.STATES.TRACKS);
                            newItems = ((MainActivity) parent).db.getTracksOf();
                            for(MenuItem item : newItems){
                                Log.i(TAG, item.getName() + ": " + String.valueOf(((TrackItem) item).getLength()));
                            }
                            updateItems(newItems);
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
                            for(MenuItem item : newItems){
                                ((ArtistItem) item).setAlbumsCnt(((MainActivity) parent).db.getAlbumsCount((ArtistItem) item));
                            }
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
