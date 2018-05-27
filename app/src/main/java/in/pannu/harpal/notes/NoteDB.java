package in.pannu.harpal.notes;

/**
 *  Created by Harpal Pannu on 2018-04-13.
 */

public class NoteDB {

    private int ID;
    private String TITLE ,NOTE , FILES , TIMESTAMP , LOCATION , TAG;

    NoteDB(int ID, String TITLE, String NOTE, String FILES, String TIMESTAMP, String LOCATION, String TAG) {
        this.ID = ID;
        this.TITLE = TITLE;
        this.NOTE = NOTE;
        this.FILES = FILES;
        this.TIMESTAMP = TIMESTAMP;
        this.LOCATION = LOCATION;
        this.TAG = TAG;
    }

    public int getID() {
        return ID;
    }

    public String getTITLE() {
        return TITLE;
    }

    public String getNOTE() {
        return NOTE;
    }

    public String getFILES() {
        return FILES;
    }

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public String getTAG() {
        return TAG;
    }
}
