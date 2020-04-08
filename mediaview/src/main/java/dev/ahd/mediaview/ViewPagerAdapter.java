package dev.ahd.mediaview;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    public static final String TAG = "viewPagerAdapterItem";

    private List<Media> medias;
    private boolean hasVideoController;
    private List<PlayData> playDataList = new ArrayList<>();

    private int prevPosition = -1;

    private PlayerView currentVideo;
    private int videoID = 123456789;

    public ViewPagerAdapter(List<Media> medias, boolean hasVideoController) {
        this.medias = medias;
        this.hasVideoController = hasVideoController;
    }

    public void addOrUpdatePlayData() {
        PlayData playData = new PlayData(prevPosition, currentVideo.getPlayer().getCurrentWindowIndex(), currentVideo.getPlayer().getCurrentPosition(), false);

        for (int i = 0; i < playDataList.size(); i++) {
            if (playDataList.get(i).getIndex() == playData.getIndex()) {
                playDataList.remove(i);
                break;
            }
        }
        playDataList.add(playData);
        currentVideo.getPlayer().setPlayWhenReady(false);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Media current = medias.get(position);

        RelativeLayout mainView = new RelativeLayout(container.getContext());
        mainView.setTag(TAG + " " + position);
        RelativeLayout pauseLayout = new RelativeLayout(container.getContext());
        pauseLayout.setVisibility(View.GONE);
        pauseLayout.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 200);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        if (current.getContentType().equals(Media.IMAGE)) {

            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            mainView.addView(imageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            Picasso.get().load(current.getContentLink()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d("loglog", "onSuccess: image Loaded");
                }

                @Override
                public void onError(Exception e) {
                    Log.e("loglog", "onError: ", e);
                }
            });

        } else if (current.getContentType().equals(Media.VIDEO)) {

            final PlayerView playerView = new PlayerView(container.getContext());

            Log.d(TAG, "instantiateItem: playerView Created " + position);

            playerView.setId(videoID);

            mainView.addView(playerView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            initVideoView(playerView, current.getContentLink(), position, pauseLayout);
        }

        container.addView(mainView);
        mainView.addView(pauseLayout, params);

        return mainView;
    }

    private void initVideoView(final PlayerView playerView, String contentLink, final int position, final RelativeLayout pauseLayout) {

        Context context = playerView.getContext();
        Uri ssUri = Uri.parse(contentLink);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context,
                        "test"),
                new DefaultBandwidthMeter());

        MediaSource sampleSource =
                new ExtractorMediaSource(ssUri, dataSourceFactory, new DefaultExtractorsFactory(),
                        new Handler(), new ExtractorMediaSource.EventListener() {
                    @Override
                    public void onLoadError(IOException error) {

                    }
                });

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        playerView.setPlayer(player);
        player.prepare(sampleSource);
        prevPosition = position;
        currentVideo = playerView;
        PlayData playData = getPlayData(position);
        if (playData != null) {
            player.seekTo(playData.getCurrentWindow(), playData.getCurrentPosition());
            playData.setCanPlay(true);
        }

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (getPlayData(position).isEmpty()) {
                    player.setPlayWhenReady(true);
                } else {
                    if (getPlayData(position).canPlay()) {
                        player.setPlayWhenReady(true);
                    }
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        if (!hasVideoController) {

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(1000);

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
            fadeOut.setStartOffset(1000);
            fadeOut.setDuration(1000);

            final AnimationSet animation = new AnimationSet(false); //change to false
            animation.addAnimation(fadeIn);
            animation.addAnimation(fadeOut);


            pauseLayout.setVisibility(View.VISIBLE);
            pauseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (player.getVolume() == 0) {
                        player.setVolume(1f);
                        pauseLayout.setBackground(playerView.getContext().getDrawable(R.drawable.volume_on));
                    } else {
                        player.setVolume(0f);
                        pauseLayout.setBackground(playerView.getContext().getDrawable(R.drawable.volume_off));
                    }

                    pauseLayout.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            pauseLayout.setBackgroundColor(Color.TRANSPARENT);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

            playerView.hideController();
            playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
                @Override
                public void onVisibilityChange(int visibility) {
                    if (visibility == View.VISIBLE) {
                        playerView.hideController();
                    }
                }
            });
        }
    }

    private PlayData getPlayData(int index) {
        for (PlayData playData : playDataList) {
            if (index == playData.getIndex()) {
                return playData;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return medias.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        PlayerView viewById = ((View) object).findViewById(videoID);
        if (viewById != null) {
            viewById.getPlayer().release();
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    void resumeVideo(ViewPager pager, int position) {
        View viewWithTag = pager.findViewWithTag(TAG + " " + position);
        if (viewWithTag.findViewById(videoID) != null) {
            PlayerView playerView = viewWithTag.findViewById(videoID);

            playerView.getPlayer().setPlayWhenReady(true);
        }
    }
}