package com.linewow.xhyy.heartdemo3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.main_heart_layout)
    HeartLayout mainHeartLayout;
    @InjectView(R.id.main_heart)
    HeartView mainHeart;
    @InjectView(R.id.main_tv)
    TextView mainTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mainHeart.setHeartLayout(mainHeartLayout);
        mainHeart.setOnThumbUp(new OnThumbUp() {
            @Override
            public void like(boolean likeFlag) {

                mainTv.setText("点赞"+likeFlag);
            }
        });
    }

}
