package com.jjc.circular;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    private CircularView circularView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        circularView = (CircularView)findViewById(R.id.ringview);
        circularView.setMax(100);
        circularView.setRate(5.43);
        circularView.setTerm("六个月");
        circularView.setProgress(40);
    }
}
