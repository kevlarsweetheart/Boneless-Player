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
import android.widget.RelativeLayout;
import android.widget.TextView;

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


    private void onItemsUpdate(ArrayList<MenuItem> newList){
        /*
        Log.i(TAG, "onItemsUpdate");
        DiffCallback diff = new DiffCallback(this.items, newList);
        DiffUtil.DiffResult res = DiffUtil.calculateDiff(diff, true);
        Log.i(TAG, "Obtained diff results");
        this.items.clear();
        this.items.addAll(newList);
        res.dispatchUpdatesTo(this);
        Log.i(TAG, "Updates dispatched");
        */
        this.items.clear();
        this.items.addAll(newList);
        notifyDataSetChanged();
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
                return holder;

            case 2:
                v = inflater.inflate(R.layout.artist_list_item, parent, false);
                holder = new ViewHolderArtist(v);
                return holder;

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        MenuItem item = items.get(position);
        switch (viewType){
            case 0:
                ((ViewHolderHome) holder).textView.setText(item.getName());
                ((ViewHolderHome) holder).imageView.setImageResource(((HomeScreenItem) item).getResourceId());
                ((ViewHolderHome) holder).setListeners();
                break;

            case 2:
                ((ViewHolderArtist) holder).textView.setText(item.getName());
                ((ViewHolderArtist) holder).setListeners();
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

        void setListeners(){
            cardView.setOnClickListener(ViewHolderHome.this);
        }

        @Override
        public void onClick(View view) {
            String name = items.get(getAdapterPosition()).getName();
            if (name.equals("Artists")){
                onItemsUpdate(db.getAllArtists());
            }
        }
    }

    /*---------------------------- Artists View Holder -------------------------------------------*/
    public class ViewHolderArtist extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardView;
        TextView textView;
        RelativeLayout topLayout;
        RelativeLayout bottomLayout;

        ViewHolderArtist(View view){
            super(view);
            cardView = (CardView) view.findViewById(R.id.cv);
            textView = (TextView) view.findViewById(R.id.cv_text);
            topLayout = (RelativeLayout) view.findViewById(R.id.top_layout);
            bottomLayout = (RelativeLayout) view.findViewById(R.id.bottom_layout);
        }

        void setListeners(){
            topLayout.setOnClickListener(ViewHolderArtist.this);
        }

        @Override
        public void onClick(View view) {
            int visibility = bottomLayout.getVisibility();
            Log.i(TAG, String.valueOf(visibility));

            switch (visibility){
                case View.VISIBLE:
                    bottomLayout.setVisibility(View.GONE);
                    break;

                case View.GONE:
                    bottomLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }


}
