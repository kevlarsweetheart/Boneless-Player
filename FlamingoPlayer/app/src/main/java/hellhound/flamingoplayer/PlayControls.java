package hellhound.flamingoplayer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class PlayControls extends Fragment {

    ImageView nextButton, prevButton, playButton;
    TextView trackView, artistAlbumView;
    public enum CONTROLS {PLAY, NEXT, PREV}
    PlayControlsListener activityCommander;

    public interface PlayControlsListener{
        void controlButtonClicked(CONTROLS action);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity){
            activity = (Activity) context;
            try{
                activityCommander = (PlayControlsListener) activity;
            }catch (ClassCastException e){
                throw new ClassCastException(activity.toString());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_control_panel, container, false);

        nextButton = view.findViewById(R.id.next_button);
        prevButton = view.findViewById(R.id.prev_button);
        playButton = view.findViewById(R.id.play_button);

        trackView = view.findViewById(R.id.track);
        artistAlbumView = view.findViewById(R.id.artist_album);

        nextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityCommander.controlButtonClicked(CONTROLS.NEXT);
                    }
                }
        );
        prevButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityCommander.controlButtonClicked(CONTROLS.PREV);
                    }
                }
        );
        playButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityCommander.controlButtonClicked(CONTROLS.PLAY);
                    }
                }
        );
        return view;
    }

    public void setTrack(TrackItem track){
        trackView.setText(track.getName());
        String albumArtist = String.format("%1$s - %2$s", track.getArtistName(), track.getAlbumName());
        trackView.setText(albumArtist);
    }
}
