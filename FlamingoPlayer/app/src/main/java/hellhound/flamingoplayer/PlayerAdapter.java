package hellhound.flamingoplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "adapter_debug";
    private ArrayList<TrackItem> items;
    private Context parent;

    public PlayerAdapter(Context parent, ArrayList<TrackItem> items) {
        this.items = new ArrayList<>();
        this.items.addAll(items);
        this.parent = parent;
    }

    public void setItems(ArrayList<TrackItem> items) {
        this.items = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder = new ViewHolderCover(inflater.inflate(R.layout.cover_displayed, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, items.get(position).getCoverPath());
        GlideApp.with(parent).load(items.get(position).coverPath).placeholder(R.drawable.default_release)
                .into(((ViewHolderCover) holder).cover);
    }

    public class ViewHolderCover extends RecyclerView.ViewHolder{
        ImageView cover;

        public ViewHolderCover(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.imageView2);
        }
    }
}
