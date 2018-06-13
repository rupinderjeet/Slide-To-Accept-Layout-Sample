package com.rupinderjeet.slidetoacceptlayout;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SlideToAcceptLayout extends LinearLayout
        implements View.OnClickListener, View.OnTouchListener,
        OnSeekBarChangeListener {

    private static final String TAG = SlideToAcceptLayout.class.getSimpleName();

    private TextView titleTextView;
    private TextView subtitleTextView;

    private SeekBar seekbarView;
    private ImageView rejectImageView;

    private int thumbWidth;
    private int acceptanceThreshold = 100;

    private boolean isInvalidMove;

    private SlideToAcceptListener actionListener;

    /*
     * Constructors
     */

    public SlideToAcceptLayout(Context context) {
        super(context);
        init(context, null);
    }

    public SlideToAcceptLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SlideToAcceptLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /*
     * initialize
     */

    @SuppressLint("ClickableViewAccessibility")     // for setOnTouchListener()
    private void init(Context context, AttributeSet attrs) {

        if (isInEditMode()) {
            return;
        }

        setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.sta_layout_height)
        ));

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        setPadding(
                getResources().getDimensionPixelSize(R.dimen.sta_layout_padding_left), 0,
                getResources().getDimensionPixelSize(R.dimen.sta_layout_padding_right), 0
        );

        inflate(context, R.layout.slide_to_accept_layout, this);

        seekbarView = findViewById(R.id.sta_seekbar);
        titleTextView = findViewById(R.id.sta_title_text_view);
        subtitleTextView = findViewById(R.id.sta_subtitle_text_view);

        rejectImageView = findViewById(R.id.sta_reject_image_view);
        rejectImageView.setOnClickListener(this);

        seekbarView.setOnTouchListener(this);
        seekbarView.setOnSeekBarChangeListener(this);

        exploreTypedArray(attrs);
    }

    private void exploreTypedArray(AttributeSet attrs) {

        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.SlideToAcceptLayout
        );

        String title = typedArray.getString(R.styleable.SlideToAcceptLayout_text);
        String subtitle = typedArray.getString(R.styleable.SlideToAcceptLayout_subtitle);

        Drawable thumb = typedArray.getDrawable(R.styleable.SlideToAcceptLayout_drawable_thumb);
        if (thumb == null) {
            thumb = getResources().getDrawable(R.drawable.seekbar_thumb_default);
        }

        Drawable track = typedArray.getDrawable(R.styleable.SlideToAcceptLayout_drawable_track);
        if (track == null) {
            track = getResources().getDrawable(R.drawable.seekbar_track_default);
        }

        typedArray.recycle();

        // Set values now

        setSlideThumb(thumb);
        setSlideTrack(track);

        // title : depends upon slide thumb being set first
        setTitle(title);
        setSubtitle(subtitle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /*
     * Getters for Values
     */

    public int getSlideProgress() {

        if (seekbarView != null) {
            return seekbarView.getProgress();
        }

        return 0;
    }

    public String getTitle() {

        if (titleTextView != null) {
            return titleTextView.getText().toString();
        }

        return null;
    }

    public String getSubtitle() {

        if (subtitleTextView != null) {
            return subtitleTextView.getText().toString();
        }

        return null;
    }

    public Drawable getSlideThumb() {

        if (seekbarView != null) {
            return seekbarView.getThumb();
        }

        return null;
    }

    public Drawable getSlideTrack() {

        if (seekbarView != null) {
            return seekbarView.getProgressDrawable();
        }

        return null;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    /*
     * Setters for Values
     */

    public void setSlideProgress(@IntRange(from = 0, to = 100) int progress) {

        if (seekbarView != null) {
            seekbarView.setProgress(progress);
        }
    }

    public void resetSlideProgress() {

        if (seekbarView != null) {
            seekbarView.setProgress(0);
        }
    }

    public void setSlideAcceptanceThreshold(@IntRange(from = 50, to = 100) int acceptanceThreshold) {
        this.acceptanceThreshold = acceptanceThreshold;
    }

    public void setSlideThumb(Drawable drawable) {

        if (seekbarView != null) {
            seekbarView.setThumb(drawable);
            seekbarView.setThumbOffset(0);
            thumbWidth = drawable.getIntrinsicWidth();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setSlideThumbColor (int resolvedColorOrColorRes, boolean isResource) {

        int resolvedColor = 0;

        if (isResource) {
            resolvedColor = ContextCompat.getColor(getContext(), resolvedColorOrColorRes);
        }

        if (seekbarView != null) {
            seekbarView.setThumbTintMode(PorterDuff.Mode.SRC_ATOP);
            seekbarView.setThumbTintList(ColorStateList.valueOf(resolvedColor));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setSlideTrackColor (int resolvedColorOrColorRes, boolean isResource) {

        int resolvedColor = 0;

        if (isResource) {
            resolvedColor = ContextCompat.getColor(getContext(), resolvedColorOrColorRes);
        }

        if (seekbarView != null) {
            seekbarView.setProgressTintMode(PorterDuff.Mode.SRC_ATOP);
            seekbarView.setProgressTintList(ColorStateList.valueOf(resolvedColor));
        }
    }

    public void setSlideTrack(Drawable drawable) {

        if (seekbarView != null) {
            seekbarView.setProgressDrawable(drawable);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setRejectImageTintColor (int resolvedColorOrColorResource, boolean isResource) {

        int resolvedColor = 0;

        if (isResource) {
            resolvedColor = ContextCompat.getColor(getContext(), resolvedColorOrColorResource);
        }

        if (rejectImageView != null) {

            rejectImageView.setImageTintMode(PorterDuff.Mode.SRC_ATOP);
            rejectImageView.setImageTintList(ColorStateList.valueOf(resolvedColor));
        }
    }

    public void setTitle(String title) {

        if (titleTextView != null) {
            titleTextView.setText(title);
            titleTextView.setPadding(thumbWidth, 0, 0, 0);
        }
    }

    public void setTitle(@StringRes int titleRes) {

        if (titleTextView != null) {
            titleTextView.setText(titleRes);
            titleTextView.setPadding(thumbWidth, 0, 0, 0);
        }
    }

    public void setSubtitle(String subtitle) {

        if (subtitleTextView != null) {
            subtitleTextView.setText(subtitle);
            subtitleTextView.setPadding(thumbWidth, 0, 0, 0);

            boolean isValidSubtitle = subtitle != null && (subtitle.trim().length() > 0);
            subtitleTextView.setVisibility(isValidSubtitle ? View.VISIBLE : View.GONE);
        }
    }

    public void setSubtitle(@StringRes int subtitleRes) {
        setSubtitle(getContext().getString(subtitleRes));
    }

    public void setActionListener(SlideToAcceptListener listener) {
        this.actionListener = listener;
    }

    /*
     * Getters for Views
     */

    public SeekBar getSeekbarView() {
        return seekbarView;
    }

    public View getRejectImageView() {
        return rejectImageView;
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public TextView getSubtitleTextView() {
        return subtitleTextView;
    }

    /*
     * Setters for Views
     */

    public SlideToAcceptLayout setSeekbarView(SeekBar seekbarView) {
        this.seekbarView = seekbarView;
        return this;
    }

    public SlideToAcceptLayout setRejectImageView(ImageView rejectImageView) {
        this.rejectImageView = rejectImageView;
        return this;
    }

    public SlideToAcceptLayout setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
        return this;
    }

    public SlideToAcceptLayout setSubtitleTextView(TextView subtitleTextView) {
        this.subtitleTextView = subtitleTextView;
        return this;
    }

    public SlideToAcceptLayout setSubtitleViewVisibility(boolean shouldShow) {

        if (subtitleTextView != null) {
            subtitleTextView.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }

        return this;
    }

    /*
     * Listeners
     */

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.sta_reject_image_view) {

            if (actionListener != null) {
                actionListener.onRejected();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (titleTextView != null) {
            titleTextView.setAlpha(1f - progress * 0.02f);
        }

        if (subtitleTextView != null) {
            subtitleTextView.setAlpha(1f - progress * 0.02f);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        if (seekBar != null) {

            if (seekBar.getProgress() < acceptanceThreshold) {

                ObjectAnimator anim = ObjectAnimator.ofInt(seekBar, "progress", 0);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                anim.start();
            } else {
                if (actionListener != null) {
                    actionListener.onAccepted();
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                Log.d(TAG, "ThumbWidth: " + thumbWidth);
                return isInvalidMove = event.getX() > thumbWidth;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                return isInvalidMove;
        }

        return super.onTouchEvent(event);
    }

    public interface SlideToAcceptListener {

        /**
         * Called when unlock event occurred.
         */
        void onAccepted();

        /**
         * Called when rejected.
         */
        void onRejected();
    }
}