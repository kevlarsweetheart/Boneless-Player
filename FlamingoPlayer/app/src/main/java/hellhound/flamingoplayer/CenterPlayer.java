package hellhound.flamingoplayer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CenterPlayer extends Fragment implements SeekBar.OnSeekBarChangeListener{

    View background;
    SeekBar seekBar;
    TextView currTime;
    TextView maxTime;
    boolean seekBarTouched = false;
    CenterPlayerListener activityCommander;


    public interface CenterPlayerListener{
        void seekBarChanged(int progress);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity) {
            activity = (Activity) context;
            try {
                activityCommander = (CenterPlayer.CenterPlayerListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString());
            }
        }
    }


        @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        background = inflater.inflate(R.layout.center_player, container, false);
        seekBar = background.findViewById(R.id.seekBar);
        currTime = background.findViewById(R.id.currTime);
        maxTime = background.findViewById(R.id.maxTime);
        seekBar.setOnSeekBarChangeListener(this);
        return background;
    }

    public void setTime(int progress, int max){
        String time = String.format(Locale.getDefault(), "%d:%02d", TimeUnit.MILLISECONDS.toMinutes(progress),
                TimeUnit.MILLISECONDS.toSeconds(progress) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress)));
        currTime.setText(time);
        time = String.format(Locale.getDefault(), "%d:%02d", TimeUnit.MILLISECONDS.toMinutes(max),
                TimeUnit.MILLISECONDS.toSeconds(max) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(max)));
        maxTime.setText(time);
        if(!seekBarTouched){
            seekBar.setMax(max);
            seekBar.setProgress(progress);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        seekBarTouched = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int newPosition = seekBar.getProgress();
        activityCommander.seekBarChanged(newPosition);
        seekBarTouched = false;
    }

}
