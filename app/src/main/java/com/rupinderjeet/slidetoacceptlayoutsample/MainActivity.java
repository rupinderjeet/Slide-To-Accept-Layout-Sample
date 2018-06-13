package com.rupinderjeet.slidetoacceptlayoutsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.rupinderjeet.slidetoacceptlayout.SlideToAcceptLayout;

public class MainActivity extends AppCompatActivity
        implements SlideToAcceptLayout.SlideToAcceptListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlideToAcceptLayout slideToAcceptLayoutView = findViewById(R.id.slide_to_accept_view);
        slideToAcceptLayoutView.setActionListener(this);
//        slideToAcceptLayoutView.setRejectImageTintColor(R.color.black, true);
    }

    @Override
    public void onAccepted() {
        Toast.makeText(this, "onUnlock()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRejected() {
        Toast.makeText(this, "onRejected()", Toast.LENGTH_SHORT).show();
    }
}
