package intech.juced.intechtest.views.player_controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import intech.juced.intechtest.R;
import intech.juced.intechtest.application.IntechApplication;
import intech.juced.intechtest.helpers.AnimationHelper;
import intech.juced.intechtest.service.music.MusicService;

/**
 * Created by juced on 05.06.2016.
 */
public class PlayerControlsView extends FrameLayout {

    private TextView text_title;
    private TextView text_artist;
    private ImageButton btn_previous;
    private ImageButton btn_next;
    private ImageButton btn_playPause;

    private boolean viewIsShowing = false;

    public PlayerControlsView(Context context) {
        super(context);
        initView();
    }

    public PlayerControlsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.player_controls_view, this);
        setVisibility(GONE);
        getViewElems();
        setActions();
    }

    private void getViewElems() {
        text_title = (TextView) findViewById(R.id.text_title);
        text_artist = (TextView) findViewById(R.id.text_artist);
        btn_previous = (ImageButton) findViewById(R.id.btn_previous);
        btn_next = (ImageButton) findViewById(R.id.btn_next);
        btn_playPause = (ImageButton) findViewById(R.id.btn_playPause);
    }

    public void show() {
        if (!viewIsShowing) {
            AnimationHelper.expand(this, 300);
            viewIsShowing = true;
        }
    }

    public void hide() {
        if (viewIsShowing) {
            AnimationHelper.collapse(this, 300);
            viewIsShowing = false;
        }
    }

    public void showAndSetSongParams(String title, String artist) {
        text_title.setText(title);
        text_artist.setText(artist);
        btn_playPause.setImageResource(R.drawable.ic_pause_white_24dp);
        show();
    }

    public void showAndSetPlayIcon() {
        btn_playPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        show();
    }

    public void showAndSetPauseIcon() {
        btn_playPause.setImageResource(R.drawable.ic_pause_white_24dp);
        show();
    }

    public void close() {
        hide();
    }

    private void setActions() {
        btn_playPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
                if (musicService != null) {
                    musicService.togglePlayState();
                }
            }
        });

        btn_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
                if (musicService != null) {
                    musicService.playNext();
                }
            }
        });

        btn_previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicService musicService = IntechApplication.getSingleton().getMusicSrv();
                if (musicService != null) {
                    musicService.playPrev();
                }
            }
        });

        findViewById(R.id.container).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // nothing to do
            }
        });
    }

}
