package in.pannu.harpal.notes;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.chrisbanes.photoview.PhotoView;


public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        Intent intent = getIntent();
        String imageUri = intent.getStringExtra("imageUri");
        PhotoView photoView =  findViewById(R.id.photoView);
        Uri imgUri = Uri.parse(imageUri);
        photoView.setImageURI(null);
        photoView.setImageURI(imgUri);
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
