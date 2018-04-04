package hellhound.humbleplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeScreenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "Debugging";
    private Context parent;
    private ArrayList<MenuItem> items;

    public HomeScreenAdapter(@NonNull Context parent, ArrayList<MenuItem> items){
        this.items = items;
        this.parent = parent;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG,"onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder;
        Log.i(TAG,"ViewType = " + String.valueOf(viewType));
        switch (viewType){
            case 0:
                View v = inflater.inflate(R.layout.home_list_item, parent, false);
                holder = new ViewHolderHome(v);
                Log.i(TAG, "Returning holder");
                return holder;

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        Log.i(TAG,"Binding, viewType = " + String.valueOf(viewType));
        switch (viewType){
            case 0:
                MenuItem item = items.get(position);
                ((ViewHolderHome) holder).textView.setText(item.getName());
                ((ViewHolderHome) holder).imageView.setImageResource(((HomeScreenItem) item).getResourseId());
                Log.i(TAG, "Done binding " + String.valueOf(position) + '\n' + '\n');
                break;
        }
    }

    public static class ViewHolderHome extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;

        ViewHolderHome(View itemView){
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.cv_text);
            imageView = (ImageView) itemView.findViewById(R.id.cv_background);
        }
    }


}
