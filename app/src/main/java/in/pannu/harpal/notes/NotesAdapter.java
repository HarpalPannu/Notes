package in.pannu.harpal.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 *  Created by Harpal Pannu on 2018-04-13.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private Context Ctx;
    private List<NoteDB> notesData;

    NotesAdapter(Context ctx, List<NoteDB> notesData) {
        Ctx = ctx;
        this.notesData = notesData;
    }

    void reloadData(List<NoteDB> noteDB){
        notesData = noteDB;
        notifyDataSetChanged();
    }
    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(Ctx);
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.noteview, parent, false);
        return new NotesViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final NotesViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.noteTitle.setText(notesData.get(position).getTITLE());
        Date date = new Date(Long.parseLong(notesData.get(position).getTIMESTAMP()));
        SimpleDateFormat jdf = new SimpleDateFormat("MMM hh:mm a", Locale.getDefault());
        jdf.setTimeZone(TimeZone.getDefault());

        String Time = "  Created : " + jdf.format(date);
        ArrayList<String> fileList = new ArrayList<>();
        String files =  notesData.get(position).getFILES();
        if(!files.isEmpty()) {
            fileList = new ArrayList<>(Arrays.asList(files.split(",")));
        }
        if(fileList.size() > 0){
            Time = "Files : " + fileList.size() + Time;
        }
        holder.noteDetails.setText(String.format("Tag : %s  %s", notesData.get(position).getTAG(), Time));
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //deleteData(holder.getAdapterPosition());
                Intent myIntent = new Intent(Ctx, NoteView.class);
                myIntent.putExtra("ID", String.valueOf(notesData.get(position).getID())); //Optional parameters
                Ctx.startActivity(myIntent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return notesData.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitle;
        TextView noteDetails;
        NotesViewHolder(View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteDetails);
            noteDetails = itemView.findViewById(R.id.noteTitle);
        }


    }
}
