package in.pannu.harpal.notes;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_Name = "Notes";
    private static final int DB_Version = 1;
    private Context Ctx;
    DatabaseHelper(Context context) {
        super(context,DB_Name,null,DB_Version);
        Ctx = context;
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
     sqLiteDatabase.execSQL("CREATE TABLE Notes(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, TITLE TEXT, NOTE TEXT,FILES TEXT,TIMESTAMP TEXT, LOCATION TEXT,TAG TEXT)");
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE Notes");
        onCreate(sqLiteDatabase);
    }

    Cursor viewData(SQLiteDatabase sqLiteDatabase, String ID) {
        String[] projection = {"ID","TITLE","NOTE","FILES" ,"TIMESTAMP" ,"LOCATION" ,"TAG"};
        return sqLiteDatabase.query("Notes",projection,"ID = ?",new String[] {ID},null,null,null);
    }

    ArrayList<NoteDB> pullNotes(SQLiteDatabase db, String Search) {
        Cursor cursor;
        ArrayList<NoteDB> retList = new ArrayList<>();
        String Sort = "";
        SharedPreferences filterData = Ctx.getSharedPreferences("filterData", Context.MODE_PRIVATE);
        int filterSet = filterData.getInt("data",1);
        switch (filterSet){
            case 1:
                Sort = "NOTE ASC";
                break;
            case 2:
                Sort = "NOTE DESC";
                break;
            case 3:
                Sort = "ID DESC";
                break;
            case 4:
                Sort = "ID ASC";
                break;
        }


        String selectQuery = "SELECT * FROM  Notes WHERE NOTE LIKE '" + Search + "%' OR TITLE LIKE '" + Search + "%'  ORDER BY " + Sort;
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            retList.add(new NoteDB(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6)));
        }
        cursor.close();
        return retList;
    }

    ArrayList<NoteDB> sortNotesByTag(SQLiteDatabase db,String Sort) {
        Cursor cursor;
        ArrayList<NoteDB> retList = new ArrayList<>();
        String selectQuery;
        String Order = "ID DESC";
        SharedPreferences filterData = Ctx.getSharedPreferences("filterData", Context.MODE_PRIVATE);
        int filterSet = filterData.getInt("data",1);
        switch (filterSet){
            case 1:
                Order = "NOTE ASC";
                break;
            case 2:
                Order = "NOTE DESC";
                break;
            case 3:
                Order = "ID DESC";
                break;
            case 4:
                Order = "ID ASC";
                break;
        }
        if(Sort.equals("All Notes")) {
            selectQuery = "SELECT * FROM  Notes  ORDER BY " + Order;
        }else{
            selectQuery = "SELECT * FROM  Notes WHERE TAG = '" + Sort + "'  ORDER BY " + Order;
        }
        Log.d("Hz",selectQuery);
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            retList.add(new NoteDB(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6)));
        }
        cursor.close();
        return retList;
    }
    public void deleteNote(SQLiteDatabase sq,String ID) {
             sq.delete("Notes","ID = ?",new String[] {ID});
    }

    public void updateData(String NoteTitle, String NoteText, String Files, String ID, String locationData, String Tag) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        contentValues.put("TITLE",NoteTitle);
        contentValues.put("NOTE",NoteText);
        contentValues.put("FILES",Files);
        contentValues.put("LOCATION",locationData);
        contentValues.put("TAG",Tag);
        sqLiteDatabase.update("Notes",contentValues,"ID = ?",new String[]{ID});
    }


    void insertData(String TITLE,String NOTE, String FILES ,String TIMESTAMP , String LOCATION , String TAG) {
        SQLiteDatabase  sq = this.getWritableDatabase();
        ContentValues noteData = new ContentValues();
        noteData.put("TITLE",TITLE);
        noteData.put("NOTE",NOTE);
        noteData.put("FILES",FILES);
        noteData.put("TIMESTAMP",TIMESTAMP);
        noteData.put("LOCATION",LOCATION);
        noteData.put("TAG",TAG);
        sq.insert("Notes", null, noteData);
    }
}
