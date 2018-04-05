package hellhound.humbleplayer;

import android.animation.ArgbEvaluator;
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
import android.widget.ArrayAdapter;
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
        Log.i(TAG, "GOING TO CREATE " + String.valueOf(items.size()) + " CARDS");
    }

    @Override
    public int getItemCount() {
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

    public void onItemsUpdate(ArrayList<MenuItem> oldList, ArrayList<MenuItem> newList){
        DiffCallback diff = new DiffCallback(oldList, newList);
        DiffUtil.DiffResult res = DiffUtil.calculateDiff(diff, true);
        this.items = newList;
        res.dispatchUpdatesTo(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

    public void updateView(ArrayList<MenuItem> newItems){
        Log.i(TAG, "I'm in!");
        Toast.makeText(parent, newItems.get(0).getName(), Toast.LENGTH_SHORT);
        DiffCallback callback = new DiffCallback(items, newItems);
        DiffUtil.DiffResult res = DiffUtil.calculateDiff(callback, true);
        this.items = newItems;
        res.dispatchUpdatesTo(this);
        Log.i(TAG, "Dispatched");
    }

    /*---------------------------- Artists View Holder -------------------------------------------*/
    public class ViewHolderHome extends RecyclerView.ViewHolder implements View.OnTouchListener{
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
            cardView.setOnTouchListener(ViewHolderHome.this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            String name = items.get(getAdapterPosition()).getName();

            ValueAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 0.9f, 0.5f);
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //cardView.setAlpha(0.5f);
                    anim.setDuration(50);
                    anim.setEvaluator(new FloatEvaluator());
                    anim.start();
                    return true;

                case MotionEvent.ACTION_UP:
                    cardView.setAlpha(0.9f);
                    if (name.equals("Artists")) {
                        Log.i(TAG, "Going to update");
                        updateView(db.getAllArtists());
                        Log.i(TAG, "Updated!");
                    }
                    return true;
            }
            return false;
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
