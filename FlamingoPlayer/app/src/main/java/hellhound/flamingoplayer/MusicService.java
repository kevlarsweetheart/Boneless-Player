package hellhound.flamingoplayer;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;

import java.io.File;
import java.util.ArrayList;

public class MusicService extends Service {

    private static final String TAG = "ExoDebug";

    private final IBinder binder = new LocalBinder();
    private SimpleExoPlayer player;
    public boolean isPlaying = false;
    int playlistLen = 0;
    DynamicConcatenatingMediaSource source;

    Handler handler;
    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Binding");
        return binder;
    }

    @Override
    public void onDestroy() {
        player.stop();
        super.onDestroy();
    }

    public class LocalBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    public void initPlayer(){
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        player.addListener(eventListener);
        source = new DynamicConcatenatingMediaSource();
        handler = new Handler();
        Log.i(TAG, "Player inited");
    }

    /*--------------------------------------------------------------------------------------------*/
    /*------------------------------- Internal methods for playback ------------------------------*/
    /*--------------------------------------------------------------------------------------------*/

    private Player.EventListener eventListener = new Player.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            Log.i(TAG, "onTimelineChanged");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.i(TAG, "onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.i(TAG, "onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            updateProgress();
            sendTrackNumber();
            Log.i(TAG,"onPlayerStateChanged: playWhenReady = " + String.valueOf(playWhenReady)
                    +" playbackState = " + playbackState);

            switch (playbackState){
                case Player.STATE_ENDED:
                    Log.i(TAG,"Playback ended!");
                    setPlayPause(false);
                    player.seekTo(0);

                case Player.STATE_READY:
                    Log.i(TAG,"ExoPlayer ready! pos: " + player.getCurrentPosition());
                    break;

                case Player.STATE_IDLE:
                    Log.i(TAG,"ExoPlayer idle!");
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            Log.i(TAG, "onRepeatModeChanged");
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            Log.i(TAG, "onShuffleModeEnabledChanged");
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.i(TAG, "onPlayerError");
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            Log.i(TAG, "onPositionDiscontinuity");
            sendTrackNumber();
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            Log.i(TAG,"onPlaybackParametersChanged");
        }

        @Override
        public void onSeekProcessed() {
            Log.i(TAG, "onSeekProcessed");
        }
    };


    private void setPlayPause(boolean play){
        isPlaying = play;
        player.setPlayWhenReady(play);
        if(!isPlaying){
            Log.i(TAG, "Play");
        }else{
            Log.i(TAG, "Y");
        }
    }

    private void updateProgress(){
        int duration = player == null ? 0 : (int)player.getDuration();
        int position = player == null ? 0 : (int)player.getCurrentPosition();
        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
        try {
            intent.putExtra(MainActivity.PARAM_TASK, MainActivity.TASK_PROGRESS);
            intent.putExtra(MainActivity.PARAM_PROGRESS, position);
            intent.putExtra(MainActivity.PARAM_MAX, duration);
            sendBroadcast(intent);
        } catch (Exception e){
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        }

        handler.removeCallbacks(updateProgressAction);
        int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
            handler.postDelayed(updateProgressAction, delayMs);
        }
    }

    private void sendTrackNumber(){
        int num = player == null ? 0 : player.getCurrentWindowIndex();
        Log.i(TAG, "New window index: " + String.valueOf(num));
        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
        try {
            intent.putExtra(MainActivity.PARAM_TASK, MainActivity.TASK_INFO);
            intent.putExtra(MainActivity.PARAM_TRACK_NUM, num);
            sendBroadcast(intent);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    /*--------------------------------- Public methods for playback ------------------------------*/
    /*--------------------------------------------------------------------------------------------*/

    public boolean play(){
        setPlayPause(!isPlaying);
        return isPlaying;
    }

    public boolean play(boolean doPlay){
        setPlayPause(doPlay);
        return doPlay;
    }

    public int nextTrack(){
        long position = player.getCurrentWindowIndex();
        Log.i(TAG, "Current position: " + String.valueOf(position));
        if(position < playlistLen - 1){
            player.seekTo((int) position + 1, 0);
            return (int)position + 1;
        } else {
            return (int)position;
        }
    }

    public int prevTrack(boolean force){
        long ms = player.getContentPosition();
        int pos = player.getCurrentWindowIndex();
        Log.i(TAG, String.valueOf(ms) + "ms");
        if(ms > 3000 && !force){
            player.seekTo(0);
            return pos;
        } else {
            Log.i(TAG, "Current position: " + String.valueOf(pos));
            if(pos > 0){
                player.seekTo(pos - 1, 0);
                return pos - 1;
            } else {
                return 0;
            }
        }
    }

    public void setProgress(int progress){
        if (player != null){
            Log.i(TAG, "New progress: " + String.valueOf(progress));
            player.seekTo((long) progress);
        }
    }

    public void prepareTracks(ArrayList<String> urls){
        player.stop();
        ArrayList<MediaSource> audioSources = new ArrayList<>();
        for (String url : urls){
            Log.i(TAG, url);
            Uri uri = Uri.fromFile(new File(url));
            Log.i(TAG, uri.getPath());
            DataSpec dataSpec = new DataSpec(uri);
            final FileDataSource ds = new FileDataSource();
            try {
                ds.open(dataSpec);
            } catch (FileDataSource.FileDataSourceException e){
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            }
            DataSource.Factory factory = new DataSource.Factory() {
                @Override
                public DataSource createDataSource() {
                    return ds;
                }
            };
            @SuppressWarnings("deprecation")
            MediaSource source = new ExtractorMediaSource(ds.getUri(), factory,
                    new DefaultExtractorsFactory(), null, null);
            audioSources.add(source);
        }


        source = new DynamicConcatenatingMediaSource();
        source.addMediaSources(audioSources);
        playlistLen = source.getSize();
        player.prepare(source);
    }

    public void seekToWindow(int position){
        player.seekTo(position, 0);
    }
}
