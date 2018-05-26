package hellhound.flamingoplayer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


public class PlayControls extends Fragment {

    ImageView nextButton, prevButton, playButton, arrowl, arrowr;
    TextView trackView, artistAlbumView;
    ConstraintLayout textArea;
    public enum CONTROLS {PLAY, NEXT, PREV}
    PlayControlsListener activityCommander;
    public final static int UP = 0;
    public final static int DOWN = 1;


    public interface PlayControlsListener{
        void controlButtonClicked(CONTROLS action);
        boolean openClosePlayer();
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
        textArea = view.findViewById(R.id.text_area);
        arrowl = view.findViewById(R.id.arrowl);
        arrowr = view.findViewById(R.id.arrowr);

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
        textArea.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityCommander.openClosePlayer();
                    }
                }
        );
        return view;
    }

    public void setTrack(TrackItem track){
        trackView.setText(track.getName());
        String albumArtist = String.format("%1$s - %2$s", track.getArtistName(), track.getAlbumName());
        artistAlbumView.setText(albumArtist);
    }

    public void setPlayButton(boolean isPlaying){
        Context parent = getActivity();
        if(parent != null){
            if(isPlaying){
                GlideApp.with(parent).load(R.mipmap.pause_button).into(playButton);
            } else {
                GlideApp.with(parent).load(R.mipmap.play_button).into(playButton);
            }
        }
    }

    public void rotateArrows(int where){
        if(where == UP){
            arrowl.animate().rotation(90).setInterpolator(new LinearOutSlowInInterpolator()).start();
            arrowr.animate().rotation(90).setInterpolator(new LinearOutSlowInInterpolator()).start();
        } else {
            arrowl.animate().rotation(-90).setInterpolator(new LinearOutSlowInInterpolator()).start();
            arrowr.animate().rotation(270).setInterpolator(new LinearOutSlowInInterpolator()).start();
        }
    }
}
