package in.pannu.harpal.notes;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import java.io.File;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;



public class NoteView extends AppCompatActivity  {

    String Longitude = "";
    String Latitude = "";
    EditText note;
    DatabaseHelper dbHelper;
    AlertDialog.Builder myAlertDialog;
    String ID;
    String Title;
    TextToSpeech textToSpeech;
    Calendar calendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        dbHelper = new DatabaseHelper(this);
        RecyclerView recyclerView =  findViewById(R.id.recyclerViewView);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        textToSpeech = new TextToSpeech(this,null);
        textToSpeech.setLanguage(Locale.UK);
        textToSpeech.setSpeechRate(Float.parseFloat("0.8"));
        note = findViewById(R.id.noteTextView);
        note.setKeyListener(null);
        ArrayList<String> fileList = new ArrayList<>();
        Cursor viewData = dbHelper.viewData(dbHelper.getWritableDatabase(),String.valueOf(ID));
        viewData.moveToFirst();
        if(viewData.getCount() == 0){
            this.finish();
            Toast.makeText(this,"Note Not Found",Toast.LENGTH_LONG).show();
        }else {
            setupView(recyclerView, fileList, viewData);
        }



    }

    private void setupView(RecyclerView recyclerView, ArrayList<String> fileList, Cursor viewData) {
        Title = viewData.getString(1);
        note.setText(viewData.getString(2));
        note.setMovementMethod(new ScrollingMovementMethod());
        String files = viewData.getString(3);
        if(!files.isEmpty()) {
            fileList = new ArrayList<>(Arrays.asList(files.split(",")));
        }
        if(fileList.size() == 0){
            recyclerView.setVisibility(View.GONE);
        }
        ViewNoteAdapter adapter = new ViewNoteAdapter(this, fileList);
        String[] Coordinates = viewData.getString(5).split(",");
        if(Coordinates.length == 2){
            Longitude = Coordinates[1];
            Latitude =  Coordinates[0];
        }
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Delete Note");
        myAlertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final ArrayList<String> finalFileList = fileList;
        myAlertDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(finalFileList.size() > 0){
                    for (String item : finalFileList) {
                        File delete = new File(Uri.parse(item).getPath());
                        if (delete.exists()) {
                            delete.delete();
                        }
                    }
                }
                dbHelper.deleteNote(dbHelper.getWritableDatabase(),String.valueOf(ID));
                finish();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewmenu, menu);
        return true;
    }
    public void startMaps(){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Longitude",Longitude);
        intent.putExtra("Latitude",Latitude);
        intent.putExtra("Title",Title);
        startActivity(intent);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                this.finish();
                break;
            case R.id.mapView :
                startMaps();
                break;
            case R.id.delete:
                myAlertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void editNote(MenuItem item) {
        this.finish();
        Intent myIntent = new Intent(this, AddNote.class);
        myIntent.putExtra("ID",String.valueOf(ID)); //Optional parameters
        startActivity(myIntent);
    }
    public void setReminder(MenuItem item) {
        final android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        @SuppressLint("InflateParams")
        final View mView = LayoutInflater.from(this).inflate(R.layout.timepicker, null, false);
        final TimePicker timePicker = mView.findViewById(R.id.timePicker1);
        final DatePicker datePicker = mView.findViewById(R.id.pickDateDate);
        mBuilder.setView(mView);
        final android.support.v7.app.AlertDialog dialog = mBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        final Button dateTimePicker = mView.findViewById(R.id.pickDate);
        dateTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timePicker.getVisibility() == View.VISIBLE){
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);
                    dateTimePicker.setText(R.string.pickTime);

                }else {
                    timePicker.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.GONE);
                    dateTimePicker.setText(R.string.pickTimeBtn);
                }
            }
        });


        mView.findViewById(R.id.set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour =   timePicker.getCurrentHour();
                int minutes = timePicker.getCurrentMinute();
                int seconds = 0;
                calendar.set(Calendar.HOUR_OF_DAY,hour);
                calendar.set(Calendar.MINUTE,minutes);
                calendar.set(Calendar.SECOND,seconds);
                calendar.set(Calendar.YEAR,datePicker.getYear());
                calendar.set(Calendar.MONTH,datePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH,datePicker.getDayOfMonth());
                Intent notificationIntent = new Intent(getApplicationContext(), NotificationPublisher.class);
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, ID);
                notificationIntent.putExtra("TITLE",Title);
                PendingIntent pendingIntents = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(ID), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                  if (alarmManager != null) {
                      long currentTime = Calendar.getInstance().getTimeInMillis();
                      if(calendar.getTimeInMillis() > currentTime) {
                          alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntents);
                          int mMonth = calendar.get(Calendar.MONTH);
                          int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                          int mHour = calendar.get(Calendar.HOUR);
                          int mMinute = calendar.get(Calendar.MINUTE);
                          int AmPm = calendar.get(Calendar.AM_PM);
                          DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
                          String[] monthNames = symbols.getMonths();
                          String AmPmValue[] = {"AM", "PM"};
                          String toastString = "Reminder set for " + monthNames[mMonth] + " " + mDay + " " + mHour + ":" + mMinute + " " + AmPmValue[AmPm];
                          Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                          dialog.cancel();
                      }else{

                          AnimationSet animSet = new AnimationSet(true);
                          animSet.setAnimationListener(new Animation.AnimationListener() {
                              @Override
                              public void onAnimationStart(Animation animation) {

                              }

                              @Override
                              public void onAnimationEnd(Animation animation) {
                                  mView.clearAnimation();
                              }

                              @Override
                              public void onAnimationRepeat(Animation animation) {

                              }
                          });
                          animSet.setInterpolator(new DecelerateInterpolator());
                          animSet.setFillAfter(true);
                          animSet.setFillEnabled(true);

                          final RotateAnimation animRotate = new RotateAnimation(5f, -5f,
                                  RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                  RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                          final TranslateAnimation translation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF,10, -10);

                          animRotate.setDuration(70);
                          animRotate.setRepeatMode(Animation.REVERSE);
                          animRotate.setRepeatCount(5);
                          animSet.addAnimation(animRotate);
                          animSet.addAnimation(translation);
                          mView.startAnimation(animSet);

                      //    anim.start();
                          Toast.makeText(getApplicationContext(),"Choose Time in Future",Toast.LENGTH_LONG).show();
                      }
                  } else {
                      Toast.makeText(getApplicationContext(), "SomeThing Went Wrong", Toast.LENGTH_LONG).show();
                  }
                }
        });
    }

    @Override
    protected void onDestroy() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public void readNote(MenuItem item) {
        textToSpeech.speak(note.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
    }
}
