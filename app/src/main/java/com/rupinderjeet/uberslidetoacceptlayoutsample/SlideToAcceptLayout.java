package com.rupinderjeet.uberslidetoacceptlayoutsample;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SlideToAcceptLayout extends LinearLayout {

    public interface SlideToAcceptListener {

        /**
         * Called when unlock event occurred.
         */
        void onUnlock();

        /**
         * Called when unlock event occurred.
         */
        void onRejected();
    }

    private SlideToAcceptListener listener;

    private SeekBar seekbarView;
    private TextView labelTextView;
    private View rejectView;
    private int thumbWidth;

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

    /**
     * Resets slider to initial state.
     */
    public void reset() {
        seekbarView.setProgress(0);
    }

    public void setActionListener(SlideToAcceptListener listener) {
        this.listener = listener;
    }

    /*
     * init
     */

    private void init(Context context, AttributeSet attrs) {

        if (isInEditMode()) {
            return;
        }

        inflate(context, R.layout.slide_to_accept_layout, this);

//        LayoutParams layoutParams = new LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                convertDpToPixel(100)
//        );
//        setLayoutParams(layoutParams);
//
//        setOrientation(HORIZONTAL);
//        setGravity(Gravity.CENTER);
//        setBackgroundColor(0xFFFFFFFF);
//        setPadding(convertDpToPixel(8), 0, convertDpToPixel(8), 0);

        seekbarView = findViewById(R.id.slide_to_accept_seekbar);
        labelTextView = findViewById(R.id.slide_to_accept_label);

        rejectView = findViewById(R.id.slide_to_accept_reject_image);
        rejectView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRejected();
                }
            }
        });

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SlideToAcceptLayout);
        String text = attributes.getString(R.styleable.SlideToAcceptLayout_text);
        Drawable thumb = attributes.getDrawable(R.styleable.SlideToAcceptLayout_drawable_thumb);
        if (thumb == null) {
            thumb = getResources().getDrawable(R.drawable.seekbar_thumb_custom);
        }
        attributes.recycle();

        thumbWidth = thumb.getIntrinsicWidth();

        if (text != null) {
            labelTextView.setText(text);
        }
        labelTextView.setPadding(thumbWidth, 0, 0, 0);

        seekbarView.setOnTouchListener(new OnTouchListener() {
            private boolean isInvalidMove;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return isInvalidMove = motionEvent.getX() > thumbWidth;
                    case MotionEvent.ACTION_MOVE:
                        return isInvalidMove;
                    case MotionEvent.ACTION_UP:
                        return isInvalidMove;
                }
                return false;
            }
        });

        seekbarView.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                labelTextView.setAlpha(1f - progress * 0.02f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (seekBar.getProgress() < 100) {
                    ObjectAnimator anim = ObjectAnimator.ofInt(seekBar, "progress", 0);
                    anim.setInterpolator(new AccelerateDecelerateInterpolator());
                    anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                    anim.start();
                }
                else {
                    if (listener != null) {
                        listener.onUnlock();
                    }
                }
            }
        });
    }

    public int convertDpToPixel(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT));
    }
}