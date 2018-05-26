package hellhound.flamingoplayer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Trace;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class TopHeader extends Fragment implements PopupMenu.OnMenuItemClickListener{

    private static final String TAG = "main_activity";
    ImageView backButton;
    TextView topText;
    View progressSquare;
    View background;
    ImageView settingsButton;
    ImageView scrobbling;
    public static final int IDLE = 0;
    public static final int SCROBBLED = 1;
    public static final int LASTFM = 2;
    public static final int ERROR = 3;

    TopHeaderListener activityCommander;

    public interface TopHeaderListener{
        void backButtonClicked();
        void setLastfm();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        background = inflater.inflate(R.layout.top_header, container, false);
        backButton = (ImageView) background.findViewById(R.id.back_button);
        topText = (TextView) background.findViewById(R.id.top_text);
        progressSquare = (View) background.findViewById(R.id.progress_square);
        settingsButton = (ImageView) background.findViewById(R.id.settings_button);
        scrobbling = (ImageView) background.findViewById(R.id.scrobbling);

        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityCommander.backButtonClicked();
                    }
                }
        );

        settingsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), v);
                        popupMenu.setOnMenuItemClickListener(TopHeader.this);
                        popupMenu.inflate(R.menu.settings_menu);
                        popupMenu.show();
                    }
                }
        );

        return background;
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

    public void setProgressSquare(int progress, int max){
        int maxWidth = background.getWidth();
        int viewProgress = progress * maxWidth / max;

        viewProgress = viewProgress < 10 ? 10 : viewProgress;
        ResizeWidthAnimation anim = new ResizeWidthAnimation(progressSquare, viewProgress);
        anim.setDuration(500);
        progressSquare.startAnimation(anim);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.lastfm_item:
                activityCommander.setLastfm();
                break;

            case R.id.reftresh_button:
                break;
        }
        return true;
    }

    public void changeScrobbing(int state){
        if(state == IDLE) {
            scrobbling.setImageResource(android.R.color.transparent);
        }
        if(state == LASTFM) {
            GlideApp.with(this).load(R.drawable.lasr_ico).transition(DrawableTransitionOptions.withCrossFade())
                    .into(scrobbling);
        }
        if ((state == SCROBBLED) || (state == ERROR)) {
            final Fragment f = this;
            scrobbling.animate().withEndAction(new Runnable() {
                @Override
                public void run() {
                    scrobbling.animate().setDuration(100).translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                }
            }).translationY(-10).setInterpolator(new OvershootInterpolator()).setDuration(700).start();
            if(state == SCROBBLED){
                GlideApp.with(f).load(R.drawable.check_ico).into(scrobbling);

            } else {
                GlideApp.with(f).load(R.drawable.error_ico).into(scrobbling);
            }
            final Handler handler = new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long futureTime = System.currentTimeMillis() + 1000;
                    while (System.currentTimeMillis() < futureTime){
                        synchronized (this){
                            try {
                                wait(futureTime - System.currentTimeMillis());
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            GlideApp.with(f).load(R.drawable.lasr_ico).transition(DrawableTransitionOptions.withCrossFade())
                                    .into(scrobbling);
                        }
                    });

                }
            }).start();
        }
    }
}
