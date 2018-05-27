package in.pannu.harpal.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AddNote extends AppCompatActivity {

    private AddFilesAdapter adapter;
    private ArrayList<String> fileList;
    String locationData = "";
    LocationManager mLocationManager;
    EditText noteTitle,note;
    MaterialBetterSpinner materialDesignSpinner;
    DatabaseHelper dbHelper;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        dbHelper = new DatabaseHelper(this);
        RecyclerView recyclerView =  findViewById(R.id.recyclerView2);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        noteTitle = findViewById(R.id.note);
        note = findViewById(R.id.noteText);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (mLocationManager != null) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000,
                    10, mLocationListener);
        }
        if (mLocationManager != null) {
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation != null) {
                locationData = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
            }
        }
        fileList = new ArrayList<>();
        if (getIntent().getStringExtra("ID") != null) {
            Cursor viewData = dbHelper.viewData(dbHelper.getWritableDatabase(),String.valueOf(getIntent().getStringExtra("ID")));
            viewData.moveToFirst();
            noteTitle.setText(viewData.getString(1));
            note.setText(viewData.getString(2));
            String files = viewData.getString(3);
            if(!files.isEmpty()) {
                fileList = new ArrayList<>(Arrays.asList(files.split(",")));
            }
        }
        adapter = new AddFilesAdapter(this, fileList);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        List<String> categories = new ArrayList<>();
        categories.add("School");
        categories.add("Important");
        categories.add("Shopping");
        categories.add("Education");
        categories.add("Personal");
        categories.add("Travel");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        materialDesignSpinner = findViewById(R.id.spinner3);
        materialDesignSpinner.setAdapter(arrayAdapter);

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            locationData = location.getLatitude() + "," + location.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bm;
            File imageFile = AddFilesAdapter.getTempFile(this);
            Uri selectedImage = Uri.fromFile(imageFile);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(selectedImage.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert ei != null;
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            bm = AddFilesAdapter.getImageResized(this, selectedImage);
            bm = rotate(bm,orientation);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            String fileName = System.currentTimeMillis() + ".png";
            File filePath = new File(cw.getFilesDir(), fileName);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(filePath);
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            File delete = new File(AddFilesAdapter.getTempFile(this).getPath());
            if (delete.exists()) {
               delete.delete();
            }
            fileList.add(filePath.getPath());
            adapter.notifyDataSetChanged();
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {

            InputStream inputStream = null;
            int orientation = 0;
            try{
                if(data.getData() != null) {
                    inputStream = getContentResolver().openInputStream(data.getData());
                    ExifInterface  ei = new ExifInterface(AddFilesAdapter.getRealPathFromUri(this,data.getData()));
                    orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    Log.d("Hz",orientation + "");
                }
            } catch (Exception e) {
               Log.d("Hz",e.getMessage());
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = rotate(bitmap,orientation);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            String fileName = System.currentTimeMillis() + ".png";
            File filePath = new File(cw.getFilesDir(), fileName);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            fileList.add(filePath.getPath());
            adapter.notifyDataSetChanged();
        }
        if (requestCode == 3 && resultCode == RESULT_OK) {
            String fileName = System.currentTimeMillis() + ".mp4";
            File filePath = new File(this.getFilesDir(), fileName);
            adapter.saveFile(Uri.parse(AddFilesAdapter.getTempFile(this).getPath()), filePath);

            File delete = new File(AddFilesAdapter.getTempFile(this).getPath());
            if (delete.exists()) {
                delete.delete();
            }

            fileList.add(filePath.getPath());
            adapter.notifyDataSetChanged();
        }

    }

    private static Bitmap rotate(Bitmap bm,int orientation) {
        Matrix matrix = new Matrix();
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                matrix.postRotate(0);
        }
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.files_menu, menu);
        return true;
    }

    @SuppressLint("InflateParams")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_favourite) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = null;
            if (inflater != null) {
                customView = inflater.inflate(R.layout.dialog,null);
            }
            final PopupWindow mPopupWindow = new PopupWindow(
                    customView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if(Build.VERSION.SDK_INT>=21){
                mPopupWindow.setElevation(5.0f);
            }
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.showAsDropDown(this.getWindow().getDecorView().findViewById(android.R.id.content), 0,5,Gravity.TOP|Gravity.END);

            if (customView != null) {
                customView.findViewById(R.id.cameraImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileMethods.getTempFile(getApplicationContext())));
                        startActivityForResult(intent, 1);
                    }
                });

                customView.findViewById(R.id.audioFromDevice).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        adapter.getAudioFromDevice();
                    }
                });
                customView.findViewById(R.id.galleyImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");
                        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
                        startActivityForResult(chooserIntent, 2);
                    }
                });
                customView.findViewById(R.id.audioRecord).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        adapter.recordDialog();
                    }
                });

                customView.findViewById(R.id.recordVideo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileMethods.getTempFile(getApplicationContext())));  // set the image file name
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
                        startActivityForResult(intent, 3);
                        mPopupWindow.dismiss();
                    }
                });
            }
            return true;
        }else if(id == android.R.id.home){
            this.finish();
        }
        else if(id == R.id.Save){
            saveData();

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {

        String NoteTitle = noteTitle.getText().toString();
        if(NoteTitle.isEmpty()){
            showSettingsDialog("Title is Required");
            return;
        }
        String NoteText = note.getText().toString();
        if(NoteText.isEmpty()){
            showSettingsDialog("Write Something");
            return;
        }
        String Files = TextUtils.join(",", fileList);
        String Tag = materialDesignSpinner.getText().toString();
        if(Tag.isEmpty()){
            showSettingsDialog("Select a Tag");
            return;
        }
        if(locationData.isEmpty()){
            showSettingsDialog("Unable To Get Location");
            return;
        }
        if(getIntent().getStringExtra("ID") != null){
            dbHelper.updateData(NoteTitle, NoteText, Files, getIntent().getStringExtra("ID"), locationData, Tag);
        }else {
            String TimeStamp = String.valueOf(System.currentTimeMillis());
            dbHelper.insertData(NoteTitle, NoteText, Files, TimeStamp, locationData, Tag);
        }
       this.finish();

    }

    @Override
    protected void onDestroy() {
        mLocationManager.removeUpdates(mLocationListener);
        super.onDestroy();
    }

    private void showSettingsDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Incomplete Data");
        builder.setMessage(message);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
