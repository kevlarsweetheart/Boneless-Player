package hellhound.humbleplayer;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeScreenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "Debugging";
    private Context parent;
    private ArrayList<MenuItem> items;
    private DatabaseHelper db;

    public HomeScreenAdapter(@NonNull Context parent, ArrayList<MenuItem> items, DatabaseHelper db){
        this.items = items;
        this.parent = parent;
        this.db = db;
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "There are " + String.valueOf(items.size()) + " items");
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (items.get(position).getType()){
            case HOME:
                return 0;

            case PATH:
                return 1;

            case ARTIST:
                return 2;

            case ALBUM:
                return 3;

            case SONG:
                return 4;

            default:
                return 0;
        }
    }


    public void onItemsUpdate(ArrayList<MenuItem> newList){
        Log.i(TAG, "onItemsUpdate");
        DiffCallback diff = new DiffCallback(this.items, newList);
        DiffUtil.DiffResult res = DiffUtil.calculateDiff(diff, true);
        Log.i(TAG, "Obtained diff results");
        this.items.clear();
        this.items.addAll(newList);
        res.dispatchUpdatesTo(this);
        Log.i(TAG, "Updates dispatched");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "Creating " + String.valueOf(viewType));
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder;
        View v;
        switch (viewType){
            case 0:
                v = inflater.inflate(R.layout.home_list_item, parent, false);
                holder = new ViewHolderHome(v);
                Log.i(TAG, "Created Home Item Holder");
                return holder;

            case 2:
                v = inflater.inflate(R.layout.artist_list_item, parent, false);
                holder = new ViewHolderArtist(v);
                Log.i(TAG, "Created Artists Holder");
                return holder;

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        Log.i(TAG,"Binding, viewType = " + String.valueOf(viewType));
        MenuItem item = items.get(position);
        switch (viewType){
            case 0:
                ((ViewHolderHome) holder).textView.setText(item.getName());
                ((ViewHolderHome) holder).imageView.setImageResource(((HomeScreenItem) item).getResourceId());
                ((ViewHolderHome) holder).setListeners();
                Log.i(TAG, "Done binding " + String.valueOf(position) + '\n' + '\n');
                break;

            case 2:
                ((ViewHolderArtist) holder).textView.setText(item.getName());
                Log.i(TAG, "Done binding " + String.valueOf(position) + '\n' + '\n');
                break;
        }
    }

    /*------------------------------- Home View Holder -------------------------------------------*/
    public class ViewHolderHome extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardView;
        TextView textView;
        ImageView imageView;

        ViewHolderHome(View itemView){
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.cv_text);
            imageView = (ImageView) itemView.findViewById(R.id.cv_background);
            cardView = (CardView) itemView.findViewById(R.id.cv);
        }

        public void setListeners(){
            cardView.setOnClickListener(ViewHolderHome.this);
        }

        @Override
        public void onClick(View view) {
            String name = items.get(getAdapterPosition()).getName();
            onItemsUpdate(db.getAllArtists());
        }
    }

    /*---------------------------- Artists View Holder -------------------------------------------*/
    public class ViewHolderArtist extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView textView;

        ViewHolderArtist(View view){
            super(view);
            cardView = (CardView) view.findViewById(R.id.cv);
            textView = (TextView) view.findViewById(R.id.cv_text);
        }
    }


}
