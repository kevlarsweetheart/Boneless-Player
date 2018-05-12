package hellhound.flamingoplayer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Trace;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TopHeader extends Fragment {

    ImageView backButton;
    TextView topText;

    TopHeaderListener activityCommander;

    public interface TopHeaderListener{
        void backButtonClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity){
            activity = (Activity) context;
            try{
                activityCommander = (TopHeaderListener) activity;
            }catch (ClassCastException e){
                throw new ClassCastException(activity.toString());
            }
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_header, container, false);
        backButton = (ImageView) view.findViewById(R.id.back_button);
        topText = (TextView) view.findViewById(R.id.top_text);

        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityCommander.backButtonClicked();
                    }
                }
        );

        return view;
    }

    public boolean switchBackButton(MainActivity.STATES state){
        if (state != MainActivity.STATES.HOME){
            backButton.setVisibility(View.VISIBLE);
            return true;
        } else {
            backButton.setVisibility(View.GONE);
            return false;
        }

    }

    public void changeTitleText(MainActivity.STATES state){
        switch (state){
            case HOME:
                topText.setText(R.string.app_name);
                break;

            case ARTISTS:
                topText.setText("Artists");
                break;

            case ALBUMS:
                topText.setText("Albums");
                break;

            case TRACKS:
                topText.setText("Tracks");
                break;

            case PLAYLISTS:
                topText.setText("Playlists");
                break;

            case CHARTS:
                topText.setText("Charts");
        }
    }

}
