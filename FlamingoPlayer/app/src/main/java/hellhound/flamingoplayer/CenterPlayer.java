package hellhound.flamingoplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CenterPlayer extends Fragment implements SeekBar.OnSeekBarChangeListener{

    View background;
    SeekBar seekBar;
    TextView currTime;
    TextView maxTime;
    boolean seekBarTouched = false;
    CenterPlayerListener activityCommander;
    private static final String TAG = "main_activity";


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
        new setProgress().execute(new DumbClass(currTime, progress), new DumbClass(maxTime, max));
        if(!seekBarTouched){
            seekBar.setMax(max);
            seekBar.setProgress(progress);
        }
    }


    private static class setProgress extends AsyncTask<DumbClass, Void, DumbClass[]> {
        @Override
        protected void onPostExecute(DumbClass[] things) {
            things[0].tv.setText(things[0].s);
            things[1].tv.setText(things[1].s);
        }

        @Override
        protected DumbClass[] doInBackground(DumbClass... things) {
            things[0].s = String.format(Locale.getDefault(), "%d:%02d", TimeUnit.MILLISECONDS.toMinutes(things[0].i),
                    TimeUnit.MILLISECONDS.toSeconds(things[0].i) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(things[0].i)));

            things[1].s = String.format(Locale.getDefault(), "%d:%02d", TimeUnit.MILLISECONDS.toMinutes(things[1].i),
                    TimeUnit.MILLISECONDS.toSeconds(things[1].i) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(things[1].i)));
            return things;
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

    private class DumbClass{
        public TextView tv;
        public int i;
        public String s;

        public DumbClass(TextView tv, int i) {
            this.tv = tv;
            this.i = i;
        }
    }

}
