package in.pannu.harpal.notes;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.github.chrisbanes.photoview.PhotoView;

public class VideoPlayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Intent intent = getIntent();
        String imageUri = intent.getStringExtra("videoUri");
        MediaController mediaController = new MediaController(this);
        VideoView videoPlayer =  findViewById(R.id.videoView);
        Uri imgUri = Uri.parse(imageUri);
        videoPlayer.setVideoURI(imgUri);
        videoPlayer.setMediaController(mediaController);
        videoPlayer.start();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return true;
    }
}
