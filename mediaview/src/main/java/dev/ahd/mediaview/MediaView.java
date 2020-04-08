package dev.ahd.mediaview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class MediaView extends RelativeLayout {

    private List<Media> medias;

    private ViewPager pager;
    private TextView count;
    private boolean hasVideoController = true;

    public MediaView(Context context) {
        super(context);
        init();
    }

    public MediaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MediaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setData(List<Media> medias) {
        this.medias = medias;
        init();
    }

    private void init() {
        if (medias != null) {
            if (pager == null) {
                pager = new ViewPager(getContext());
                addView(pager, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                initCountText(this, pager.getCurrentItem() + 1 + "/" + medias.size());

                final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(medias, hasVideoController);
                pager.setAdapter(viewPagerAdapter);

                pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        viewPagerAdapter.addOrUpdatePlayData();

                        viewPagerAdapter.resumeVideo(pager, position);

                        initCountText(MediaView.this, position + 1 + "/" + medias.size());
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
    }

    private void initCountText(RelativeLayout layout, String text) {
        if (count == null) {
            count = new TextView(layout.getContext());
            count.setText(text);
            count.setTextSize(15f);
            count.setGravity(View.TEXT_ALIGNMENT_CENTER);
            count.setBackground(makeBackGround());
            count.setTextColor(Color.WHITE);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.topMargin = count.getContext().getResources().getDisplayMetrics().widthPixels / 35;
            layoutParams.rightMargin = count.getContext().getResources().getDisplayMetrics().heightPixels / 35;

            int padding = count.getContext().getResources().getDisplayMetrics().widthPixels / 35;
            count.setPadding(padding, padding / 2, padding, padding / 2);

            layout.addView(count, layoutParams);
        } else {
            count.setText(text);
        }
    }

    private Drawable makeBackGround() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(55f);
        drawable.setColors(new int[]{Color.BLACK, Color.BLACK, Color.BLACK});
        return drawable;
    }

    public boolean isHasVideoController() {
        return hasVideoController;
    }

    public void setHasVideoController(boolean hasVideoController) {
        if (this.hasVideoController != hasVideoController) {
            init();
        }
        this.hasVideoController = hasVideoController;
    }
}