package in.pannu.harpal.notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import java.io.File;

import java.util.ArrayList;

public class ViewNoteAdapter extends RecyclerView.Adapter<ViewNoteAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> fileList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }
    ViewNoteAdapter(Context mContext, ArrayList<String> fileList) {
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
                return true;
            }
        });
    }
    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
