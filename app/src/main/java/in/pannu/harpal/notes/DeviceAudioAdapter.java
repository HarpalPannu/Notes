package in.pannu.harpal.notes;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class DeviceAudioAdapter extends CursorAdapter {
    private final LayoutInflater mInflater;
    DeviceAudioAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.audiolistview, parent, false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textTitle= view.findViewById(R.id.textView11);
        textTitle.setText(cursor.getString(1));
    }
}