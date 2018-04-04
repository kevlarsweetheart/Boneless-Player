package hellhound.humbleplayer;

import android.support.v7.util.DiffUtil;
import java.util.ArrayList;

public class DiffCallback extends DiffUtil.Callback {

    private ArrayList<MenuItem> newList;
    private ArrayList<MenuItem> oldList;

    public DiffCallback(ArrayList<MenuItem> newList, ArrayList<MenuItem> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).getItemId() == oldList.get(oldItemPosition).getItemId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).equals(oldList.get(oldItemPosition));
    }
}
