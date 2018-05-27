package in.pannu.harpal.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Objects;

public class AddFilesAdapter extends RecyclerView.Adapter<AddFilesAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> fileList;
    private MediaRecorder mRecorder = null;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }

    AddFilesAdapter(Context mContext, ArrayList<String> fileList) {
        this.mContext = mContext;
        this.fileList = fileList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.files, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
            String file = fileList.get(position);
            File fileObject = new  File(file);
            if(fileObject.exists()){
                String FileExtension = MimeTypeMap.getFileExtensionFromUrl(fileObject.getAbsolutePath());
                switch (FileExtension) {
                    case "png":
                        Bitmap myBitmap = BitmapFactory.decodeFile(fileObject.getAbsolutePath());
                        holder.thumbnail.setImageBitmap(myBitmap);
                        break;
                    case "mp3":
                        holder.thumbnail.setImageDrawable(mContext.getResources().getDrawable(R.drawable.audio));
                        break;
                    case "mp4":
                        holder.thumbnail.setImageDrawable(mContext.getResources().getDrawable(R.drawable.videocard));
                        break;
                }
            }
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String file = fileList.get(holder.getAdapterPosition());
                    String FileExtension = MimeTypeMap.getFileExtensionFromUrl(file);
                switch (FileExtension) {
                    case "png": {
                        Intent myIntent = new Intent(mContext, ImageViewActivity.class);
                        myIntent.putExtra("imageUri", file);
                        mContext.startActivity(myIntent);
                        break;
                    }
                    case "mp3":
                        FileMethods.audioPlayer(mContext, Uri.parse(file));
                        break;
                    case "mp4": {
                        Intent myIntent = new Intent(mContext, VideoPlayer.class);
                        myIntent.putExtra("videoUri", file);

                        mContext.startActivity(myIntent);
                        break;
                    }
                }

                }

        });
        holder.thumbnail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               android.app.AlertDialog.Builder myAlertDialog = new android.app.AlertDialog.Builder(mContext);
                myAlertDialog.setTitle("Delete Item");
                myAlertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                myAlertDialog.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        File delete = new File(Uri.parse(fileList.get(holder.getAdapterPosition())).getPath());
                        if (delete.exists()) {
                            delete.delete();
                        }
                        fileList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    }});
                myAlertDialog.show();
                return true;
            }
        });
    }

    private Bitmap scaleBitmap(Bitmap bm,int maxWidth,int maxHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        Log.v("Pictures", "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            int ratio = width / maxWidth;
            width = maxWidth;
            height = height / ratio;
        } else if (height > width) {
            // portrait
            int ratio = height / maxHeight;
            height = maxHeight;
            width = width / ratio;
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

//    public static void shareMusic(long id, Context context) {
//
//            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//            Uri trackUri = Uri.parse(uri.toString() + "/" + id);
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.putExtra(Intent.EXTRA_STREAM, trackUri);
//            intent.setType("audio/*");
//            context.startActivity(Intent.createChooser(intent, "Hz"));
//        }

     void saveFile(Uri sourceUri, File destination){
        try {
            File source = new File(sourceUri.getPath());
            FileChannel src = new FileInputStream(source).getChannel();
            FileChannel dst = new FileOutputStream(destination).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static File getTempFile(Context context) {
        return new File(context.getExternalCacheDir(), "tempImage");
    }
    static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            i++;
        } while (bm.getWidth() < 500 && i < sampleSizes.length);
        return bm;
    }


    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert fileDescriptor != null;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
    }





    void recordDialog(){
        final boolean[] isStarted = {false};
        final String[] mFileName = new String[1];
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        @SuppressLint("InflateParams") final View mView = LayoutInflater.from(mContext)
                .inflate(R.layout.recordview, null, false);
        final Button btnStop = mView.findViewById(R.id.recordStop);
        final Button btnStart = mView.findViewById(R.id.recordStart);
        final Animation anim = AnimationUtils.loadAnimation(mContext,R.anim.scale);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mFileName[0] = mContext.getFilesDir().getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3";
                mRecorder.setOutputFile(mFileName[0]);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                try {
                    mRecorder.prepare();
                } catch (Exception e) {
                    Log.e("Hz", e.getMessage());
                }
                mRecorder.start();
                isStarted[0] = true;
                btnStart.setEnabled(false);
                mView.setAnimation(anim);
                btnStop.setText("Stop");
            }
        });
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                anim.cancel();
                if(mRecorder != null) {
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                }
                if(isStarted[0]){
                    fileList.add(mFileName[0]);
                    notifyDataSetChanged();

                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anim.cancel();
                if(mRecorder != null) {
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                }
                if(isStarted[0]){
                    fileList.add(mFileName[0]);
                    notifyDataSetChanged();
                }

                dialog.dismiss();
            }
        });

    }

    @SuppressLint("Recycle")
    public  void getAudioFromDevice() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        @SuppressLint("InflateParams") final View list = LayoutInflater.from(mContext).inflate(R.layout.audiolist, null, false);
        final ListView listView = list.findViewById(R.id.audioList);
        final SearchView listSearch = list.findViewById(R.id.listSearch);
        Cursor audioCursor = audioSearch("");
        final DeviceAudioAdapter adapter = new DeviceAudioAdapter(mContext, audioCursor);
        listView.setAdapter(adapter);
        mBuilder.setView(list);
        final AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = adapterView.getAdapter().getItem(i);
                Cursor value = (Cursor) obj;
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Uri trackUri = Uri.parse(uri.toString() + "/" + Long.parseLong(value.getString(0)));
                Log.w("Hz", getRealPathFromUri(mContext, trackUri));
                String fileName = System.currentTimeMillis() + ".mp3";
                File mypath = new File(mContext.getFilesDir(), fileName);
                saveFile(Uri.parse(getRealPathFromUri(mContext, trackUri)), mypath);
                Log.d("Hz","FileName" + fileName);
                fileList.add(mypath.getAbsolutePath());
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        listSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                final DeviceAudioAdapter tempAdapter = new DeviceAudioAdapter(mContext, audioSearch(s));
                listView.setAdapter(tempAdapter);
                return false;
            }
        });
    }

    private Cursor audioSearch(String searchText){
        Cursor audioCursor = null;
        String[] projection = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME};
        String selection = MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ?";

        String[] selectionArgs = new String[]{"%"+searchText+"%"};
        try {
            audioCursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, MediaStore.Audio.Media.DISPLAY_NAME + " ASC");
            assert audioCursor != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return audioCursor;
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
