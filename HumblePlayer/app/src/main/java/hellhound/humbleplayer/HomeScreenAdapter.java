package hellhound.humbleplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeScreenAdapter extends ArrayAdapter<HomeScreenItem> {

    HomeScreenAdapter(@NonNull Context context, ArrayList<HomeScreenItem> content) {
        super(context, R.layout.home_list_item, content);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        HomeScreenItem item = getItem(position);

        View view;
        switch (item.getType()){
            case HSI:
                view = inflater.inflate(R.layout.home_list_item, parent, false);
                break;

            default:
                view = inflater.inflate(R.layout.device_path_item, parent, false);
                break;
        }

        TextView text = (TextView) view.findViewById(R.id.list_text);
        ImageView image = (ImageView) view.findViewById(R.id.list_pic);

        text.setText(item.getText());
        image.setImageResource(item.getImage());
        return view;
    }
}
