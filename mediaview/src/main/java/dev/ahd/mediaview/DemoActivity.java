package dev.ahd.mediaview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        MediaView mediaView = findViewById(R.id.media_view_test);

        List<Media> medias = new ArrayList<>();
        medias.add(new Media("https://as6.cdn.asset.aparat.com/aparat-video/7e58408bbc4c45964d72b18da1b8ddb720874142-360p.mp4", Media.VIDEO));
        medias.add(new Media("https://images.pexels.com/photos/36753/flower-purple-lical-blosso.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500", Media.IMAGE));
        medias.add(new Media("https://cardgames.io/solitaire/images/solitaire-logo.png", Media.IMAGE));
        medias.add(new Media("https://as6.cdn.asset.aparat.com/aparat-video/7e58408bbc4c45964d72b18da1b8ddb720874142-360p.mp4", Media.VIDEO));

        mediaView.setHasVideoController(false);
        mediaView.setData(medias);
    }
}
