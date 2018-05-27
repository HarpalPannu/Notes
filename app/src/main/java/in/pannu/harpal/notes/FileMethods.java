package in.pannu.harpal.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import nl.changer.audiowife.AudioWife;

public class FileMethods {
    static File getTempFile(Context context) {
        return new File(context.getExternalCacheDir(), "tempImage");
    }
    static void audioPlayer(Context mContext,final Uri uri){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        @SuppressLint("InflateParams") final View mView = LayoutInflater.from(mContext)
                .inflate(R.layout.audioplayer, null, false);
        final Button btnPlay = mView.findViewById(R.id.btnPlay);
        final Button btnPause = mView.findViewById(R.id.btnPause);
        final SeekBar mMediaSeekBar = mView.findViewById(R.id.seekBar);
        final TextView mTotalTime =  mView.findViewById(R.id.currentTime);
        final TextView mRunTime =  mView.findViewById(R.id.totalTime);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                AudioWife.getInstance().release();
            }
        });
        AudioWife.getInstance()
                .init(mContext, uri)
                .setPlayView(btnPlay)
                .setPauseView(btnPause)
                .setSeekBar(mMediaSeekBar)
                .setRuntimeView(mRunTime)
                .setTotalTimeView(mTotalTime);
        AudioWife.getInstance().play();
        AudioWife.getInstance().addOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

}
